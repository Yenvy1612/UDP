
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 b. Nhận thông điệp là một chuỗi từ server theo định dạng
 "requestId;string", với: requestId là chuỗi ngẫu nhiên duy nhất;
 string là một chuỗi chứa các chuỗi con bị thay đổi vị trí.
 Ví dụ: "veM3xgA1g:4,IPFfgEanY:5,aWXlSzDwe:2,PHupvPc:3,PR3gH8ahN:6,UEEKHLIt:7,M6dpWTE:1"
 c. Xử lý chuỗi xáo trộn và gửi về chuỗi sau khi sắp xếp: "requestId;string".
 Ví dụ chuỗi đã được xử lý:
 "M6dpWTEaWXlSzDwePHupvPcveM3xgA1gIPFfgEanYPR3gH8ahNUEEKHLIt"
 */
public class UDPDataTypeQ2 {

    public static void main(String[] args) {

        String SERVER_IP = "36.50.135.242"; // Thay IP server nếu đề cho IP khác
        int SERVER_PORT = 807;

        String studentCode = "B23DCCN952";
        String qCode = "F3E8B2D4"; // Thay mã câu hỏi thật

        try (DatagramSocket socket = new DatagramSocket()) {

            socket.setSoTimeout(5000);

            InetAddress serverAddress =
                    InetAddress.getByName(SERVER_IP);

            // ==============================
            // BƯỚC A: Gửi studentCode;qCode
            // ==============================

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData =
                    message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket =
                    new DatagramPacket(
                            sendData,
                            sendData.length,
                            serverAddress,
                            SERVER_PORT
                    );

            socket.send(sendPacket);

            System.out.println("Da gui:");
            System.out.println(message);


            // ==============================
            // BƯỚC B: Nhận dữ liệu từ server
            // ==============================

            byte[] buffer = new byte[4096];

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
                            receivePacket.getLength(),
                            StandardCharsets.UTF_8
                    );

            System.out.println("\nServer gui:");
            System.out.println(response);


            // response dạng:
            //
            // requestId;chuoi1:4,chuoi2:2,...
            //
            // Tách requestId và phần dữ liệu

            String[] responseParts = response.split(";", 2);

            if (responseParts.length != 2) {
                System.out.println("Du lieu server khong dung dinh dang!");
                return;
            }

            String requestId = responseParts[0];
            String shuffledString = responseParts[1];

            System.out.println("\nRequest ID:");
            System.out.println(requestId);

            System.out.println("\nChuoi bi xao tron:");
            System.out.println(shuffledString);


            // ===================================
            // BƯỚC C: Sắp xếp các chuỗi theo vị trí
            // ===================================

            /*
             * TreeMap tự động sắp xếp key tăng dần.
             *
             * Ví dụ:
             *
             * 4 -> veM3xgA1g
             * 5 -> IPFfgEanY
             * 2 -> aWXlSzDwe
             *
             * Sau khi đưa vào TreeMap:
             *
             * 1 -> M6dpWTE
             * 2 -> aWXlSzDwe
             * 3 -> PHupvPc
             * ...
             */

            Map<Integer, String> map = new TreeMap<>();

            String[] pieces = shuffledString.split(",");

            for (String piece : pieces) {

                /*
                 * Ví dụ piece:
                 *
                 * veM3xgA1g:4
                 */

                String[] item = piece.split(":");

                if (item.length != 2) {
                    continue;
                }

                String value = item[0];
                int position = Integer.parseInt(item[1]);

                map.put(position, value);
            }


            // Ghép chuỗi theo thứ tự

            StringBuilder result = new StringBuilder();

            for (String value : map.values()) {
                result.append(value);
            }

            String sortedString = result.toString();

            System.out.println("\nChuoi sau khi sap xep:");
            System.out.println(sortedString);


            // ==================================
            // Gửi requestId;sortedString
            // ==================================

            String answer =
                    requestId + ";" + sortedString;

            byte[] answerData =
                    answer.getBytes(StandardCharsets.UTF_8);

            DatagramPacket answerPacket =
                    new DatagramPacket(
                            answerData,
                            answerData.length,
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );

            socket.send(answerPacket);

            System.out.println("\nDa gui ket qua:");
            System.out.println(answer);

            // ==============================
            // BƯỚC D
            // Socket tự đóng do try-with-resources
            // ==============================

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}