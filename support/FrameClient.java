package support;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.TimeUnit;

/** Frame: int32 big-endian độ dài byte + payload. Không thêm xuống dòng. */
public final class FrameClient implements AutoCloseable {
    private static final int MAX_FRAME = 8 * 1024 * 1024;
    private final SocketChannel channel;
    private final Selector selector;
    private final SelectionKey key;
    private final int timeout;

    public FrameClient(String host, int port, int timeout) throws IOException {
        this.timeout = timeout;
        SocketChannel c = SocketChannel.open();
        Selector s = null;
        try {
            c.socket().connect(new InetSocketAddress(host, port), timeout);
            c.configureBlocking(false);
            s = Selector.open();
            this.key = c.register(s, 0);
            this.channel = c;
            this.selector = s;
        } catch (IOException | RuntimeException e) {
            c.close();
            if (s != null) s.close();
            throw e;
        }
    }

    private long deadline() { return System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout); }
    private void waitReady(int operation, long end) throws IOException {
        while (true) {
            long remaining = end - System.nanoTime();
            if (remaining <= 0) throw new SocketTimeoutException("Hết thời gian đọc/ghi frame.");
            key.interestOps(operation);
            long millis = Math.max(1, TimeUnit.NANOSECONDS.toMillis(remaining));
            int selected = selector.select(millis);
            selector.selectedKeys().clear();
            if (selected > 0) return;
        }
    }

    private void readFully(ByteBuffer buffer, long end) throws IOException {
        while (buffer.hasRemaining()) {
            if (System.nanoTime() >= end) throw new SocketTimeoutException("Hết thời gian đọc frame.");
            int n = channel.read(buffer);
            if (n == -1) throw new EOFException("Server đóng kết nối khi frame chưa đủ byte.");
            if (n == 0) waitReady(SelectionKey.OP_READ, end);
        }
    }

    private void writeFully(ByteBuffer buffer, long end) throws IOException {
        while (buffer.hasRemaining()) {
            if (System.nanoTime() >= end) throw new SocketTimeoutException("Hết thời gian ghi frame.");
            if (channel.write(buffer) == 0) waitReady(SelectionKey.OP_WRITE, end);
        }
    }

    public void send(String text) throws IOException {
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        if (payload.length > MAX_FRAME) throw new IOException("Frame quá lớn.");
        ByteBuffer frame = ByteBuffer.allocate(4 + payload.length).order(ByteOrder.BIG_ENDIAN);
        frame.putInt(payload.length).put(payload).flip();
        writeFully(frame, deadline());
    }

    public String readFragments(int count) throws IOException {
        if (count < 1) throw new IllegalArgumentException("Số frame phải dương.");
        long end = deadline();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int i = 0; i < count; i++) {
            ByteBuffer header = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
            readFully(header, end);
            header.flip();
            int length = header.getInt();
            if (length < 0 || length > MAX_FRAME || bytes.size() > MAX_FRAME - length) {
                throw new IOException("Độ dài frame không hợp lệ: " + length);
            }
            ByteBuffer payload = ByteBuffer.allocate(length);
            readFully(payload, end);
            bytes.write(payload.array());
        }
        // Ghép BYTE trước khi decode, phòng frame cắt ngang một ký tự UTF-8.
        return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes.toByteArray())).toString();
    }

    @Override public void close() throws IOException {
        try { channel.close(); } finally { selector.close(); }
    }
}
