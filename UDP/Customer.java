package UDP;

import java.io.Serializable;

/** Đối tượng Customer của đề UDP Object - Câu 3. */
public class Customer implements Serializable {
    private static final long serialVersionUID = 20151107L;

    private String id;
    private String code;
    private String name;
    private String dayOfBirth;
    private String userName;

    public Customer(String id, String code, String name, String dayOfBirth, String userName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dayOfBirth = dayOfBirth;
        this.userName = userName;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDayOfBirth() { return dayOfBirth; }
    public String getUserName() { return userName; }
    public void setId(String id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setDayOfBirth(String dayOfBirth) { this.dayOfBirth = dayOfBirth; }
    public void setUserName(String userName) { this.userName = userName; }

    @Override
    public String toString() {
        return "Customer{" + id + ", " + code + ", " + name + ", " + dayOfBirth + ", " + userName + "}";
    }
}
