import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Hàm dùng chung cho các bài UDP kiểu Data type và Character.
 *
 * Lưu ý: qCode là mã câu hỏi do server cấp cho từng lần làm bài. Các lớp
 * bên dưới để sẵn một giá trị mẫu; hãy thay lại đúng qCode của đề thực tế.
 */
final class UDPClientSupport {
    static final String SERVER_HOST = "36.50.135.242";
    static final String STUDENT_CODE = "B23DCCN952";
    static final int BUFFER_SIZE = 65507;

    private UDPClientSupport() {
    }

    static DatagramSocket openSocket() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(10_000);
        return socket;
    }

    static InetAddress serverAddress() throws Exception {
        return InetAddress.getByName(SERVER_HOST);
    }

    static void send(DatagramSocket socket, InetAddress address, int port, String message)
            throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(data, data.length, address, port));
    }

    static String receive(DatagramSocket socket) throws Exception {
        byte[] data = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    static String[] splitRequest(String response) {
        String[] parts = response.split(";", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Sai định dạng requestId;data: " + response);
        }
        return parts;
    }

    static String request(String qCode, int port, StringProcessor processor) throws Exception {
        try (DatagramSocket socket = openSocket()) {
            InetAddress address = serverAddress();
            send(socket, address, port, ";" + STUDENT_CODE + ";" + qCode);
            String response = receive(socket);
            String[] parts = splitRequest(response);
            String result = parts[0] + ";" + processor.process(parts[1]);
            send(socket, address, port, result);
            return result;
        }
    }

    @FunctionalInterface
    interface StringProcessor {
        String process(String data);
    }

    static String normalizeWords(String data) {
        String trimmed = data.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String word : trimmed.split("\\s+")) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                result.append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    static String reverseWords(String data) {
        String[] words = data.trim().split("\\s+");
        java.util.Arrays.sort(words, java.util.Comparator.reverseOrder());
        return String.join(",", words);
    }

    static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        for (int divisor = 2; (long) divisor * divisor <= number; divisor++) {
            if (number % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    static BigInteger binary(String value) {
        return new BigInteger(value.trim(), 2);
    }
}
