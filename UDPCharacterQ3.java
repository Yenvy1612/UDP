import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * ĐỀ UDP Character - Câu 3:
 * Nhận requestId;str1;str2, loại khỏi str1 mọi ký tự xuất hiện trong str2,
 * giữ nguyên thứ tự các ký tự còn lại và gửi requestId;strOutput. Mã SV:
 * B23DCCN952.
 */
public class UDPCharacterQ3 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "B34D51E0";

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

            String[] parts = response.split(";", 3);

            String requestId = parts[0];
            String str1 = parts[1];
            String str2 = parts[2];

            StringBuilder strOutput = new StringBuilder();

            for (int i = 0; i < str1.length(); i++) {

                char c = str1.charAt(i);

                if (str2.indexOf(c) == -1) {
                    strOutput.append(c);
                }
            }

            String result = requestId + ";" + strOutput;

            byte[] resultData =
                    result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData,
                    resultData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort()
            );

            socket.send(resultPacket);

            System.out.println("str1: " + str1);
            System.out.println("str2: " + str2);
            System.out.println("Output: " + strOutput);
            System.out.println("Ket qua: " + result);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}