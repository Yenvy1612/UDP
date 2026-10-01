package UDP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;

/** Hàm dùng chung cho các bài UDP Object: 8 byte requestId + object serialize. */
final class ObjectUdpSupport {
    static final String SERVER_HOST = "36.50.135.242";
    static final String STUDENT_CODE = "B23DCCN952";

    private ObjectUdpSupport() {
    }

    static DatagramSocket openSocket() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(10_000);
        return socket;
    }

    static InetAddress serverAddress() throws Exception {
        return InetAddress.getByName(SERVER_HOST);
    }

    static void sendText(DatagramSocket socket, InetAddress address, int port, String text)
            throws Exception {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(data, data.length, address, port));
    }

    static ReceivedObject receiveObject(DatagramSocket socket) throws Exception {
        byte[] data = new byte[65507];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        int length = packet.getLength();
        if (length <= 8) throw new IllegalArgumentException("Gói UDP không có object hợp lệ");
        byte[] requestId = Arrays.copyOfRange(packet.getData(), 0, 8);
        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(packet.getData(), 8, length - 8))) {
            return new ReceivedObject(requestId, input.readObject());
        }
    }

    static void sendObject(DatagramSocket socket, InetAddress address, int port,
                           byte[] requestId, Object object) throws Exception {
        byte[] objectBytes;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(object);
            output.flush();
            objectBytes = bytes.toByteArray();
        }
        ByteArrayOutputStream packetBytes = new ByteArrayOutputStream(8 + objectBytes.length);
        packetBytes.write(requestId);
        packetBytes.write(objectBytes);
        byte[] data = packetBytes.toByteArray();
        socket.send(new DatagramPacket(data, data.length, address, port));
    }

    static String titleCase(String text) {
        if (text == null || text.isBlank()) return text;
        StringBuilder result = new StringBuilder();
        for (String word : text.trim().replaceAll("\\s+", " ").split(" ")) {
            if (result.length() > 0) result.append(' ');
            String lower = word.toLowerCase();
            result.append(Character.toUpperCase(lower.charAt(0)));
            if (lower.length() > 1) result.append(lower.substring(1));
        }
        return result.toString();
    }

    static String normalizeCustomerName(String name) {
        String normalized = titleCase(name);
        if (normalized == null || normalized.isBlank()) return normalized;
        String[] words = normalized.split(" ");
        StringBuilder result = new StringBuilder(words[words.length - 1].toUpperCase());
        result.append(", ");
        for (int i = 0; i < words.length - 1; i++) {
            if (i > 0) result.append(' ');
            result.append(words[i]);
        }
        return result.toString();
    }

    static String customerUserName(String name) {
        String plain = removeVietnameseAccent(name).toLowerCase().trim().replaceAll("\\s+", " ");
        if (plain.isEmpty()) return "";
        String[] words = plain.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) result.append(words[i].charAt(0));
        result.append(words[words.length - 1]);
        return result.toString();
    }

    static String removeVietnameseAccent(String text) {
        String result = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return result.replace('đ', 'd').replace('Đ', 'D');
    }

    static String convertDate(String date, String fromSeparator, String toSeparator) {
        String[] parts = date.split(java.util.regex.Pattern.quote(fromSeparator));
        if (parts.length != 3) return date;
        return parts[1] + toSeparator + parts[0] + toSeparator + parts[2];
    }

    static int digitSum(String text) {
        int sum = 0;
        for (char c : text.toCharArray()) if (Character.isDigit(c)) sum += c - '0';
        return sum;
    }

    static String reverseDigits(int value) {
        String sign = value < 0 ? "-" : "";
        String digits = Integer.toString(Math.abs(value));
        return sign + new StringBuilder(digits).reverse();
    }

    static final class ReceivedObject {
        final byte[] requestId;
        final Object object;

        ReceivedObject(byte[] requestId, Object object) {
            this.requestId = requestId;
            this.object = object;
        }
    }
}
