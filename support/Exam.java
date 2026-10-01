package support;

public final class Exam {
    private Exam() {}
    public static String host() { return System.getProperty("exam.host", "36.50.135.242"); }
    public static String student() { return System.getProperty("exam.student", "B23DCCN165"); }
    public static String question(String fallback) { return System.getProperty("exam.qCode", fallback); }
    public static int port(int fallback) { return Integer.getInteger("exam.port", fallback); }
    public static int timeout() { return Integer.getInteger("exam.timeout", 5000); }
    public static String wsdl(String service) {
        return System.getProperty("exam.wsdl", "http://" + host() + ":2221/" + service + "?wsdl");
    }
}
