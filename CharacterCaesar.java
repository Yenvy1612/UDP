import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CharacterCaesar {

    public static void main(String[] args){

        String studentCode = "B23DCCN952";
        String qCode = "J5SE2YXc";
        String host = "127.0.0.1";
        int port = 2207;

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String mess = ";" + studentCode + ";" + qCode;
            byte[] messData = mess.getBytes();

            DatagramPacket messPacket = new DatagramPacket(messData, messData.length, serverAddress, port);

            socket.send(messPacket);

            byte[] receiveBytes = new byte[6675];
            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");

            String[] parts = response.split(";");

            String requestId = parts[0];
            String data = parts[1];
            int s = Integer.parseInt(parts[2]);

            StringBuilder res = new StringBuilder();
            res.append(requestId + ";");

            for(int i = 0; i < data.length(); ++i){
                char c = data.charAt(i);
                if('a' <= c && c <= 'z') {
                    res.append((char) (((c - 'a' - s + 26) % 26 + 'a')));
                } else if ('A' <= c && c <= 'Z') {
                    res.append((char) (((c - 'A' - s + 26) % 26 + 'A')));
                }
                else {
                    res.append(c);
                }
            }

            byte[] resultBytes = res.toString().getBytes();

            DatagramPacket resultPacket = new DatagramPacket(resultBytes, resultBytes.length, serverAddress,port);

            socket.send(resultPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
