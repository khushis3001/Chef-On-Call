import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChefData {
    private ChefData() { }

    public static final Map<String, List<Chef>> CHEFS = new LinkedHashMap<>();

    private static final String[] IMAGES = {
        "https://images.unsplash.com/photo-1577219491135-ce391730fb2c?auto=format&fit=crop&w=700&q=85",
        "https://images.unsplash.com/photo-1556910103-1c02745aae4d?auto=format&fit=crop&w=700&q=85",
        "https://images.unsplash.com/photo-1581299894007-aaa50297cf16?auto=format&fit=crop&w=700&q=85",
        "https://images.unsplash.com/photo-1574969903809-3f7a166bcb80?auto=format&fit=crop&w=700&q=85"
    };

    static {
        // Indian cuisines — two chefs for each cuisine so users can actually compare choices.
        add("Gujarati", "Chef Hetal Mehta", "Gujarati thali, farsan & festive menus", "11+ years with dhokla, undhiyu & handvo", "12 years", "4.9", 350, "Tue-Sun • 11 AM–9 PM", "9876501001", 0);
        add("Gujarati", "Chef Bhavin Patel", "Kathiyawadi food & traditional thalis", "8+ years with sev tameta, rotla & dal dhokli", "9 years", "4.8", 300, "Mon-Sat • 10 AM–8 PM", "9876501002", 1);

        add("Punjabi", "Chef Arjun Bedi", "Amritsari kulcha, tandoori grills & gravies", "8+ years with butter chicken, kulcha & kebabs", "14 years", "4.8", 500, "Mon-Sat • 12 PM–10 PM", "9876501003", 2);
        add("Punjabi", "Chef Simran Kaur", "Punjabi home-style cooking & tandoor", "7+ years with chole bhature, tandoori chicken & dal", "10 years", "4.9", 450, "Tue-Sun • 11 AM–9 PM", "9876501004", 3);

        add("South Indian", "Chef Meera Nair", "Dosa, appam, sambar & Kerala seafood", "9+ years with dosa batter, appam & coastal curries", "11 years", "4.9", 400, "Mon-Sun • 8 AM–9 PM", "9876501005", 0);
        add("South Indian", "Chef Aditya Rao", "Andhra, Tamil & Karnataka favourites", "8+ years with biryani, pesarattu & bisibele bath", "10 years", "4.8", 380, "Mon-Sat • 8 AM–8 PM", "9876501006", 1);

        add("Rajasthani", "Chef Kunal Rathore", "Dal baati churma, gatte & royal thalis", "10+ years with dal baati, gatte & Laal Maas", "13 years", "4.8", 450, "Wed-Mon • 11 AM–9 PM", "9876501007", 2);
        add("Rajasthani", "Chef Padmini Singh", "Royal Rajasthani vegetarian cuisine", "7+ years with ker sangri, bajra roti & churma", "9 years", "4.9", 420, "Tue-Sun • 12 PM–9 PM", "9876501008", 3);

        add("Bengali", "Chef Ananya Sen", "Bengali fish, kosha mangsho & festive sweets", "7+ years with fish curries, kosha & mishti", "10 years", "4.9", 500, "Tue-Sun • 12 PM–9 PM", "9876501009", 0);
        add("Bengali", "Chef Rohan Mukherjee", "Kolkata-style comfort food", "6+ years with shorshe ilish, luchi & aloo dum", "8 years", "4.7", 470, "Mon-Sat • 12 PM–9 PM", "9876501010", 1);

        add("Maharashtrian", "Chef Riya Deshmukh", "Misal, bhakri, varan-bhaat & regional thalis", "6+ years with misal, puran poli & bhakri", "9 years", "4.8", 300, "Mon-Sat • 10 AM–9 PM", "9876501011", 2);
        add("Maharashtrian", "Chef Omkar Jadhav", "Kolhapuri & Maharashtrian specialities", "7+ years with tambda rassa, bharli vangi & thalipeeth", "10 years", "4.8", 340, "Tue-Sun • 11 AM–9 PM", "9876501012", 3);

        add("Mughlai", "Chef Zoya Khan", "Biryani, seekh kebab & slow-cooked curries", "11+ years with dum biryani, nihari & kebabs", "15 years", "4.9", 650, "Wed-Sun • 1 PM–10 PM", "9876501013", 0);
        add("Mughlai", "Chef Farhan Qureshi", "Lucknowi & Delhi-style Mughlai food", "9+ years with galouti kebab, korma & biryani", "12 years", "4.8", 620, "Mon-Sat • 1 PM–10 PM", "9876501014", 1);

        add("Kashmiri", "Chef Aamir Mir", "Rogan josh, yakhni & traditional wazwan", "9+ years with Rogan Josh & Kashmiri wazwan", "12 years", "4.8", 600, "Tue-Sun • 12 PM–9 PM", "9876501015", 2);
        add("Kashmiri", "Chef Sana Bhat", "Kashmiri vegetarian & festive dishes", "7+ years with dum aloo, nadru yakhni & haak", "9 years", "4.8", 580, "Mon-Sat • 12 PM–8 PM", "9876501016", 3);

        add("Chettinad", "Chef Kavya Iyer", "Chettinad curries, idiyappam & pepper dishes", "7+ years with Chettinad spice blends", "10 years", "4.8", 450, "Mon-Sat • 11 AM–9 PM", "9876501017", 0);
        add("Chettinad", "Chef Suresh Raman", "Tamil Nadu regional cooking", "8+ years with Chettinad chicken, dosa & pongal", "11 years", "4.7", 430, "Tue-Sun • 10 AM–9 PM", "9876501018", 1);

        add("Goan", "Chef Neil D'Souza", "Goan seafood, xacuti & vindaloo", "8+ years with coastal seafood & xacuti", "11 years", "4.7", 550, "Thu-Tue • 12 PM–10 PM", "9876501019", 2);
        add("Goan", "Chef Alisha Fernandes", "Goan Catholic & coastal home cooking", "6+ years with fish curry, cafreal & bebinca", "8 years", "4.8", 520, "Wed-Mon • 12 PM–9 PM", "9876501020", 3);

        // International cuisines
        add("Italian", "Chef Luca Moretti", "Fresh pasta, risotto & wood-fired classics", "12+ years making pasta from scratch", "16 years", "4.8", 850, "Mon-Sat • 5 PM–10 PM", "9876501021", 0);
        add("Italian", "Chef Giulia Romano", "Roman pasta & Italian family-style menus", "8+ years with carbonara, ravioli & tiramisu", "11 years", "4.9", 900, "Tue-Sun • 5 PM–10 PM", "9876501022", 1);

        add("Chinese", "Chef Wei Chen", "Dim sum, noodles & wok-tossed classics", "9+ years with dim sum & wok cooking", "12 years", "4.7", 700, "Tue-Sun • 12 PM–10 PM", "9876501023", 2);
        add("Chinese", "Chef Lin Mei", "Cantonese & Sichuan home-style menus", "7+ years with dumplings, fried rice & Sichuan dishes", "10 years", "4.8", 680, "Mon-Sat • 12 PM–9 PM", "9876501024", 3);

        add("Mexican", "Chef Sofia Alvarez", "Tacos, enchiladas, salsa & street-food menus", "7+ years with tacos, mole & fresh salsa", "10 years", "4.8", 750, "Wed-Mon • 4 PM–10 PM", "9876501025", 0);
        add("Mexican", "Chef Diego Morales", "Mexican street food & family feasts", "8+ years with tacos al pastor, mole & tamales", "12 years", "4.8", 780, "Tue-Sun • 4 PM–10 PM", "9876501026", 1);

        add("Thai", "Chef Niran Sutham", "Thai curries, noodles & aromatic starters", "10+ years with curry pastes & wok dishes", "13 years", "4.9", 800, "Mon-Sat • 5 PM–10 PM", "9876501027", 2);
        add("Thai", "Chef Mali Chantar", "Northern Thai & Bangkok-style dishes", "7+ years with pad thai, green curry & tom yum", "9 years", "4.8", 780, "Tue-Sun • 5 PM–10 PM", "9876501028", 3);

        add("Japanese", "Chef Kenji Mori", "Sushi-style bowls, ramen & teppanyaki", "8+ years with ramen broth & teppanyaki", "12 years", "4.8", 950, "Tue-Sun • 5 PM–10 PM", "9876501029", 0);
        add("Japanese", "Chef Aiko Tanaka", "Japanese comfort food & teppan menus", "7+ years with ramen, donburi & yakitori", "10 years", "4.9", 980, "Mon-Sat • 5 PM–10 PM", "9876501030", 1);

        add("Mediterranean", "Chef Eleni Papadopoulos", "Greek mezze, grilled vegetables & flatbreads", "6+ years with mezze, hummus & grilled dishes", "9 years", "4.7", 800, "Mon-Fri • 5 PM–10 PM", "9876501031", 2);
        add("Mediterranean", "Chef Marco Bellini", "Mediterranean grills & coastal dishes", "8+ years with kebabs, falafel & grilled seafood", "12 years", "4.8", 820, "Tue-Sun • 5 PM–10 PM", "9876501032", 3);

        add("Korean", "Chef Min-Jae Park", "Bibimbap, Korean barbecue & noodles", "8+ years with Korean barbecue & fermentation", "11 years", "4.8", 900, "Tue-Sun • 5 PM–10 PM", "9876501033", 0);
        add("Korean", "Chef Ji-Won Han", "Korean home cooking & barbecue", "7+ years with bulgogi, kimchi & japchae", "9 years", "4.9", 880, "Mon-Sat • 5 PM–10 PM", "9876501034", 1);

        add("Continental", "Chef Daniel Brooks", "Roasts, grilled mains & modern continental plates", "9+ years in modern continental cooking", "14 years", "4.7", 750, "Mon-Sat • 12 PM–10 PM", "9876501035", 2);
        add("Continental", "Chef Emma Wilson", "Modern European comfort food", "7+ years with grilled mains, soups & plated desserts", "10 years", "4.8", 720, "Tue-Sun • 12 PM–9 PM", "9876501036", 3);
    }

    private static void add(String cuisine, String name, String speciality, String dishExperience,
                            String experience, String rating, int price, String availability,
                            String phone, int imageIndex) {
        CHEFS.computeIfAbsent(cuisine, k -> new ArrayList<>())
             .add(new Chef(cuisine, name, speciality, dishExperience, experience, rating, price, availability,
                     phone, IMAGES[imageIndex % IMAGES.length]));
    }
}
