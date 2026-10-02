import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPObject1 {
    public static void main(String[] args){
        String studentCode = "B23DCCN952";
        String host = "36.50.135.242";
        String qCode = "YXhEOFke";
        int port = 2209;

        try {

            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serAddress = InetAddress.getByName(host);
            String mess = ";" + studentCode + ";" + qCode;

            byte[] messData = mess.getBytes();
            DatagramPacket messPacket = new DatagramPacket(
                    messData,
                    messData.length,
                    serAddress,
                    port);

            socket.send(messPacket);

            byte[] revc = new byte[66557];
            DatagramPacket revcPacket = new DatagramPacket(revc, revc.length);

            socket.receive(revcPacket);

            int lenght = revcPacket.getLength();

            byte[] requestIdBytes = Arrays.copyOfRange(revc,0,8);
            String requestId = new String(requestIdBytes, "UTF-8");


        } catch (Exception e) {
        }
    }
}
