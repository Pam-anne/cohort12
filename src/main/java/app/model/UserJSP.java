package app.model;
import java.util.List;

public class UserJSP {
    private String name;
    private int age;
    private int bonus; 
    private String role;
    private List<String> hobbies;

    public UserJSP() {}
    public UserJSP(String name, int age, String role, List<String> hobbies) {
        this.name = name; this.age = age; this.role = role; this.hobbies = hobbies;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getBonus() { return bonus; }
    public void setBonus(int bonus) { this.bonus = bonus; }

 public String getRole() { return role; }
    public void setRole(String r) { this.role = r; }
    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> h) { this.hobbies = h; }
}