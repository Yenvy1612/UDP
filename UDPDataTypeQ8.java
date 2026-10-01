import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
/**
 * ĐỀ UDP Data type - Câu 8:
 * Nhận requestId;n và gửi n số nguyên tố đầu tiên theo dạng
 * requestId;p1,p2,...,pn. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ8 {
    // Hàm kiểm tra số nguyên tố
    private static boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // Hàm lấy danh sách n số nguyên tố đầu tiên
    private static List<Integer> getFirstNPrimes(int n) {
        List<Integer> primes = new ArrayList<>();
        int current = 2;
        while (primes.size() < n) {
            if (isPrime(current)) {
                primes.add(current);
            }
            current++;
        }
        return primes;
    }

    public static void main(String[] args) {
        // Cấu hình địa chỉ IP và Port của Server (Thay đổi IP server thực tế khi chấm bài)
        String serverIp = "127.0.0.1"; // Ví dụ: "203.162.10.109"
        int serverPort = 807;

        // Thông tin cá nhân (Thay đổi theo mã sinh viên và mã câu hỏi của bạn)
        String studentCode = "B15DCCN009";
        String qCode = "F3E8B2D4";

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

            // b. Nhận thông điệp từ server: "requestId;n"
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            System.out.println("[Nhận từ server] " + response);

            // Tách requestId và số n
            String[] parts = response.split(";");
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());

            // c. Tính toán danh sách n số nguyên tố đầu tiên
            List<Integer> primes = getFirstNPrimes(n);

            // Ghép các số nguyên tố thành chuỗi cách nhau bởi dấu phẩy
            StringBuilder primesStr = new StringBuilder();
            for (int i = 0; i < primes.size(); i++) {
                primesStr.append(primes.get(i));
                if (i < primes.size() - 1) {
                    primesStr.append(",");
                }
            }

            // Gửi thông điệp kết quả lên server theo định dạng "requestId;p1,p2,...,pn"
            String resultMessage = requestId + ";" + primesStr.toString();
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