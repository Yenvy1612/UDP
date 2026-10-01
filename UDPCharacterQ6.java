import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ĐỀ UDP Character - Câu 6:
 * Đếm ký tự và gom theo dạng số_lần_ký_tự, theo thứ tự xuất hiện đầu tiên,
 * gửi requestId;processedData. Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ6 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_6";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPCharacterQ6::solve));
    }

    private static String solve(String data) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char c : data.toCharArray()) counts.merge(c, 1, Integer::sum);
        StringBuilder output = new StringBuilder();
        for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
            output.append(entry.getValue()).append(entry.getKey());
        }
        return output.toString();
    }
}
