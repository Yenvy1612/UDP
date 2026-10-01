import java.util.HashSet;
import java.util.Set;

/**
 * ĐỀ UDP Character - Câu 3:
 * Nhận requestId;str1;str2, loại khỏi str1 mọi ký tự xuất hiện trong str2,
 * giữ nguyên thứ tự các ký tự còn lại và gửi requestId;strOutput. Mã SV:
 * B23DCCN952.
 */
public class UDPCharacterQ3 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_3";

    public static void main(String[] args) throws Exception {
        try (var socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] parts = UDPClientSupport.receive(socket).split(";", 3);
            Set<Character> removed = new HashSet<>();
            for (char c : parts[2].toCharArray()) removed.add(c);
            StringBuilder output = new StringBuilder();
            for (char c : parts[1].toCharArray()) if (!removed.contains(c)) output.append(c);
            UDPClientSupport.send(socket, address, PORT, parts[0] + ";" + output);
        }
    }
}
