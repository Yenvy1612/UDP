import java.util.HashSet;
import java.util.Set;

/**
 * ĐỀ UDP Character - Câu 5:
 * Nhận strInput, bỏ ký tự đặc biệt, chữ số và ký tự trùng; giữ lại các chữ
 * cái theo thứ tự xuất hiện đầu tiên, gửi requestId;strOutput. Mã SV:
 * B23DCCN952.
 */
public class UDPCharacterQ5 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_5";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPCharacterQ5::solve));
    }

    private static String solve(String data) {
        Set<Character> seen = new HashSet<>();
        StringBuilder output = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (Character.isLetter(c) && seen.add(c)) output.append(c);
        }
        return output.toString();
    }
}
