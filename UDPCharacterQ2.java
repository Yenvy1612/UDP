import java.math.BigInteger;

/**
 * ĐỀ UDP Character - Câu 2:
 * Nhận hai số nhị phân b1,b2, tính tổng và gửi tổng ở dạng thập phân theo
 * định dạng requestId;sum. Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ2 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_2";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, data -> {
            String[] values = data.split(",", 2);
            BigInteger sum = UDPClientSupport.binary(values[0]).add(UDPClientSupport.binary(values[1]));
            return sum.toString();
        }));
    }
}
