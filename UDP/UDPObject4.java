package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 4:
 * Nhận Product có name bị đổi từ đầu/cuối và quantity bị đảo chữ số; khôi
 * phục hai thuộc tính rồi gửi lại object với 8 byte requestId ở đầu. Mã SV:
 * B23DCCN952.
 */

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPObject4 {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 809;

        String studentCode = "B23DCCN952";
        String qCode = "EE29C059";

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                    sendData,
                    sendData.length,
                    serverAddress,
                    port
            ));

            byte[] buffer = new byte[4096];

            DatagramPacket receivePacket =
                    new DatagramPacket(buffer, buffer.length);

            socket.receive(receivePacket);

            byte[] data = receivePacket.getData();
            int length = receivePacket.getLength();

            String requestId = new String(
                    data,
                    0,
                    8,
                    StandardCharsets.UTF_8
            );

            ByteArrayInputStream bis =
                    new ByteArrayInputStream(data, 8, length - 8);

            ObjectInputStream ois =
                    new ObjectInputStream(bis);

            Product product = (Product) ois.readObject();

            System.out.println("Truoc: " + product);

            String[] words = product.getName().trim().split("\\s+");

            if (words.length >= 2) {
                String temp = words[0];
                words[0] = words[words.length - 1];
                words[words.length - 1] = temp;
            }

            String newName = String.join(" ", words);

            int quantity = product.getQuantity();

            int reversedQuantity = 0;

            while (quantity > 0) {
                reversedQuantity =
                        reversedQuantity * 10 + quantity % 10;

                quantity /= 10;
            }

            product.setName(newName);
            product.setQuantity(reversedQuantity);

            System.out.println("Sau: " + product);

            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();

            ObjectOutputStream oos =
                    new ObjectOutputStream(bos);

            oos.writeObject(product);
            oos.flush();

            byte[] objectData = bos.toByteArray();
            byte[] requestIdData =
                    requestId.getBytes(StandardCharsets.UTF_8);

            byte[] resultData =
                    new byte[8 + objectData.length];

            System.arraycopy(
                    requestIdData,
                    0,
                    resultData,
                    0,
                    8
            );

            System.arraycopy(
                    objectData,
                    0,
                    resultData,
                    8,
                    objectData.length
            );

            DatagramPacket resultPacket =
                    new DatagramPacket(
                            resultData,
                            resultData.length,
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );

            socket.send(resultPacket);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}