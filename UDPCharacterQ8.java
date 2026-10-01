import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ĐỀ UDP Character - Câu 8:
 * Tách các từ và chọn các từ mà toàn bộ ký tự là nguyên âm a,e,i,o,u; gửi
 * requestId;word1,word2,... . Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ8 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "CD34EF56";

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

            List<String> resultWords = new ArrayList<>();

            for (String word : words) {

                boolean allVowels = true;

                for (char c : word.toLowerCase().toCharArray()) {

                    if ("aeiou".indexOf(c) == -1) {
                        allVowels = false;
                        break;
                    }
                }

                if (allVowels) {
                    resultWords.add(word);
                }
            }

            String output = String.join(",", resultWords);

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