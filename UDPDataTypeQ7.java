import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * ĐỀ UDP Data type - Câu 7:
 * Nhận 50 số nguyên, tìm số lớn thứ hai và số nhỏ thứ hai (không tính trùng
 * lặp), gửi requestId;secondMax,secondMin. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ7 {
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_7";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] request = UDPClientSupport.splitRequest(UDPClientSupport.receive(socket));
            int[] numbers = java.util.Arrays.stream(request[1].split(","))
                    .mapToInt(value -> Integer.parseInt(value.trim())).distinct().sorted().toArray();
            if (numbers.length < 2) throw new IllegalArgumentException("Cần ít nhất 2 giá trị khác nhau");
            int secondMin = numbers[1];
            int secondMax = numbers[numbers.length - 2];
            UDPClientSupport.send(socket, address, PORT,
                    request[0] + ";" + secondMax + "," + secondMin);
        }
    }
}
