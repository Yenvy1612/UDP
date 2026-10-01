import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ĐỀ UDP Character - Câu 8:
 * Tách các từ và chọn các từ mà toàn bộ ký tự là nguyên âm a,e,i,o,u; gửi
 * requestId;word1,word2,... . Mã sinh viên: B23DCCN952.
 */
public class UDPCharacterQ8 {
    private static final int PORT = 808;
    private static final String Q_CODE = "THAY_QCODE_CAU_8";
    private static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u');

    public static void main(String[] args) throws Exception {
        System.out.println(UDPClientSupport.request(Q_CODE, PORT, UDPCharacterQ8::solve));
    }

    private static String solve(String data) {
        if (data.trim().isEmpty()) return "";
        return Arrays.stream(data.trim().split("\\s+"))
                .filter(UDPCharacterQ8::allVowels)
                .collect(Collectors.joining(","));
    }

    private static boolean allVowels(String word) {
        for (char c : word.toLowerCase().toCharArray()) if (!VOWELS.contains(c)) return false;
        return !word.isEmpty();
    }
}
