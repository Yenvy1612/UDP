import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
/**
 * ĐỀ UDP Character - Câu 4:
 * Chuẩn hóa requestId;data: chữ cái đầu mỗi từ viết hoa, các chữ còn lại
 * viết thường; gửi lại requestId;data. Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ4 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 808;

        String studentCode = "B23DCCN952";
        String qCode = "5B35BCC1";

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

            String[] words = data.trim().toLowerCase().split("\\s+");

            StringBuilder output = new StringBuilder();

            for (String word : words) {

                if (!word.isEmpty()) {

                    String normalized =
                            Character.toUpperCase(word.charAt(0))
                                    + word.substring(1);

                    if (output.length() > 0) {
                        output.append(" ");
                    }

                    output.append(normalized);
                }
            }

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

            System.out.println("Chuoi ban dau: " + data);
            System.out.println("Chuoi chuan hoa: " + output);
            System.out.println("Ket qua: " + result);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}