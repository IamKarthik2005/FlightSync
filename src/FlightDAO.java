import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public List<Flight> getFlightsByDate(String date) {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights WHERE DATE(departure_time) = ? AND available_seats > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Flight flight = new Flight(
                        rs.getInt("flight_id"),
                        rs.getString("flight_number"),
                        rs.getString("airline"),
                        rs.getString("source_city"),
                        rs.getString("destination_city"),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time"),
                        rs.getDouble("price"),
                        rs.getInt("available_seats")
                );
                flights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }


    public boolean reduceAvailableSeats(int flightId) {
        String sql = "UPDATE flights SET available_seats = available_seats - 1 WHERE flight_id = ? AND available_seats > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Flight getFlightById(int flightId) {
        String sql = "SELECT * FROM flights WHERE flight_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Flight(
                        rs.getInt("flight_id"),
                        rs.getString("flight_number"),
                        rs.getString("airline"),
                        rs.getString("source_city"),
                        rs.getString("destination_city"),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time"),
                        rs.getDouble("price"),
                        rs.getInt("available_seats")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}