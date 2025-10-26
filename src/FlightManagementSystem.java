import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class FlightManagementSystem extends JFrame {
    private JTextField dateField;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JButton searchButton, bookButton;
    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;

    public FlightManagementSystem() {
        flightDAO = new FlightDAO();
        bookingDAO = new BookingDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());

        topPanel.add(new JLabel("Select Date (YYYY-MM-DD):"));
        dateField = new JTextField(15);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        topPanel.add(dateField);

        searchButton = new JButton("Search Flights");
        searchButton.addActionListener(new SearchButtonListener());
        topPanel.add(searchButton);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        String[] columnNames = {
                "Flight ID", "Flight Number", "Airline", "Source",
                "Destination", "Departure Time", "Arrival Time",
                "Price (₹)", "Available Seats"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        flightsTable = new JTable(tableModel);

        // Set table selection mode to single selection
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());

        bookButton = new JButton("Book Selected Flight");
        bookButton.addActionListener(new BookButtonListener());
        bottomPanel.add(bookButton);

        return bottomPanel;
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedDate = dateField.getText().trim();

            if (selectedDate.isEmpty()) {
                showError("Please enter a date");
                return;
            }

            tableModel.setRowCount(0);

            List<Flight> flights = flightDAO.getFlightsByDate(selectedDate);

            if (flights.isEmpty()) {
                showInfo("No flights available on " + selectedDate);
            } else {
                displayFlights(flights);
            }
        }
    }

    private class BookButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = flightsTable.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a flight to book");
                return;
            }

            // Get flight ID from the selected row (first column)
            int flightId = (Integer) tableModel.getValueAt(selectedRow, 0);
            showBookingDialog(flightId);
        }
    }

    private void showBookingDialog(int flightId) {
        // Get flight details for display
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            showError("Flight not found!");
            return;
        }

        // Create booking dialog
        JDialog bookingDialog = new JDialog(this, "Book Flight", true);
        bookingDialog.setSize(500, 400);
        bookingDialog.setLocationRelativeTo(this);
        bookingDialog.setLayout(new BorderLayout(10, 10));

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Flight details panel
        JPanel flightPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        flightPanel.setBorder(BorderFactory.createTitledBorder("Flight Details"));
        flightPanel.add(new JLabel("Flight:"));
        flightPanel.add(new JLabel(flight.getFlightNumber() + " - " + flight.getAirline()));
        flightPanel.add(new JLabel("Route:"));
        flightPanel.add(new JLabel(flight.getSourceCity() + " to " + flight.getDestinationCity()));
        flightPanel.add(new JLabel("Departure:"));
        flightPanel.add(new JLabel(formatDateTime(flight.getDepartureTime())));
        flightPanel.add(new JLabel("Price:"));
        flightPanel.add(new JLabel("₹" + flight.getPrice()));

        // Passenger details panel
        JPanel passengerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        passengerPanel.setBorder(BorderFactory.createTitledBorder("Passenger Details"));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        passengerPanel.add(new JLabel("Full Name:"));
        passengerPanel.add(nameField);
        passengerPanel.add(new JLabel("Email:"));
        passengerPanel.add(emailField);
        passengerPanel.add(new JLabel("Phone:"));
        passengerPanel.add(phoneField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm Booking");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(flightPanel, BorderLayout.NORTH);
        mainPanel.add(passengerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        bookingDialog.add(mainPanel);

        // Confirm button action
        confirmButton.addActionListener(e -> {
            String passengerName = nameField.getText().trim();
            String passengerEmail = emailField.getText().trim();
            String passengerPhone = phoneField.getText().trim();

            // Validate inputs
            if (passengerName.isEmpty() || passengerEmail.isEmpty() || passengerPhone.isEmpty()) {
                showError("Please fill all passenger details");
                return;
            }

            if (!isValidEmail(passengerEmail)) {
                showError("Please enter a valid email address");
                return;
            }

            if (!isValidPhone(passengerPhone)) {
                showError("Please enter a valid phone number (10 digits)");
                return;
            }

            // Create booking object
            Booking booking = new Booking(flightId, passengerName, passengerEmail, passengerPhone);

            // Process booking
            boolean bookingSuccess = bookingDAO.createBooking(booking);
            boolean seatReduced = flightDAO.reduceAvailableSeats(flightId);

            if (bookingSuccess && seatReduced) {
                int bookingId = bookingDAO.getLastInsertId();
                showSuccess("Booking confirmed!\n\n" +
                        "Booking ID: " + bookingId + "\n" +
                        "Flight: " + flight.getFlightNumber() + "\n" +
                        "Passenger: " + passengerName + "\n" +
                        "Thank you for your booking!");
                bookingDialog.dispose();
                // Refresh the flights list to update available seats
                searchButton.doClick();
            } else {
                showError("Booking failed. Please try again.");
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> bookingDialog.dispose());

        bookingDialog.setVisible(true);
    }

    private void displayFlights(List<Flight> flights) {
        for (Flight flight : flights) {
            Object[] rowData = {
                    flight.getFlightId(),
                    flight.getFlightNumber(),
                    flight.getAirline(),
                    flight.getSourceCity(),
                    flight.getDestinationCity(),
                    formatDateTime(flight.getDepartureTime()),
                    formatDateTime(flight.getArrivalTime()),
                    String.format("₹%.2f", flight.getPrice()),
                    flight.getAvailableSeats()
            };
            tableModel.addRow(rowData);
        }
    }

    private String formatDateTime(String dateTime) {
        try {
            return dateTime.substring(0, 16).replace("T", " ");
        } catch (Exception e) {
            return dateTime;
        }
    }

    // Validation methods
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FlightManagementSystem().setVisible(true);
        });
    }
}