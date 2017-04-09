package org.happy.utils.datasource.mysql;

/**
 * Person类示例
 *
 * @author happy
 * @version 17/4/08 下午7:00
 */
@Table(name = "person")
public class SimpleBeanDemo {
    private long id;
    private String full_name;
    private int age;
    private String home_address;

    public SimpleBeanDemo() {}

    public SimpleBeanDemo(String full_name, int age, String home_address) {
        this.full_name = full_name;
        this.age = age;
        this.home_address = home_address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "full_name", comment = "姓名")
    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    @Column(name = "home_address", comment = "家庭住址")
    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
    }

    @Column(name = "age", comment = "年龄")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "SimpleBeanDemo{" +
                "id=" + id +
                ", full_name='" + full_name + '\'' +
                ", age=" + age +
                ", home_address='" + home_address + '\'' +
                '}';
    }
}
