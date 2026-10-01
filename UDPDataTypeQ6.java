import java.net.DatagramSocket;

/**
 * ĐỀ UDP Data type - Câu 6:
 * Nhận requestId;strEncode;s và dịch các chữ cái theo độ dịch s để thu được
 * chuỗi cần giải mã, sau đó gửi requestId;strDecode. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ6 {
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_6";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] parts = UDPClientSupport.receive(socket).split(";", 3);
            String decoded = shift(parts[1], Integer.parseInt(parts[2].trim()));
            UDPClientSupport.send(socket, address, PORT, parts[0] + ";" + decoded);
        }
    }

    private static String shift(String text, int shift) {
        StringBuilder result = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                result.append((char) ('a' + Math.floorMod(c - 'a' + shift, 26)));
            } else if (c >= 'A' && c <= 'Z') {
                result.append((char) ('A' + Math.floorMod(c - 'A' + shift, 26)));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
