import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
/**
 * ĐỀ UDP Character - Câu 9:
 * Nhận các từ phân cách bằng dấu cách, sắp xếp từ điển ngược (z đến a) và
 * gửi requestId;word1,word2,... . Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ9 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "EF56GH78";

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData =
                    message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    serverAddress,
                    port
            );

            socket.send(sendPacket);

            byte[] buffer = new byte[4096];

            DatagramPacket receivePacket =
                    new DatagramPacket(buffer, buffer.length);

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
            String data = parts[1];

            String[] words = data.trim().split("\\s+");

            Arrays.sort(
                    words,
                    Collections.reverseOrder()
            );

            String output = String.join(",", words);

            String result = requestId + ";" + output;

            byte[] resultData =
                    result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData,
                    resultData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort()
            );

            socket.send(resultPacket);

            System.out.println("Ket qua: " + result);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}