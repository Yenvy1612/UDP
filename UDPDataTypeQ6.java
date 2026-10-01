import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * ĐỀ UDP Data type - Câu 6:
 * Nhận requestId;strEncode;s và dịch các chữ cái theo độ dịch s để thu được
 * chuỗi cần giải mã, sau đó gửi requestId;strDecode. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ6 {
    public static void main(String[] args) {

        // Thông tin server
        String host = "36.50.135.242"; // Thay IP server nếu đề cho IP khác
        int port = 807;

        // Thông tin sinh viên
        String studentCode = "B23DCCN952";
        String qCode = "825EE3A7"; // Thay bằng mã câu hỏi thực tế

        try {
            // ============================================================
            // Tạo UDP Socket để giao tiếp với Server
            // ============================================================
            DatagramSocket socket = new DatagramSocket();

            // Timeout 5 giây để tránh chờ vô hạn nếu server không phản hồi
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData =
                    message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket =
                    new DatagramPacket(
                            sendData,
                            sendData.length,
                            serverAddress,
                            port
                    );

            socket.send(sendPacket);

            System.out.println("Da gui len server:");
            System.out.println(message);


            byte[] buffer = new byte[2048];

            DatagramPacket receivePacket =
                    new DatagramPacket(
                            buffer,
                            buffer.length
                    );

            socket.receive(receivePacket);

            String response =
                    new String(
                            receivePacket.getData(),
                            0,
                            receivePacket.getLength(),
                            StandardCharsets.UTF_8
                    );

            System.out.println("\nServer gui:");
            System.out.println(response);


            String[] parts = response.split(";", 3);

            if (parts.length != 3) {
                System.out.println("Du lieu server khong dung dinh dang!");
                socket.close();
                return;
            }

            String requestId = parts[0];
            String strEncode = parts[1];
            int s = Integer.parseInt(parts[2]);

            System.out.println("\nRequest ID: " + requestId);
            System.out.println("Chuoi ma hoa: " + strEncode);
            System.out.println("Do dich s: " + s);

            StringBuilder strDecode = new StringBuilder();

            for (int i = 0; i < strEncode.length(); i++) {

                char c = strEncode.charAt(i);

                // Nếu là chữ HOA: A - Z
                if (c >= 'A' && c <= 'Z') {

                    char decoded =
                            (char) ('A' + (c - 'A' + s) % 26);

                    strDecode.append(decoded);
                }

                // Nếu là chữ thường: a - z
                else if (c >= 'a' && c <= 'z') {

                    char decoded =
                            (char) ('a' + (c - 'a' + s) % 26);

                    strDecode.append(decoded);
                }

                // Nếu không phải chữ cái thì giữ nguyên
                else {
                    strDecode.append(c);
                }
            }

            System.out.println("\nChuoi sau khi giai ma:");
            System.out.println(strDecode);


            // ============================================================
            // c. Gửi thông điệp đã giải mã lên server theo định dạng:
            //
            // "requestId;strDecode"
            //
            // Ví dụ:
            // abc123;DEFabc
            // ============================================================

            String result =
                    requestId + ";" + strDecode;

            byte[] resultData =
                    result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket =
                    new DatagramPacket(
                            resultData,
                            resultData.length,

                            // Gửi lại đúng địa chỉ server vừa gửi dữ liệu
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );

            socket.send(resultPacket);

            System.out.println("\nDa gui ket qua len server:");
            System.out.println(result);


            // ============================================================
            // d. Đóng socket và kết thúc chương trình
            // ============================================================

            socket.close();

            System.out.println("\nDa dong socket.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}