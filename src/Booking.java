public class Booking {
    private final String reference, preference, guests, date, time, name, phone, address, request, paymentMethod;
    private final Chef chef;
    private final int guestCount, total;
    private String status;
    public Booking(String reference, Chef chef, String preference, String guests, int guestCount, String date, String time,
                   String name, String phone, String address, String request, String paymentMethod, int total) {
        this.reference=reference; this.chef=chef; this.preference=preference; this.guests=guests; this.guestCount=guestCount;
        this.date=date; this.time=time; this.name=name; this.phone=phone; this.address=address; this.request=request;
        this.paymentMethod=paymentMethod; this.total=total; this.status="Confirmed";
    }
    public String getReference(){return reference;} public Chef getChef(){return chef;} public String getPreference(){return preference;}
    public String getGuests(){return guests;} public int getGuestCount(){return guestCount;} public String getDate(){return date;} public String getTime(){return time;}
    public String getName(){return name;} public String getPhone(){return phone;} public String getAddress(){return address;} public String getRequest(){return request;}
    public String getPaymentMethod(){return paymentMethod;} public int getTotal(){return total;} public String getStatus(){return status;} public void cancel(){status="Cancelled — Refund initiated";}
}
