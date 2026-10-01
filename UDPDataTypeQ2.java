import java.net.DatagramSocket;
import java.util.Arrays;

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
    private static final int PORT = 807;
    private static final String Q_CODE = "THAY_QCODE_CAU_2";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);
            String[] request = UDPClientSupport.splitRequest(UDPClientSupport.receive(socket));
            String[] pieces = request[1].split(",");
            Arrays.sort(pieces, (left, right) -> Integer.compare(position(left), position(right)));
            StringBuilder output = new StringBuilder();
            for (String piece : pieces) {
                output.append(piece.substring(0, piece.lastIndexOf(':')));
            }
            UDPClientSupport.send(socket, address, PORT, request[0] + ";" + output);
        }
    }

    private static int position(String piece) {
        return Integer.parseInt(piece.substring(piece.lastIndexOf(':') + 1).trim());
    }
}
