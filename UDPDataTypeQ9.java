import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * ĐỀ UDP Data type - Câu 9:
 * Nhận requestId;n;k;z1,...,zn, tìm max của từng cửa sổ trượt kích thước k
 * và gửi requestId;max1,...,maxm. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ9 {
    public static void main(String[] args) {
        // Cấu hình địa chỉ IP và Port của Server (Thay đổi IP server thực tế khi chấm bài)
        String serverIp = "127.0.0.1"; // Ví dụ: "203.162.10.109"
        int serverPort = 807;

        // Thông tin cá nhân (Thay đổi theo mã sinh viên và mã câu hỏi của bạn)
        String studentCode = "B21DCCN795";
        String qCode = "ylrhZ6UM";

        DatagramSocket socket = null;
        try {
            // Khởi tạo UDP socket
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(serverIp);

            // a. Gửi thông điệp ban đầu theo định dạng ";studentCode;qCode"
            String message = ";" + studentCode + ";" + qCode;
            byte[] sendBuffer = message.getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("[Đã gửi] " + message);

            // b. Nhận thông điệp từ server: "requestId;n;k;z1,z2,...,zn"
            byte[] receiveBuffer = new byte[65535];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            System.out.println("[Nhận từ server] " + response);

            // Tách các thành phần từ thông điệp server
            String[] parts = response.split(";");
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());
            int k = Integer.parseInt(parts[2].trim());

            String[] numStrs = parts[3].split(",");
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) {
                nums[i] = Integer.parseInt(numStrs[i].trim());
            }

            // c. Tìm giá trị lớn nhất trong mỗi cửa sổ trượt kích thước k (Sử dụng thuật toán Deque O(n))
            List<Integer> maxValues = new ArrayList<>();
            Deque<Integer> deque = new LinkedList<>();

            for (int i = 0; i < n; i++) {
                // Loại bỏ các chỉ số nằm ngoài cửa sổ hiện tại
                if (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                    deque.pollFirst();
                }

                // Loại bỏ các phần tử nhỏ hơn phần tử hiện tại ở đuôi hàng đợi
                while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                    deque.pollLast();
                }

                // Thêm chỉ số hiện tại vào hàng đợi
                deque.offerLast(i);

                // Lưu giá trị lớn nhất khi cửa sổ đã đủ kích thước k
                if (i >= k - 1) {
                    maxValues.add(nums[deque.peekFirst()]);
                }
            }

            // Ghép kết quả thành chuỗi theo định dạng "requestId;max1,max2,...,maxm"
            StringBuilder resultStr = new StringBuilder();
            for (int i = 0; i < maxValues.size(); i++) {
                resultStr.append(maxValues.get(i));
                if (i < maxValues.size() - 1) {
                    resultStr.append(",");
                }
            }

            String resultMessage = requestId + ";" + resultStr.toString();
            byte[] resultBuffer = resultMessage.getBytes("UTF-8");
            DatagramPacket resultPacket = new DatagramPacket(resultBuffer, resultBuffer.length, serverAddress, serverPort);
            socket.send(resultPacket);
            System.out.println("[Đã gửi kết quả] " + resultMessage);

            // Nhận phản hồi cuối cùng từ server
            socket.receive(receivePacket);
            String finalResponse = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            System.out.println("[Phản hồi cuối] " + finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // d. Đóng socket và kết thúc chương trình
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[Đã đóng socket và kết thúc chương trình]");
            }
        }
    }
}