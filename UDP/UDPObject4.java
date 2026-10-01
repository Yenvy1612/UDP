package UDP;

import java.net.DatagramSocket;

/**
 * ĐỀ UDP Object - Câu 4:
 * Nhận Product có name bị đổi từ đầu/cuối và quantity bị đảo chữ số; khôi
 * phục hai thuộc tính rồi gửi lại object với 8 byte requestId ở đầu. Mã SV:
 * B23DCCN952.
 */
public class UDPObject4 {
    private static final int PORT = 809;
    private static final String Q_CODE = "THAY_QCODE_CAU_4";

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = ObjectUdpSupport.openSocket()) {
            var address = ObjectUdpSupport.serverAddress();
            ObjectUdpSupport.sendText(socket, address, PORT,
                    ";" + ObjectUdpSupport.STUDENT_CODE + ";" + Q_CODE);
            ObjectUdpSupport.ReceivedObject received = ObjectUdpSupport.receiveObject(socket);
            Product product = (Product) received.object;
            product.setName(swapFirstAndLastWord(product.getName()));
            product.setQuantity(Integer.parseInt(ObjectUdpSupport.reverseDigits(product.getQuantity())));
            ObjectUdpSupport.sendObject(socket, address, PORT, received.requestId, product);
        }
    }

    private static String swapFirstAndLastWord(String name) {
        String[] words = name.trim().split("\\s+");
        if (words.length < 2) return name;
        String temp = words[0];
        words[0] = words[words.length - 1];
        words[words.length - 1] = temp;
        return String.join(" ", words);
    }
}
