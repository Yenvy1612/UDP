package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 2:
 * Nhận Employee qua gói 8 byte requestId + object; viết hoa chữ đầu mỗi từ
 * của name, tăng lương theo tổng chữ số năm trong hireDate, đổi ngày
 * yyyy-mm-dd thành dd/mm/yyyy rồi gửi lại object. Mã SV: B23DCCN952.
 */
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPObject2 {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 809;

        String studentCode = "B23DCCN952";
        String qCode = "ITleSdqV";

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

            Employee employee =
                    (Employee) ois.readObject();

            System.out.println("Request ID: " + requestId);
            System.out.println("Employee: " + employee);

            // =====================================
            // XỬ LÝ EMPLOYEE THEO YÊU CẦU MỤC C
            // =====================================

            // Ví dụ:
            // employee.setSalary(...);
            // employee.setName(...);
            // employee.setHireDate(...);


            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();

            ObjectOutputStream oos =
                    new ObjectOutputStream(bos);

            oos.writeObject(employee);
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