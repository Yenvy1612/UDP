import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Character7 {
    public static void main(String[] args){
        String studentCode = "B23DCCN952";
        String qCode = "AB12CD34";
        String host = "127.0.0.1";
        int port = 808;

        try {

            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String mess = ";" + studentCode + ";" + qCode;
            byte[] messBytes = mess.getBytes();

            DatagramPacket messPacket = new DatagramPacket(messBytes, messBytes.length, serverAddress, port);

            socket.send(messPacket);

            byte[] receiveBytes = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");

            String[] parts = response.split(";");
            String requestId = parts[0];
            String data = parts[1];
            System.out.println(data);

            Map<Character, Integer> freq = new HashMap<>();
            for(int i = 0; i < data.length(); ++i){

                char c = data.charAt(i);
                if(freq.containsKey(c)){
                    freq.put(c, freq.get(c) + 1);
                }
                else {
                    freq.put(c, 1);
                }
            }

            List<Map.Entry<Character, Integer>> list =
                    new ArrayList<>(freq.entrySet());

            list.sort((a, b) -> {

                if (!a.getValue().equals(b.getValue())) {
                    return b.getValue() - a.getValue();
                }

                if(Character.compare(
                        Character.toLowerCase(a.getKey()),
                        Character.toLowerCase(b.getKey())
                ) == 0){
                    return Character.compare(
                            a.getKey(),
                            b.getKey()
                    );
                }
                return Character.compare(
                        Character.toLowerCase(a.getKey()),
                        Character.toLowerCase(b.getKey())
                );
            });

            StringBuilder result = new StringBuilder();
            result.append(requestId);
            int index = 0;

            for (int i = 0; i < list.size(); i++) {

                if (index > 0) {
                    result.append(",");
                }
                else{
                    index++;
                    result.append(";");
                }

                Map.Entry<Character, Integer> entry =
                        list.get(i);

                result.append(entry.getKey())
                        .append(":")
                        .append(entry.getValue());
            }

            byte[] resultBytes = result.toString().getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(resultBytes, resultBytes.length, serverAddress, port);

            socket.send(sendPacket);
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
