import java.net.DatagramSocket;

/**
 * ĐỀ UDP Data type - Câu 5:
 * Nhận requestId;a1,...,a50, tìm giá trị lớn nhất và nhỏ nhất, gửi
 * requestId;max,min. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ5 {
    private static final int PORT = 807;
    private static final String Q_CODE = "erb8WGbH";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] request = UDPClientSupport.splitRequest(UDPClientSupport.receive(socket));
            String[] values = request[1].split(",");
            int min = Integer.parseInt(values[0].trim());
            int max = min;
            for (String value : values) {
                int number = Integer.parseInt(value.trim());
                min = Math.min(min, number);
                max = Math.max(max, number);
            }
            UDPClientSupport.send(socket, address, PORT, request[0] + ";" + max + "," + min);
        }
    }
}
