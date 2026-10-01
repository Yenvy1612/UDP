package UDP;

import java.io.Serializable;

/** Đối tượng Product của đề UDP Object - Câu 4. */
public class Product implements Serializable {
    private static final long serialVersionUID = 20161107L;

    private String id;
    private String code;
    private String name;
    private int quantity;

    public Product(String id, String code, String name, int quantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void setId(String id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "Product{" + id + ", " + code + ", " + name + ", " + quantity + "}";
    }
}
