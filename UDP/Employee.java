package UDP;

import java.io.Serializable;

/** Đối tượng Employee của đề UDP Object - Câu 2. */
public class Employee implements Serializable {
    private static final long serialVersionUID = 20261107L;

    private String id;
    private String name;
    private double salary;
    private String hireDate;

    public Employee(String id, String name, double salary, String hireDate) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getSalary() { return salary; }
    public String getHireDate() { return hireDate; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    @Override
    public String toString() {
        return "Employee{" + id + ", " + name + ", " + salary + ", " + hireDate + "}";
    }
}
