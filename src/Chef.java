public class Chef {
    private final String cuisine;
    private final String name;
    private final String speciality;
    private final String dishExperience;
    private final String experience;
    private final String rating;
    private final int price;
    private final String availability;
    private final String phone;
    private final String imageUrl;

    public Chef(String cuisine, String name, String speciality, String dishExperience,
                String experience, String rating, int price, String availability,
                String phone, String imageUrl) {
        this.cuisine = cuisine;
        this.name = name;
        this.speciality = speciality;
        this.dishExperience = dishExperience;
        this.experience = experience;
        this.rating = rating;
        this.price = price;
        this.availability = availability;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }
    public String getCuisine() { return cuisine; }
    public String getName() { return name; }
    public String getSpeciality() { return speciality; }
    public String getDishExperience() { return dishExperience; }
    public String getExperience() { return experience; }
    public String getRating() { return rating; }
    public int getPrice() { return price; }
    public String getAvailability() { return availability; }
    public String getPhone() { return phone; }
    public String getImageUrl() { return imageUrl; }
    @Override public String toString() { return name; }
}
