public class Booking {
    private int bookingId;
    private int flightId;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    private String bookingDate;

    public Booking(int flightId, String passengerName, String passengerEmail, String passengerPhone) {
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.passengerPhone = passengerPhone;
    }

    public int getBookingId() { return bookingId; }
    public int getFlightId() { return flightId; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public String getPassengerPhone() { return passengerPhone; }
    public String getBookingDate() { return bookingDate; }
}
