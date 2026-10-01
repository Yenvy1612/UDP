import java.net.DatagramSocket;

/**
 b. Nhận thông điệp là một chuỗi từ server theo định dạng
 “requestId; n; A1,A2,...An”,
 với: requestId là chuỗi ngẫu nhiên duy nhất;
 n là một số ngẫu nhiên nhỏ hơn 100; dãy A1, A2 ... Am
 với m <= n là các giá trị nguyên liên tiếp, nhỏ hơn hoặc bằng n
 và không trùng nhau. Ví dụ: requestId;10;2,3,5,6,9
 c. Tìm kiếm các giá trị còn thiếu và gửi lên server theo
 định dạng “requestId;B1,B2,...,Bm”. Ví dụ: requestId;1,4,7,8,10

 */
public class UDPDataTypeQ1 {
    private static final int PORT = 2207;
    private static final String Q_CODE = "THAY_QCODE_CAU_1";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = UDPClientSupport.openSocket()) {
            var address = UDPClientSupport.serverAddress();
            UDPClientSupport.send(socket, address, PORT,
                    ";" + UDPClientSupport.STUDENT_CODE + ";" + Q_CODE);

            String[] parts = UDPClientSupport.receive(socket).split(";", 3);
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());
            boolean[] appeared = new boolean[n + 1];
            if (parts.length == 3 && !parts[2].isBlank()) {
                for (String value : parts[2].split(",")) {
                    int number = Integer.parseInt(value.trim());
                    if (number >= 1 && number <= n) {
                        appeared[number] = true;
                    }
                }
            }

            StringBuilder missing = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                if (!appeared[i]) {
                    if (missing.length() > 0) missing.append(',');
                    missing.append(i);
                }
            }
            UDPClientSupport.send(socket, address, PORT, requestId + ";" + missing);
        }
    }
}
