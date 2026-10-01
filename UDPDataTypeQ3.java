import java.math.BigInteger;
import java.net.DatagramSocket;

/**
 b. Nhận thông điệp là một chuỗi từ server theo định dạng "requestId;num",
 với: requestId là chuỗi ngẫu nhiên duy nhất; num là một số nguyên lớn.
 c. Tính tổng các chữ số trong num và gửi lại tổng này về server theo
 định dạng "requestId;sumDigits".

 */
public class UDPDataTypeQ3 {
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_3";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] request = UDPClientSupport.splitRequest(UDPClientSupport.receive(socket));
            BigInteger number = new BigInteger(request[1].trim());
            int sum = 0;
            for (char c : number.abs().toString().toCharArray()) sum += c - '0';
            UDPClientSupport.send(socket, address, PORT, request[0] + ";" + sum);
        }
    }
}
