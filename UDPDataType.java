
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương
trình client trao đổi thông tin với server theo kịch bản:

a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

Ví dụ: ;B15DCCN001;73457A17

b. Nhận thông điệp là một chuỗi từ server theo định dạng requestId;n;A1,A2,...An , với

requestId là chuỗi ngẫu nhiên duy nhất
n là một số ngẫu nhiên nhỏ hơn 100.
A1, A2 ... Am (m <= n) là các giá trị ngẫu nhiên nhỏ hơn hoặc bằng n và có thể trùng nhau.
Ví dụ: requestId;10;2,3,5,6,5

c. Tìm kiếm các giá trị còn thiếu và gửi lên server theo định dạng requestId;B1,B2,...,Bm

Ví dụ: requestId;1,4,7,8,9,10

d. Đóng socket và kết thúc chương trình.
 */

public class UDPDataType {

    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            // ==============================
            // 1. THÔNG TIN SERVER
            // ==============================
            String serverHost = "36.50.135.242"; // Thay bằng IP server của đề
            int serverPort = 2207;

            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // Tạo UDP socket
            socket = new DatagramSocket();

            // ==============================
            // 2. GỬI MÃ SV + MÃ CÂU HỎI
            // ==============================
            String studentCode = "B23DCCN952";
            String qCode = "gQ5PAl8r";

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData = message.getBytes("UTF-8");

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    serverAddress,
                    serverPort
            );

            socket.send(sendPacket);

            System.out.println("Da gui:");
            System.out.println(message);

            // ==============================
            // 3. NHẬN DỮ LIỆU TỪ SERVER
            // requestId;n;A1,A2,...,Am
            // ==============================
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(
                    receiveData,
                    receiveData.length
            );

            socket.receive(receivePacket);

            String response = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength(),
                    "UTF-8"
            );

            System.out.println("Server gui:");
            System.out.println(response);

            // ==============================
            // 4. TÁCH DỮ LIỆU
            // ==============================
            String[] parts = response.split(";");

            String requestId = parts[0];

            int n = Integer.parseInt(parts[1]);

            String numberString = parts.length >= 3
                    ? parts[2]
                    : "";

            System.out.println("requestId = " + requestId);
            System.out.println("n = " + n);
            System.out.println("Danh sach = " + numberString);

            // ==============================
            // 5. ĐÁNH DẤU SỐ ĐÃ XUẤT HIỆN
            // ==============================
            boolean[] appeared = new boolean[n + 1];

            if (!numberString.isEmpty()) {

                String[] numbers = numberString.split(",");

                for (String s : numbers) {

                    int value = Integer.parseInt(s.trim());

                    if (value >= 1 && value <= n) {
                        appeared[value] = true;
                    }
                }
            }

            // ==============================
            // 6. TÌM CÁC SỐ CÒN THIẾU
            // ==============================
            StringBuilder missing = new StringBuilder();

            for (int i = 1; i <= n; i++) {

                if (!appeared[i]) {

                    if (missing.length() > 0) {
                        missing.append(",");
                    }

                    missing.append(i);
                }
            }

            System.out.println("Cac gia tri con thieu:");
            System.out.println(missing);

            // ==============================
            // 7. GỬI KẾT QUẢ LÊN SERVER
            // requestId;B1,B2,...,Bm
            // ==============================
            String result = requestId + ";" + missing;

            byte[] resultData = result.getBytes("UTF-8");

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData,
                    resultData.length,
                    serverAddress,
                    serverPort
            );

            socket.send(resultPacket);

            System.out.println("Da gui ket qua:");
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            // ==============================
            // 8. ĐÓNG SOCKET
            // ==============================
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Da dong socket.");
        }
    }
}
