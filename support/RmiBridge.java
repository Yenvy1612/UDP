package support;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.registry.*;
import java.util.*;

/**
 * Đọc chữ ký từ interface THẬT trong JAR của môn học.
 * Không khai báo bản đoán của RMI.DataService/ObjectService/TicketSla.
 */
public final class RmiBridge {
    private final Remote service;
    private final Class<?> api;
    public RmiBridge(Remote service, Class<?> api) {
        this.service = service; this.api = api;
        if (!api.isInterface() || !Remote.class.isAssignableFrom(api) || !api.isInstance(service)) {
            throw new IllegalArgumentException("Remote object không tương thích interface " + api.getName());
        }
    }
    public static RmiBridge connect(String apiName, String binding) throws Exception {
        Class<?> api;
        try { api = Class.forName(apiName); }
        catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Cần JAR/interface gốc chứa " + apiName
                    + ". Đặt JAR vào lib và chạy với classpath out;lib/*. Không tự tạo interface đoán.", e);
        }
        int port = Integer.getInteger("exam.rmiPort", 1099);
        Registry registry = LocateRegistry.getRegistry(Exam.host(), port);
        return new RmiBridge(registry.lookup(binding), api);
    }
    public Object request(String name, String question) throws Exception {
        Method m = api.getMethod(name, String.class, String.class);
        return invoke(m, Exam.student(), Exam.question(question));
    }
    public void submit(String name, String question, Object data) throws Exception {
        Method chosen = null;
        for (Method m : api.getMethods()) {
            Class<?>[] p = m.getParameterTypes();
            if (!m.getName().equals(name) || p.length != 3
                    || p[0] != String.class || p[1] != String.class || !accepts(p[2], data)) continue;
            if (chosen == null || chosen.getParameterTypes()[2].isAssignableFrom(p[2])) chosen = m;
        }
        if (chosen == null) throw new NoSuchMethodException("Interface thật không có " + name
                + "(String,String,<kiểu phù hợp với " + (data == null ? "null" : data.getClass().getName()) + ">).");
        invoke(chosen, Exam.student(), Exam.question(question), data);
    }
    private Object invoke(Method m, Object... args) throws Exception {
        try { return m.invoke(service, args); }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) throw (Exception)cause;
            throw new RuntimeException(cause);
        }
    }
    private static boolean accepts(Class<?> type, Object value) {
        if (value == null) return !type.isPrimitive();
        return type.isInstance(value) || (type == boolean.class && value instanceof Boolean);
    }
    public static List<Integer> integers(Object data) {
        if (data == null) throw new IllegalArgumentException("Dữ liệu null.");
        List<Integer> result = new ArrayList<>();
        if (data instanceof Iterable<?>) {
            for (Object value : (Iterable<?>)data) result.add(integer(value));
        } else if (data.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(data); i++) result.add(integer(Array.get(data, i)));
        } else throw new IllegalArgumentException("Cần mảng hoặc List<Integer>, nhận " + data.getClass().getName());
        return result;
    }
    private static int integer(Object value) {
        if (!(value instanceof Number)) throw new IllegalArgumentException("Phần tử không phải số nguyên.");
        return new BigDecimal(value.toString()).intValueExact();
    }
    public static Object property(Object object, String name) throws Exception {
        String suffix = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (String prefix : new String[]{"get", "is"}) {
            try { return object.getClass().getMethod(prefix + suffix).invoke(object); }
            catch (NoSuchMethodException ignored) {}
        }
        Field f = field(object.getClass(), name); f.setAccessible(true); return f.get(object);
    }
    public static void property(Object object, String name, Object value) throws Exception {
        String setter = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (Method m : object.getClass().getMethods()) {
            if (m.getName().equals(setter) && m.getParameterCount() == 1) {
                Object converted = convert(m.getParameterTypes()[0], value);
                m.invoke(object, converted); return;
            }
        }
        Field f = field(object.getClass(), name); f.setAccessible(true);
        f.set(object, convert(f.getType(), value));
    }
    private static Object convert(Class<?> type, Object value) {
        if (type.isEnum() && value instanceof String) {
            for (Object constant : type.getEnumConstants()) {
                if (((Enum<?>)constant).name().equals(value)) return constant;
            }
            throw new IllegalArgumentException("Không có enum value " + value);
        }
        if (!accepts(type, value)) throw new IllegalArgumentException("Kiểu thuộc tính không phù hợp: " + type);
        return value;
    }
    private static Field field(Class<?> type, String name) throws NoSuchFieldException {
        for (Class<?> t = type; t != null; t = t.getSuperclass()) {
            try { return t.getDeclaredField(name); }
            catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException(name);
    }
}
