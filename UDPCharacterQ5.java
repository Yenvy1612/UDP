import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;


/**
 * ĐỀ UDP Character - Câu 5:
 * Nhận strInput, bỏ ký tự đặc biệt, chữ số và ký tự trùng; giữ lại các chữ
 * cái theo thứ tự xuất hiện đầu tiên, gửi requestId;strOutput. Mã SV:
 * B23DCCN952.
 */
public class UDPCharacterQ5 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "06D6800D";

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
            String strInput = parts[1];

            Set<Character> seen = new HashSet<>();
            StringBuilder strOutput = new StringBuilder();

            for (char c : strInput.toCharArray()) {

                if (Character.isLetter(c) && !seen.contains(c)) {
                    strOutput.append(c);
                    seen.add(c);
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

            System.out.println("Input: " + strInput);
            System.out.println("Output: " + strOutput);
            System.out.println("Ket qua: " + result);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}