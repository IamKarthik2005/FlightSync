public class Flight {
    private int flightId;
    private String flightNumber;
    private String airline;
    private String sourceCity;
    private String destinationCity;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private int availableSeats;

    public Flight(int flightId, String flightNumber, String airline, String sourceCity,
                  String destinationCity, String departureTime, String arrivalTime,
                  double price, int availableSeats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    public int getFlightId() { return flightId; }
    public String getFlightNumber() { return flightNumber; }
    public String getAirline() { return airline; }
    public String getSourceCity() { return sourceCity; }
    public String getDestinationCity() { return destinationCity; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public double getPrice() { return price; }
    public int getAvailableSeats() { return availableSeats; }
}