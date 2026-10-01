package support;

import java.math.BigDecimal;
import java.util.*;

/** Bộ đọc JSON nhỏ, có xử lý chuỗi escape; không tách JSON bằng dấu phẩy/regex. */
public final class Json {
    private final String text;
    private int pos;
    private Json(String text) { this.text = Objects.requireNonNull(text, "JSON null"); }

    public static Map<String, Object> object(String text) {
        Json parser = new Json(text);
        Object value = parser.value(0);
        parser.space();
        if (parser.pos != text.length()) throw parser.error("Thừa dữ liệu sau JSON");
        if (!(value instanceof Map)) throw parser.error("Cần JSON object");
        @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) value;
        return map;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " tại vị trí " + pos);
    }
    private void space() {
        while (pos < text.length() && " \r\n\t".indexOf(text.charAt(pos)) >= 0) pos++;
    }
    private boolean take(char c) {
        if (pos < text.length() && text.charAt(pos) == c) { pos++; return true; }
        return false;
    }
    private void expect(char c) { if (!take(c)) throw error("Cần ký tự " + c); }
    private Object value(int depth) {
        if (depth > 128) throw error("JSON lồng quá sâu");
        space();
        if (pos >= text.length()) throw error("Thiếu giá trị");
        char c = text.charAt(pos);
        if (c == '"') return string();
        if (take('{')) {
            Map<String, Object> result = new LinkedHashMap<>();
            space();
            if (take('}')) return result;
            do {
                space(); String key = string(); space(); expect(':');
                if (result.containsKey(key)) throw error("Trùng khóa " + key);
                result.put(key, value(depth + 1)); space();
                if (take('}')) return result;
                expect(',');
            } while (true);
        }
        if (take('[')) {
            List<Object> result = new ArrayList<>();
            space();
            if (take(']')) return result;
            do {
                result.add(value(depth + 1)); space();
                if (take(']')) return result;
                expect(',');
            } while (true);
        }
        if (text.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
        if (text.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
        if (text.startsWith("null", pos)) { pos += 4; return null; }
        int start = pos;
        take('-');
        if (!take('0')) digits();
        if (take('.')) digits();
        if (take('e') || take('E')) { if (!take('+')) take('-'); digits(); }
        try { return new BigDecimal(text.substring(start, pos)); }
        catch (NumberFormatException e) { throw error("Giá trị JSON không hợp lệ"); }
    }
    private void digits() {
        int start = pos;
        while (pos < text.length() && text.charAt(pos) >= '0' && text.charAt(pos) <= '9') pos++;
        if (pos == start) throw error("Cần chữ số");
    }
    private String string() {
        expect('"');
        StringBuilder result = new StringBuilder();
        while (pos < text.length()) {
            char c = text.charAt(pos++);
            if (c == '"') return result.toString();
            if (c < 0x20) throw error("Ký tự điều khiển trong chuỗi");
            if (c != '\\') { result.append(c); continue; }
            if (pos >= text.length()) throw error("Thiếu escape");
            switch (text.charAt(pos++)) {
                case '"': result.append('"'); break;
                case '\\': result.append('\\'); break;
                case '/': result.append('/'); break;
                case 'b': result.append('\b'); break;
                case 'f': result.append('\f'); break;
                case 'n': result.append('\n'); break;
                case 'r': result.append('\r'); break;
                case 't': result.append('\t'); break;
                case 'u':
                    if (pos + 4 > text.length()) throw error("Thiếu mã Unicode");
                    int code = 0;
                    for (int i = 0; i < 4; i++) {
                        int n = Character.digit(text.charAt(pos++), 16);
                        if (n < 0) throw error("Mã Unicode không hợp lệ");
                        code = code * 16 + n;
                    }
                    result.append((char) code); break;
                default: throw error("Escape không hợp lệ");
            }
        }
        throw error("Chuỗi chưa đóng dấu nháy");
    }
}
