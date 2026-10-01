import java.math.BigInteger;
import java.net.DatagramSocket;

/**
 * ĐỀ UDP Data type - Câu 10:
 * Nhận requestId;a;b là hai số nguyên lớn, tính tổng và hiệu a-b, gửi
 * requestId;sum;difference. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ10 {
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_10";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] parts = UDPClientSupport.receive(socket).split(";", 3);
            BigInteger a = new BigInteger(parts[1].trim());
            BigInteger b = new BigInteger(parts[2].trim());
            UDPClientSupport.send(socket, address, PORT,
                    parts[0] + ";" + a.add(b) + ";" + a.subtract(b));
        }
    }
}
