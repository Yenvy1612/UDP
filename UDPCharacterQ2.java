import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
/**
 * ĐỀ UDP Character - Câu 2:
 * Nhận hai số nhị phân b1,b2, tính tổng và gửi tổng ở dạng thập phân theo
 * định dạng requestId;sum. Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ2 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "XbYdNZ3";

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    serverAddress,
                    port
            );

            socket.send(sendPacket);

            byte[] buffer = new byte[4096];

            DatagramPacket receivePacket = new DatagramPacket(
                    buffer,
                    buffer.length
            );

            socket.receive(receivePacket);

            String response = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength(),
                    StandardCharsets.UTF_8
            );

            System.out.println("Server: " + response);

            String[] parts = response.split(";", 2);

            String requestId = parts[0];
            String[] binaryNumbers = parts[1].split(",");

            String b1 = binaryNumbers[0];
            String b2 = binaryNumbers[1];

            long number1 = Long.parseLong(b1, 2);
            long number2 = Long.parseLong(b2, 2);

            long sum = number1 + number2;

            String result = requestId + ";" + sum;

            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData,
                    resultData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort()
            );

            socket.send(resultPacket);

            System.out.println("b1 = " + b1 + " -> " + number1);
            System.out.println("b2 = " + b2 + " -> " + number2);
            System.out.println("Tong = " + sum);
            System.out.println("Ket qua: " + result);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
