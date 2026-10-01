import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ĐỀ UDP Character - Câu 7:
 * Đếm số lần xuất hiện, sắp xếp tần suất giảm dần; nếu bằng nhau thì ký tự
 * đứng trước trong bảng chữ cái trước, gửi char:count cách nhau bằng dấu phẩy.
 * Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ7 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_7";

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPCharacterQ7::solve));
    }

    private static String solve(String data) {
        Map<Character, Integer> counts = new HashMap<>();
        for (char c : data.toCharArray()) counts.merge(c, 1, Integer::sum);
        return counts.entrySet().stream()
                .sorted((a, b) -> {
                    int byCount = Integer.compare(b.getValue(), a.getValue());
                    return byCount != 0 ? byCount : Character.compare(a.getKey(), b.getKey());
                })
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }
}
