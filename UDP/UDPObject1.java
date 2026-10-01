package UDP;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;

/*
a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

Ví dụ: ;B15DCCN001;EE29C059

b. Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Student từ server. Trong đó, các thông tin được thiết lập gồm id và name.

c. Yêu cầu:

Chuẩn hóa tên theo quy tắc: Chữ cái đầu tiên in hoa, các chữ cái còn lại in thường và gán lại thuộc tính name của đối tượng
Tạo email ptit.edu.vn từ tên người dùng bằng cách lấy tên và các chữ cái bắt đầu của họ và tên đệm.
Ví dụ: nguyen van tuan nam -> namnvt@ptit.edu.vn. Gán giá trị này cho thuộc tính email của đối tượng nhận được

Gửi thông điệp chứa đối tượng xử lý ở bước c lên Server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Student đã được sửa đổi.
d. Đóng socket và kết thúc chương trình.
 */

// ======================================================
// CLIENT
// ======================================================
public class UDPObject1 {

    private static final String SERVER_IP = "36.50.135.242";
    // Theo mẫu server đang dùng: UDP Object chạy tại cổng 2209.
    private static final int SERVER_PORT = 2209;

    public static void main(String[] args) {

        // ==================================================
        // THAY MÃ SINH VIÊN CỦA BẠN Ở ĐÂY
        // ==================================================
        String studentCode = "B23DCCN952";

        // Mã câu hỏi trên hình
        String qCode = "YXhEOFke";

        DatagramSocket socket = null;

        try {

            socket = new DatagramSocket();

            // Tránh chương trình treo mãi nếu server không phản hồi
            socket.setSoTimeout(5000);

            InetAddress serverAddress =
                    InetAddress.getByName(SERVER_IP);


            // ==================================================
            // BƯỚC A
            // Gửi ;studentCode;qCode
            // ==================================================

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

            System.out.println("Da gui: " + message);


            // ==================================================
            // BƯỚC B
            // Nhận:
            //
            // 8 byte requestId
            // +
            // Object Student
            // ==================================================

            byte[] receiveData = new byte[65507];

            DatagramPacket receivePacket =
                    new DatagramPacket(
                            receiveData,
                            receiveData.length
                    );

            socket.receive(receivePacket);

            int length = receivePacket.getLength();

            System.out.println("So byte nhan duoc: " + length);

            if (length <= 8) {
                throw new IOException(
                        "Du lieu server gui ve khong hop le."
                );
            }


            // ==================================================
            // Lấy 8 byte đầu làm requestId
            // ==================================================

            byte[] requestIdBytes =
                    Arrays.copyOfRange(
                            receiveData,
                            0,
                            8
                    );

            String requestId =
                    new String(
                            requestIdBytes,
                            StandardCharsets.UTF_8
                    );

            System.out.println("requestId: " + requestId);


            // ==================================================
            // Các byte còn lại là Object Student
            // ==================================================

            ByteArrayInputStream bais =
                    new ByteArrayInputStream(
                            receiveData,
                            8,
                            length - 8
                    );

            ObjectInputStream ois =
                    new ObjectInputStream(bais);

            Student student =
                    (Student) ois.readObject();

            System.out.println("\nStudent nhan tu server:");
            System.out.println(student);


            // ==================================================
            // BƯỚC C1
            // Chuẩn hóa tên
            // ==================================================

            String normalizedName =
                    normalizeName(student.getName());

            student.setName(normalizedName);


            // ==================================================
            // BƯỚC C2
            // Tạo email
            // ==================================================

            String email =
                    createEmail(normalizedName);

            student.setEmail(email);


            System.out.println("\nSau khi xu ly:");

            System.out.println(
                    "Name  : " + student.getName()
            );

            System.out.println(
                    "Email : " + student.getEmail()
            );


            // ==================================================
            // BƯỚC C3
            // Serialize Student đã sửa
            // ==================================================

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            ObjectOutputStream oos =
                    new ObjectOutputStream(baos);

            oos.writeObject(student);

            oos.flush();

            byte[] studentBytes =
                    baos.toByteArray();


            // ==================================================
            // Tạo packet:
            //
            // 8 byte requestId
            // +
            // Object Student
            // ==================================================

            ByteArrayOutputStream result =
                    new ByteArrayOutputStream();

            result.write(requestIdBytes);
            result.write(studentBytes);

            byte[] finalData =
                    result.toByteArray();


            // ==================================================
            // Gửi lại server
            // ==================================================

            DatagramPacket finalPacket =
                    new DatagramPacket(
                            finalData,
                            finalData.length,
                            serverAddress,
                            SERVER_PORT
                    );

            socket.send(finalPacket);

            System.out.println(
                    "\nDa gui Student da xu ly len server."
            );

        } catch (SocketTimeoutException e) {

            System.out.println(
                    "Qua thoi gian cho server phan hoi!"
            );

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (socket != null) {
                socket.close();
            }
        }
    }


    // ======================================================
    // Chuẩn hóa tên
    //
    // nguyen VAN    tuan nam
    //
    // ->
    //
    // Nguyen Van Tuan Nam
    // ======================================================

    private static String normalizeName(String name) {

        if (name == null || name.isBlank()) {
            return name;
        }

        name = name.trim()
                .replaceAll("\\s+", " ");

        String[] words = name.split(" ");

        StringBuilder result =
                new StringBuilder();

        for (String word : words) {

            word = word.toLowerCase();

            String normalizedWord =
                    Character.toUpperCase(
                            word.charAt(0)
                    )
                            +
                            word.substring(1);

            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(normalizedWord);
        }

        return result.toString();
    }


    // ======================================================
    // Tạo email
    //
    // Nguyen Van Tuan Nam
    //
    // ->
    //
    // namnvt@ptit.edu.vn
    //
    // Nam
    // +
    // Nguyen -> n
    // Van    -> v
    // Tuan   -> t
    // ======================================================

    private static String createEmail(String name) {

        if (name == null || name.isBlank()) {
            return "";
        }

        String plainName =
                removeVietnameseAccent(name)
                        .toLowerCase()
                        .trim()
                        .replaceAll("\\s+", " ");

        String[] words =
                plainName.split(" ");

        StringBuilder email =
                new StringBuilder();

        // Tên nằm ở cuối
        email.append(
                words[words.length - 1]
        );

        // Chữ cái đầu của họ + tên đệm
        for (int i = 0; i < words.length - 1; i++) {

            if (!words[i].isEmpty()) {

                email.append(
                        words[i].charAt(0)
                );
            }
        }

        email.append("@ptit.edu.vn");

        return email.toString();
    }


    // ======================================================
    // Loại bỏ dấu tiếng Việt khi tạo email
    //
    // Nguyễn -> Nguyen
    // Đặng    -> Dang
    // ======================================================

    private static String removeVietnameseAccent(String str) {

        String temp =
                Normalizer.normalize(
                        str,
                        Normalizer.Form.NFD
                );

        temp =
                temp.replaceAll(
                        "\\p{M}+",
                        ""
                );

        temp =
                temp.replace('đ', 'd')
                        .replace('Đ', 'D');

        return temp;
    }
}
