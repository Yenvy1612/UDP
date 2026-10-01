/**
 * ĐỀ UDP Character - Câu 9:
 * Nhận các từ phân cách bằng dấu cách, sắp xếp từ điển ngược (z đến a) và
 * gửi requestId;word1,word2,... . Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ9 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_9";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPClientSupport::reverseWords));
    }
}
