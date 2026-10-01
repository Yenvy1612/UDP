import java.net.DatagramSocket;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ĐỀ UDP Data type - Câu 9:
 * Nhận requestId;n;k;z1,...,zn, tìm max của từng cửa sổ trượt kích thước k
 * và gửi requestId;max1,...,maxm. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ9 {
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_9";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] parts = UDPClientSupport.receive(socket).split(";", 4);
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());
            int k = Integer.parseInt(parts[2].trim());
            int[] values = java.util.Arrays.stream(parts[3].split(","))
                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
            if (values.length != n || k <= 0 || k > n) throw new IllegalArgumentException("Sai n hoặc k");

            Deque<Integer> deque = new ArrayDeque<>();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < n; i++) {
                while (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.removeFirst();
                while (!deque.isEmpty() && values[deque.peekLast()] <= values[i]) deque.removeLast();
                deque.addLast(i);
                if (i >= k - 1) {
                    if (result.length() > 0) result.append(',');
                    result.append(values[deque.peekFirst()]);
                }
            }
            UDPClientSupport.send(socket, address, PORT, requestId + ";" + result);
        }
    }
}
