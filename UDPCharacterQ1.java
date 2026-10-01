import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ĐỀ UDP Character - Câu 1:
 * Nhận requestId;data, tìm ký tự xuất hiện nhiều nhất và các vị trí (đánh số
 * từ 1), gửi requestId;character:position1,position2,... . Mã SV: B23DCCN952.
 */
public class UDPCharacterQ1 {
    private static final int PORT = 808;
    private static final String Q_CODE = "IuRWGyUR";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPCharacterQ1::solve));
    }

    private static String solve(String data) {
        if (data.isEmpty()) return ":";
        Map<Character, Integer> count = new LinkedHashMap<>();
        for (char c : data.toCharArray()) count.merge(c, 1, Integer::sum);
        char best = data.charAt(0);
        for (char c : data.toCharArray()) {
            if (count.get(c) > count.get(best)) best = c;
        }
        StringBuilder positions = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == best) positions.append(i + 1).append(',');
        }
        return best + ":" + positions;
    }
}
