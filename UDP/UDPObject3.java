package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 3:
 * Nhận Customer, đổi tên thành LASTNAME_UPPERCASE, Họ Tên; đổi ngày sinh
 * mm-dd-yyyy thành dd/mm/yyyy; tạo username từ chữ đầu họ/tên đệm + tên,
 * rồi gửi lại object. Mã sinh viên: B23DCCN952.
 */
public class UDPObject3 {
    private static final int PORT = 2209;
    private static final String Q_CODE = "gtOG4Xil";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = ObjectUdpSupport.openSocket()) {
            var address = ObjectUdpSupport.serverAddress();
            ObjectUdpSupport.sendText(socket, address, PORT,
                    ";" + ObjectUdpSupport.STUDENT_CODE + ";" + Q_CODE);
            ObjectUdpSupport.ReceivedObject received = ObjectUdpSupport.receiveObject(socket);
            Customer customer = (Customer) received.object;
            String originalName = customer.getName();
            customer.setName(ObjectUdpSupport.normalizeCustomerName(customer.getName()));
            customer.setDayOfBirth(ObjectUdpSupport.convertDate(customer.getDayOfBirth(), "-", "/"));
            customer.setUserName(ObjectUdpSupport.customerUserName(originalName));
            ObjectUdpSupport.sendObject(socket, address, PORT, received.requestId, customer);
        }
    }
}
