import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPString {
/*
Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình
client tương tác với server kịch bản dưới đây:

a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

Ví dụ: ;B15DCCN001;EE29C059

b. Nhận thông điệp từ server theo định dạng requestId; data

requestId là một chuỗi ngẫu nhiên duy nhất
data là chuỗi dữ liệu đầu vào cần xử lý
Ví dụ: requestId;Qnc8d5x78aldSGWWmaAAjyg3

c. Tìm kiếm ký tự xuất hiện nhiều nhất trong chuỗi và gửi lên server theo định dạng “requestId;ký tự xuất hiện
nhiều nhất: các vị trí xuất hiện ký tự đó”

Ví dụ: requestId;8:4,9,

d. Đóng socket và kết thúc chương trình
 */
    public static void main(String[] args) {

        String SERVER_IP = "36.50.135.242";
        int SERVER_PORT = 2208;

        String STUDENT_CODE = "B23DCCN952"; // sửa thành mã sinh viên
        String QCODE = "IuRWGyUR";

        DatagramSocket socket = null;

        try {
            // =========================================
            // 1. TẠO UDP SOCKET
            // =========================================
            socket = new DatagramSocket();

            InetAddress serverAddress =
                    InetAddress.getByName(SERVER_IP);

            // =========================================
            // 2. GỬI ;studentCode;qCode
            // =========================================
            String message =
                    ";" + STUDENT_CODE + ";" + QCODE;

            byte[] sendData = message.getBytes("UTF-8");

            DatagramPacket sendPacket =
                    new DatagramPacket(
                            sendData,
                            sendData.length,
                            serverAddress,
                            SERVER_PORT
                    );

            socket.send(sendPacket);

            System.out.println("Da gui:");
            System.out.println(message);

            // =========================================
            // 3. NHẬN requestId;data
            // =========================================
            byte[] buffer = new byte[2048];

            DatagramPacket receivePacket =
                    new DatagramPacket(
                            buffer,
                            buffer.length
                    );

            socket.receive(receivePacket);

            String response = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength(),
                    "UTF-8"
            );

            System.out.println("Nhan tu server:");
            System.out.println(response);

            // =========================================
            // 4. TÁCH requestId VÀ data
            // =========================================

            // Chỉ split tại dấu ; đầu tiên
            String[] parts = response.split(";", 2);

            String requestId = parts[0];
            String data = parts[1];

            System.out.println("requestId = " + requestId);
            System.out.println("data = " + data);

            // =========================================
            // 5. ĐẾM SỐ LẦN XUẤT HIỆN CỦA TỪNG KÝ TỰ
            // =========================================

            int[] count = new int[65536];

            for (int i = 0; i < data.length(); i++) {

                char c = data.charAt(i);

                count[c]++;
            }

            // =========================================
            // 6. TÌM KÝ TỰ XUẤT HIỆN NHIỀU NHẤT
            // =========================================

            char maxChar = data.charAt(0);

            int maxCount = count[maxChar];

            for (int i = 1; i < data.length(); i++) {

                char c = data.charAt(i);

                if (count[c] > maxCount) {

                    maxCount = count[c];

                    maxChar = c;
                }
            }

            System.out.println(
                    "Ky tu xuat hien nhieu nhat: "
                            + maxChar
            );

            System.out.println(
                    "So lan xuat hien: "
                            + maxCount
            );

            // =========================================
            // 7. TÌM CÁC VỊ TRÍ XUẤT HIỆN
            // =========================================

            StringBuilder positions =
                    new StringBuilder();

            for (int i = 0; i < data.length(); i++) {

                if (data.charAt(i) == maxChar) {

                    // Đề đánh số vị trí bắt đầu từ 1
                    positions.append(i + 1)
                            .append(",");
                }
            }

            System.out.println(
                    "Cac vi tri: " + positions
            );

            // =========================================
            // 8. TẠO KẾT QUẢ
            //
            // requestId;character:position1,position2,...
            // =========================================

            String result =
                    requestId
                            + ";"
                            + maxChar
                            + ":"
                            + positions;

            System.out.println("Ket qua:");
            System.out.println(result);

            // =========================================
            // 9. GỬI KẾT QUẢ LÊN SERVER
            // =========================================

            byte[] resultData =
                    result.getBytes("UTF-8");

            DatagramPacket resultPacket =
                    new DatagramPacket(
                            resultData,
                            resultData.length,
                            serverAddress,
                            SERVER_PORT
                    );

            socket.send(resultPacket);

            System.out.println(
                    "Da gui ket qua len server."
            );

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            // =========================================
            // 10. ĐÓNG SOCKET
            // =========================================

            if (socket != null
                    && !socket.isClosed()) {

                socket.close();
            }

            System.out.println("Da dong socket.");
        }
    }
}