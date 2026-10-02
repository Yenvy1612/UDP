package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 3:
 * Nhận Customer, đổi tên thành LASTNAME_UPPERCASE, Họ Tên; đổi ngày sinh
 * mm-dd-yyyy thành dd/mm/yyyy; tạo username từ chữ đầu họ/tên đệm + tên,
 * rồi gửi lại object. Mã sinh viên: B23DCCN952.
 */
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPObject3 {

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

            byte[] sendData =
                    message.getBytes(StandardCharsets.UTF_8);

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

            byte[] data = receivePacket.getData();
            int length = receivePacket.getLength();

            String requestId = new String(
                    data,
                    0,
                    8,
                    StandardCharsets.UTF_8
            );

            ByteArrayInputStream bis =
                    new ByteArrayInputStream(
                            data,
                            8,
                            length - 8
                    );

            ObjectInputStream ois =
                    new ObjectInputStream(bis);

            Customer customer =
                    (Customer) ois.readObject();

            System.out.println("Request ID: " + requestId);
            System.out.println("Customer: " + customer);

            String originalName = customer.getName()
                    .trim()
                    .toLowerCase()
                    .replaceAll("\\s+", " ");

            String[] words = originalName.split(" ");

            StringBuilder newName = new StringBuilder();

            String lastName =
                    words[words.length - 1].toUpperCase();

            newName.append(lastName).append(", ");

            for (int i = 0; i < words.length - 1; i++) {

                String word = words[i];

                newName.append(
                        Character.toUpperCase(word.charAt(0))
                );

                newName.append(word.substring(1));

                if (i < words.length - 2) {
                    newName.append(" ");
                }
            }

            String[] dateParts =
                    customer.getDayOfBirth().split("-");

            String newDate =
                    dateParts[1] + "/"
                            + dateParts[0] + "/"
                            + dateParts[2];

            StringBuilder userName = new StringBuilder();

            for (int i = 0; i < words.length - 1; i++) {
                userName.append(words[i].charAt(0));
            }

            userName.append(words[words.length - 1]);

            customer.setName(newName.toString());
            customer.setDayOfBirth(newDate);
            customer.setUserName(userName.toString());

            System.out.println("Sau khi xu ly:");
            System.out.println(customer);

            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();

            ObjectOutputStream oos =
                    new ObjectOutputStream(bos);

            oos.writeObject(customer);
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