import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 b. Nhận thông điệp là một chuỗi từ server theo định dạng "requestId;num",
 với: requestId là chuỗi ngẫu nhiên duy nhất; num là một số nguyên lớn.
 c. Tính tổng các chữ số trong num và gửi lại tổng này về server theo
 định dạng "requestId;sumDigits".

 */
public class UDPDataTypeQ3 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 807;
        String studentCode = "B23DCCN952";
        String qCode = "A1F3D5B7";

        try {
            DatagramSocket socket = new DatagramSocket();

            // Timeout 5 giây, tránh chờ server vô hạn
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

            String[] parts = response.split(";", 2);

            if (parts.length != 2) {
                System.out.println("Du lieu server khong dung dinh dang!");
                socket.close();
                return;
            }

            String requestId = parts[0];

            // Giữ num dưới dạng String vì đây có thể là số rất lớn
            String num = parts[1];

            System.out.println("\nRequest ID: " + requestId);
            System.out.println("Num: " + num);


            long sumDigits = 0;

            for (int i = 0; i < num.length(); i++) {

                char c = num.charAt(i);

                // Kiểm tra ký tự có phải chữ số 0-9 không
                if (Character.isDigit(c)) {
                    int digit = c - '0';
                    sumDigits += digit;
                }
            }

            System.out.println("\nTong cac chu so:");
            System.out.println(sumDigits);

            String result =
                    requestId + ";" + sumDigits;

            byte[] resultData =
                    result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket =
                    new DatagramPacket(
                            resultData,
                            resultData.length,

                            // Gửi lại đúng server vừa gửi dữ liệu
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );

            socket.send(resultPacket);

            System.out.println("\nDa gui ket qua len server:");
            System.out.println(result);


            socket.close();

            System.out.println("\nDa dong socket.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}