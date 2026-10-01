import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ĐỀ UDP Data type - Câu 7:
 * Nhận 50 số nguyên, tìm số lớn thứ hai và số nhỏ thứ hai (không tính trùng
 * lặp), gửi requestId;secondMax,secondMin. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ7 {
    public static void main(String[] args) {
        // Cấu hình địa chỉ IP và Port của Server (Thay đổi IP server thực tế khi chấm bài)
        String serverIp = "127.0.0.1"; // Ví dụ: "203.162.10.109"
        int serverPort = 807;

        // Thông tin cá nhân (Thay đổi theo mã sinh viên và mã câu hỏi của bạn)
        String studentCode = "B15DCCN004";
        String qCode = "99D9F604";

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

            // b. Nhận thông điệp từ server: "requestId;z1,z2,...,z50"
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            System.out.println("[Nhận từ server] " + response);

            // Tách requestId và dãy số
            String[] parts = response.split(";");
            String requestId = parts[0];

            // Chuyển chuỗi các số thành danh sách số nguyên
            List<Integer> numbers = Arrays.stream(parts[1].split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            // c. Tìm số lớn thứ hai và số nhỏ thứ hai (loại bỏ các số trùng lặp)
            List<Integer> uniqueNumbers = numbers.stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            int secondMin, secondMax;
            if (uniqueNumbers.size() >= 2) {
                secondMin = uniqueNumbers.get(1);          // Số nhỏ thứ hai
                secondMax = uniqueNumbers.get(uniqueNumbers.size() - 2); // Số lớn thứ hai
            } else {
                secondMin = uniqueNumbers.get(0);
                secondMax = uniqueNumbers.get(0);
            }

            // Gửi kết quả lên server theo định dạng "requestId;secondMax,secondMin"
            String resultMessage = requestId + ";" + secondMax + "," + secondMin;
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