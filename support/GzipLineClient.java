package support;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;
import java.util.zip.*;

public final class GzipLineClient {
    private GzipLineClient() {}

    // Đọc đến LF trực tiếp trên byte đã giải nén: không đọc đón thêm thông điệp sau.
    // Không dùng InputStreamReader.readLine gián tiếp, vì available() của luồng GZIP
    // có thể báo còn dữ liệu trong khi peer đang chờ client trả lời.
    public static String readUtf8Line(InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        while (true) {
            int b = in.read();
            if (b == -1) {
                if (line.size() == 0) return null;
                throw new EOFException("Dòng GZIP chưa kết thúc bằng LF.");
            }
            if (b == '\n') return new String(line.toByteArray(), StandardCharsets.UTF_8);
            if (line.size() >= 1024 * 1024) throw new IOException("Dòng dữ liệu quá lớn.");
            line.write(b);
        }
    }

    public static void run(String question, UnaryOperator<String> solve) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(Exam.host(), Exam.port(2210)), Exam.timeout());
            socket.setSoTimeout(Exam.timeout());

            // syncFlush=true: flush() đẩy cả dữ liệu đang chờ trong bộ nén.
            try (GZIPOutputStream gzipOut = new GZIPOutputStream(socket.getOutputStream(), true)) {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(gzipOut, StandardCharsets.UTF_8));
                out.write(Exam.student() + ";" + Exam.question(question));
                out.write('\n');
                out.flush();

                // Chỉ tạo luồng đọc sau khi đã gửi yêu cầu, tránh chờ header lẫn nhau.
                GZIPInputStream gzipIn = new GZIPInputStream(socket.getInputStream());
                String data = readUtf8Line(gzipIn);
                if (data == null) throw new EOFException("Server đóng kết nối trước khi gửi dòng dữ liệu.");
                String result = solve.apply(data); // Không trim dữ liệu.
                if (result == null || result.indexOf('\n') >= 0 || result.indexOf('\r') >= 0) {
                    throw new IOException("Kết quả phải là đúng một dòng text.");
                }
                out.write(result);
                out.write('\n');
                out.flush();
                // finish chỉ sau thông điệp cuối, không gọi sau yêu cầu ban đầu.
                gzipOut.finish();
                gzipOut.flush();
                System.out.println("Nhận: " + data);
                System.out.println("Đã gửi: " + result);
            }
        }
    }
}
