package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 2:
 * Nhận Employee qua gói 8 byte requestId + object; viết hoa chữ đầu mỗi từ
 * của name, tăng lương theo tổng chữ số năm trong hireDate, đổi ngày
 * yyyy-mm-dd thành dd/mm/yyyy rồi gửi lại object. Mã SV: B23DCCN952.
 */
public class UDPObject2 {
    private static final int PORT = 2209;
    private static final String Q_CODE = "THAY_QCODE_CAU_2";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = ObjectUdpSupport.openSocket()) {
            var address = ObjectUdpSupport.serverAddress();
            ObjectUdpSupport.sendText(socket, address, PORT,
                    ";" + ObjectUdpSupport.STUDENT_CODE + ";" + Q_CODE);
            ObjectUdpSupport.ReceivedObject received = ObjectUdpSupport.receiveObject(socket);
            Employee employee = (Employee) received.object;
            employee.setName(ObjectUdpSupport.titleCase(employee.getName()));
            String year = employee.getHireDate().substring(0, 4);
            int percent = ObjectUdpSupport.digitSum(year);
            employee.setSalary(employee.getSalary() * (100.0 + percent) / 100.0);
            employee.setHireDate(ObjectUdpSupport.convertDate(employee.getHireDate(), "-", "/"));
            ObjectUdpSupport.sendObject(socket, address, PORT, received.requestId, employee);
        }
    }
}
