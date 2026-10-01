package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 5:
 * Nhận Book; chuẩn hóa title, author theo dạng Họ, Tên, ISBN thành
 * 978-3-16-148410-0 và publishDate yyyy-mm-dd thành mm/yyyy; gửi lại object.
 * Mã sinh viên: B23DCCN952.
 */
public class UDPObject5 {
    private static final int PORT = 2209;
    private static final String Q_CODE = "THAY_QCODE_CAU_5";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = ObjectUdpSupport.openSocket()) {
            var address = ObjectUdpSupport.serverAddress();
            ObjectUdpSupport.sendText(socket, address, PORT,
                    ";" + ObjectUdpSupport.STUDENT_CODE + ";" + Q_CODE);
            ObjectUdpSupport.ReceivedObject received = ObjectUdpSupport.receiveObject(socket);
            Book book = (Book) received.object;
            book.setTitle(ObjectUdpSupport.titleCase(book.getTitle()));
            book.setAuthor(formatAuthor(book.getAuthor()));
            book.setIsbn(formatIsbn(book.getIsbn()));
            book.setPublishDate(toMonthYear(book.getPublishDate()));
            ObjectUdpSupport.sendObject(socket, address, PORT, received.requestId, book);
        }
    }

    private static String formatAuthor(String author) {
        String normalized = ObjectUdpSupport.titleCase(author);
        String[] words = normalized.split(" ");
        if (words.length < 2) return normalized;
        String givenName = words[words.length - 1];
        StringBuilder familyAndMiddle = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            if (i > 0) familyAndMiddle.append(' ');
            familyAndMiddle.append(words[i]);
        }
        return familyAndMiddle + ", " + givenName;
    }

    private static String formatIsbn(String isbn) {
        String digits = isbn.replaceAll("\\D", "");
        if (digits.length() != 13) return isbn;
        return digits.substring(0, 3) + "-" + digits.substring(3, 4) + "-"
                + digits.substring(4, 6) + "-" + digits.substring(6, 12) + "-" + digits.substring(12);
    }

    private static String toMonthYear(String date) {
        String[] parts = date.split("-");
        return parts.length == 3 ? parts[1] + "/" + parts[0] : date;
    }
}
