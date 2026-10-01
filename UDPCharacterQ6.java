
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * ĐỀ UDP Character - Câu 6:
 * Đếm ký tự và gom theo dạng số_lần_ký_tự, theo thứ tự xuất hiện đầu tiên,
 * gửi requestId;processedData. Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ6 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "9F8C2D3A";

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
            String data = parts[1];

            Map<Character, Integer> count = new LinkedHashMap<>();

            for (char c : data.toCharArray()) {
                count.put(c, count.getOrDefault(c, 0) + 1);
            }

            StringBuilder processedData = new StringBuilder();

            for (Map.Entry<Character, Integer> entry : count.entrySet()) {
                processedData.append(entry.getValue());
                processedData.append(entry.getKey());
            }

            String result = requestId + ";" + processedData;

            byte[] resultData =
                    result.getBytes(StandardCharsets.UTF_8);

            DatagramPacket resultPacket = new DatagramPacket(
                    resultData,
                    resultData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort()
            );

            socket.send(resultPacket);

            System.out.println("Data: " + data);
            System.out.println("Processed: " + processedData);
            System.out.println("Ket qua: " + result);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}