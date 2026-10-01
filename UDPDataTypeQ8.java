import java.net.DatagramSocket;

/**
 * ĐỀ UDP Data type - Câu 8:
 * Nhận requestId;n và gửi n số nguyên tố đầu tiên theo dạng
 * requestId;p1,p2,...,pn. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ8 {
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_8";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] request = UDPClientSupport.splitRequest(UDPClientSupport.receive(socket));
            int n = Integer.parseInt(request[1].trim());
            StringBuilder primes = new StringBuilder();
            for (int candidate = 2, count = 0; count < n; candidate++) {
                if (UDPClientSupport.isPrime(candidate)) {
                    if (primes.length() > 0) primes.append(',');
                    primes.append(candidate);
                    count++;
                }
            }
            UDPClientSupport.send(socket, address, PORT, request[0] + ";" + primes);
        }
    }
}
