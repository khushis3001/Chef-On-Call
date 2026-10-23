import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name, email, phone;
    private String password;
    private final List<Booking> bookings = new ArrayList<>();
    public User(String name, String email, String phone, String password) { this.name=name; this.email=email; this.phone=phone; this.password=password; }
    public String getName(){return name;} public String getEmail(){return email;} public String getPhone(){return phone;} public String getPassword(){return password;}
    public List<Booking> getBookings(){return bookings;} public void setPassword(String password){this.password=password;}
}
