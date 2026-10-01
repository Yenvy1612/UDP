import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

/**
 * ĐỀ UDP Data type - Câu 10:
 * Nhận requestId;a;b là hai số nguyên lớn, tính tổng và hiệu a-b, gửi
 * requestId;sum;difference. Mã sinh viên: B23DCCN952.
 */
public class UDPDataTypeQ10 {
    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 807;

        String studentCode = "B23DCCN952";
        String qCode = "D3F9A7B8";

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

            String[] parts = response.split(";");

            String requestId = parts[0];

            BigInteger a = new BigInteger(parts[1]);
            BigInteger b = new BigInteger(parts[2]);

            BigInteger sum = a.add(b);
            BigInteger difference = a.subtract(b);

            String result =
                    requestId + ";" + sum + ";" + difference;

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