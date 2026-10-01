/**
 * ĐỀ UDP Character - Câu 4:
 * Chuẩn hóa requestId;data: chữ cái đầu mỗi từ viết hoa, các chữ còn lại
 * viết thường; gửi lại requestId;data. Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ4 {
    private static final int PORT = 808;
    private static final String Q_CODE = "heSH0W1r";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPClientSupport::normalizeWords));
    }
}
