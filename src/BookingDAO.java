import java.sql.*;

public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (flight_id, passenger_name, passenger_email, passenger_phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getFlightId());
            stmt.setString(2, booking.getPassengerName());
            stmt.setString(3, booking.getPassengerEmail());
            stmt.setString(4, booking.getPassengerPhone());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLastInsertId() {
        String sql = "SELECT LAST_INSERT_ID()";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
