
import java.net.*;

public class UDPDataType2 {

/*
Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình
client trao đổi thông tin với server theo kịch bản:

a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

Ví dụ: ;B15DCCN001;DC73CA2E

b. Nhận thông điệp là một chuỗi từ server theo định dạng requestId;a1,a2,...,a50

requestId là chuỗi ngẫu nhiên duy nhất
a1 -> a50 là 50 số nguyên ngẫu nhiên
c. Thực hiện tìm giá trị lớn nhất và giá trị nhỏ nhất thông điệp trong a1 -> a50 và gửi thông điệp lên lên server
theo định dạng requestId;max,min

d. Đóng socket và kết thúc chương trình
 */

    public static void main(String[] args) throws Exception {

        String host = "36.50.135.242";
        int port = 2207;

        String studentCode = "B23DCCN952";
        String qCode = "erb8WGbH";

        DatagramSocket socket = new DatagramSocket();

        InetAddress address =
                InetAddress.getByName(host);

        // Gửi mã SV + mã câu hỏi
        String message =
                ";" + studentCode + ";" + qCode;

        byte[] send = message.getBytes();

        socket.send(
                new DatagramPacket(
                        send,
                        send.length,
                        address,
                        port
                )
        );

        // Nhận dữ liệu
        byte[] buffer = new byte[2048];

        DatagramPacket packet =
                new DatagramPacket(
                        buffer,
                        buffer.length
                );

        socket.receive(packet);

        String response =
                new String(
                        packet.getData(),
                        0,
                        packet.getLength()
                );

        System.out.println("Nhan: " + response);

        // requestId;a1,a2,...,a50
        String[] parts = response.split(";", 2);

        String requestId = parts[0];

        String[] numbers =
                parts[1].split(",");

        int max =
                Integer.parseInt(numbers[0].trim());

        int min = max;

        // Tìm max, min
        for (String s : numbers) {

            int value =
                    Integer.parseInt(s.trim());

            if (value > max) {
                max = value;
            }

            if (value < min) {
                min = value;
            }
        }

        // requestId;max,min
        String result =
                requestId + ";" + max + "," + min;

        byte[] resultBytes =
                result.getBytes();

        socket.send(
                new DatagramPacket(
                        resultBytes,
                        resultBytes.length,
                        address,
                        port
                )
        );

        System.out.println("Gui: " + result);

        socket.close();
    }
}
