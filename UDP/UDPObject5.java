package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 5:
 * Nhận Book; chuẩn hóa title, author theo dạng Họ, Tên, ISBN thành
 * 978-3-16-148410-0 và publishDate yyyy-mm-dd thành mm/yyyy; gửi lại object.
 * Mã sinh viên: B23DCCN952.
 */
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPObject5 {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 809;

        String studentCode = "B23DCCN952";
        String qCode = "eQkvAeId";

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            InetAddress serverAddress = InetAddress.getByName(host);

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData =
                    message.getBytes(StandardCharsets.UTF_8);

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
                    new ByteArrayInputStream(
                            data,
                            8,
                            length - 8
                    );

            ObjectInputStream ois =
                    new ObjectInputStream(bis);

            Book book = (Book) ois.readObject();

            System.out.println("Truoc: " + book);

            String[] titleWords =
                    book.getTitle()
                            .trim()
                            .toLowerCase()
                            .split("\\s+");

            StringBuilder newTitle = new StringBuilder();

            for (String word : titleWords) {

                if (newTitle.length() > 0) {
                    newTitle.append(" ");
                }

                newTitle.append(
                        Character.toUpperCase(word.charAt(0))
                );

                newTitle.append(word.substring(1));
            }

            book.setTitle(newTitle.toString());

            String[] authorWords =
                    book.getAuthor().trim().split("\\s+");

            StringBuilder authorName = new StringBuilder();

            authorName.append(authorWords[0]).append(",");

            for (int i = 1; i < authorWords.length; i++) {
                authorName.append(" ")
                        .append(authorWords[i]);
            }

            book.setAuthor(authorName.toString());

            String isbn =
                    book.getIsbn().replace("-", "");

            if (isbn.length() == 13) {

                String newIsbn =
                        isbn.substring(0, 3) + "-"
                                + isbn.substring(3, 4) + "-"
                                + isbn.substring(4, 6) + "-"
                                + isbn.substring(6, 12) + "-"
                                + isbn.substring(12);

                book.setIsbn(newIsbn);
            }

            String[] date =
                    book.getPublishDate().split("-");

            String newDate =
                    date[1] + "/" + date[0];

            book.setPublishDate(newDate);

            System.out.println("Sau: " + book);

            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();

            ObjectOutputStream oos =
                    new ObjectOutputStream(bos);

            oos.writeObject(book);
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