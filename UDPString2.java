import java.net.*;

public class UDPString2 {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2208;

        String studentCode = "B23DCCN952"; // sửa đúng mã SV
        String qCode = "heSH0W1r";

        try {
            DatagramSocket socket = new DatagramSocket();

            // Chờ tối đa 5 giây
            socket.setSoTimeout(5000);

            InetAddress address =
                    InetAddress.getByName(host);

            // =============================
            // 1. GỬI REQUEST
            // =============================

            String message =
                    ";" + studentCode + ";" + qCode;

            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket =
                    new DatagramPacket(
                            sendData,
                            sendData.length,
                            address,
                            port
                    );

            System.out.println("Dang gui toi:");
            System.out.println(host + ":" + port);

            System.out.println("Noi dung:");
            System.out.println(message);

            socket.send(sendPacket);

            System.out.println("Da gui thanh cong.");
            System.out.println("Dang cho server response...");

            // =============================
            // 2. NHẬN RESPONSE
            // =============================

            byte[] buffer = new byte[2048];

            DatagramPacket receivePacket =
                    new DatagramPacket(
                            buffer,
                            buffer.length
                    );

            socket.receive(receivePacket);

            String response =
                    new String(
                            receivePacket.getData(),
                            0,
                            receivePacket.getLength()
                    );

            System.out.println("Nhan duoc:");
            System.out.println(response);

            // =============================
            // 3. TÁCH requestId;data
            // =============================

            String[] parts =
                    response.split(";", 2);

            if (parts.length < 2) {
                System.out.println(
                        "Du lieu server khong dung dinh dang!"
                );

                socket.close();
                return;
            }

            String requestId = parts[0];
            String data = parts[1];

            // =============================
            // 4. CHUẨN HÓA
            // =============================

            String[] words =
                    data.trim().split("\\s+");

            StringBuilder sb =
                    new StringBuilder();

            for (String word : words) {

                if (word.isEmpty()) {
                    continue;
                }

                sb.append(
                        Character.toUpperCase(
                                word.charAt(0)
                        )
                );

                if (word.length() > 1) {
                    sb.append(
                            word.substring(1)
                                    .toLowerCase()
                    );
                }

                sb.append(" ");
            }

            String normalized =
                    sb.toString().trim();

            // =============================
            // 5. GỬI RESPONSE
            // =============================

            String result =
                    requestId
                            + ";"
                            + normalized;

            byte[] resultData =
                    result.getBytes();

            DatagramPacket resultPacket =
                    new DatagramPacket(
                            resultData,
                            resultData.length,
                            address,
                            port
                    );

            socket.send(resultPacket);

            System.out.println(
                    "Da gui ket qua:"
            );

            System.out.println(result);

            socket.close();

        } catch (SocketTimeoutException e) {

            System.out.println(
                    "LOI: Server khong phan hoi sau 5 giay!"
            );

            System.out.println(
                    "Kiem tra IP, port, studentCode va qCode."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}