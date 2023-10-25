import java.sql.*;
import java.util.Scanner;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;

public class main {
    private static String loggedInUser;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean isLoggedIn = false;

        while (!isLoggedIn) {
            System.out.print("Enter your choice (1 - Signup, 2 - Login): ");
            int choice = sc.nextInt();

            if (choice == 1) {
                // Signup logic
                System.out.print("Enter your Username: ");
                String username = sc.next();
                System.out.print("Enter your password: ");
                String password = sc.next();
                signupUser(username, password);
                System.out.println("Signup successful! You can now log in.");
            } else if (choice == 2) {
                // Login logic
                System.out.print("Enter your Username: ");
                String username = sc.next();
                System.out.print("Enter your password: ");
                String password = sc.next();
                isLoggedIn = loginUser(username, password, sc);
                if (isLoggedIn) {
                    loggedInUser = username;
                    System.out.println("Login successful! Welcome, " + username + "!");
                    if ("admin".equals(username)) {
                        showAdminMenu(sc);
                    } else {
                        showTicketReservationMenu(sc);
                    }
                } else {
                    System.out.println("Login failed! Invalid username or password.");
                }
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }



    private static void signupUser(String username, String password) {
        try {
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");
            PreparedStatement statement = conn.prepareStatement("INSERT INTO credentials (Username, Passwd) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            int rowsInserted = statement.executeUpdate();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean loginUser(String username, String password, Scanner sc) {
        while (true) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");
                PreparedStatement statement = conn.prepareStatement("SELECT Passwd FROM credentials WHERE Username = ?");
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("Passwd");
                    resultSet.close();
                    statement.close();
                    conn.close();
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return true;
                    } else {
                        System.out.println("Invalid credentials. Please try again.");
                        break;  // Break out of the loop and prompt for input again
                    }
                } else {
                    resultSet.close();
                    statement.close();
                    conn.close();
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private static void showAdminMenu(Scanner sc) {
        System.out.println("Admin Menu");
        System.out.println("1. Add Flight");
        System.out.println("2. Cancel Flight");
        System.out.println("3. Logout");

        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                addFlight(sc);
                break;
            case 2:
                cancelFlight(sc);
                break;
            case 3:
                loggedInUser = null;
                System.out.println("Logged out successfully!");
                break;
            default:
                System.out.println("Invalid choice!");
                break;
        }

        if (loggedInUser != null) {
            showAdminMenu(sc);
        }
    }

    private static void cancelFlight(Scanner sc) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            System.out.print("Enter Origin of the flight to cancel: ");
            String origin = sc.next();
            System.out.print("Enter Destination of the flight to cancel: ");
            String destination = sc.next();
            System.out.print("Enter Flight Date of the flight to cancel (yyyy-mm-dd): ");
            String date = sc.next();


            PreparedStatement statement = conn.prepareStatement("DELETE FROM flight WHERE Origin = ? AND Destination = ? AND FlightDate = ?");
            statement.setString(1, origin);
            statement.setString(2, destination);
            statement.setString(3, date);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Flight canceled successfully!");
            } else {
                System.out.println("Failed to cancel the flight.");
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addFlight(Scanner sc) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            System.out.print("Enter Origin: ");
            String origin = sc.next();
            System.out.print("Enter Destination: ");
            String destination = sc.next();
            System.out.print("Enter Flight Date (yyyy-mm-dd): ");
            String date = sc.next();

            PreparedStatement statement = conn.prepareStatement("INSERT INTO flight (Origin, Destination, FlightDate) VALUES (?, ?, ?)");
            statement.setString(1, origin);
            statement.setString(2, destination);
            statement.setString(3, date);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Flight added successfully!");
            } else {
                System.out.println("Failed to add the flight.");
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showFlightsOnDate(Scanner sc) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            System.out.print("Enter Date (yyyy-mm-dd): ");
            String date = sc.next();

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM flight WHERE FlightDate = ?");
            statement.setString(1, date);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Flights on " + date);
                do {
                    int FlightId = resultSet.getInt("FlightID");
                    String origin = resultSet.getString("Origin");
                    String destination = resultSet.getString("Destination");

                    System.out.println("Flight id: "+ FlightId);
                    System.out.println("From: " + origin);
                    System.out.println("To: " + destination);
                    System.out.println("--------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No flights found for the given date.");
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void showTicketReservationMenu(Scanner sc) {
        System.out.println("Ticket Reservation Menu");
        System.out.println("1. Book Ticket");
        System.out.println("2. View Past Bookings");
        System.out.println("3. Cancel Ticket");
        System.out.println("4. Show Flights on Date");
        System.out.println("5. Logout");

        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                bookTicket(sc);
                break;
            case 2:
                viewPastBookings();
                break;
            case 3:
                cancelTicket(sc);
                break;
            case 4:
                showFlightsOnDate(sc);
                break;
            case 5:
                loggedInUser = null;
                System.out.println("Logged out successfully!");
                break;
            default:
                System.out.println("Invalid choice!");
                break;
        }

        if (loggedInUser != null) {
            showTicketReservationMenu(sc);
        }
    }


    private static void bookTicket(Scanner sc) {
        System.out.print("Enter number of passengers: ");
        int numPassengers = sc.nextInt();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");
            Random random = new Random();

            for (int i = 1; i <= numPassengers; i++) {
                System.out.println("Enter details for Passenger " + i);
                sc.nextLine();
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Passport Number: ");
                String passportNumber = sc.next();
                System.out.print("From: ");
                String origin = sc.next();
                System.out.print("To: ");
                String destination = sc.next();
                System.out.print("Date: (yyyy-mm-dd) ");
                String date = sc.next();
                System.out.print("Address: ");
                String address = sc.next();

                if (isValidFlight(origin, destination, date, conn)) {

                    int flightId = getFlightId(conn, origin, destination, date);

                    long ticketNumber = random.nextInt(900000000) + 1000000000;
                    insertReservationData(conn, loggedInUser, name, passportNumber, origin, destination, date, address, ticketNumber, flightId);
                    System.out.println("Ticket booked successfully for Passenger " + i + ". Ticket Number: " + ticketNumber);
                } else {
                    System.out.println("No available flights for the given route and date.");
                }
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Additional method to retrieve FlightID based on source, destination, and date
    private static int getFlightId(Connection conn, String origin, String destination, String date) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT FlightID FROM flight WHERE Origin = ? AND Destination = ? AND FlightDate = ?");
            statement.setString(1, origin);
            statement.setString(2, destination);
            statement.setString(3, date);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("FlightID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 to indicate no valid FlightID found
    }

    private static boolean isValidFlight(String origin, String destination, String date, Connection conn) {
        try {
            String query = "SELECT COUNT(*) AS count FROM flight WHERE Origin = ? AND Destination = ? AND FlightDate = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, origin);
            statement.setString(2, destination);
            statement.setString(3, date);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void insertReservationData(Connection conn, String username, String name, String passportNumber, String origin, String destination, String date, String address, long ticketNumber, int flight_id) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO reservation (Username, Name, PassportNumber, Origin, Destination, Date, Address, TicketNumber, flight_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, username);
        statement.setString(2, name);
        statement.setString(3, passportNumber);
        statement.setString(4, origin);
        statement.setString(5, destination);
        statement.setString(6, date);
        statement.setString(7, address);
        statement.setLong(8, ticketNumber);
        statement.setInt(9, flight_id);

        int rowsInserted = statement.executeUpdate();
        statement.close();
    }


    private static void viewPastBookings() {
        try {
            // Establishing the database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            // Creating a prepared statement to retrieve booking data
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM reservation WHERE Username = ?");
            statement.setString(1, loggedInUser);

            // Executing the query
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Past Booking Details for User: " + loggedInUser);
                do {
                    int ticketNumber = resultSet.getInt("TicketNumber");
                    String name = resultSet.getString("Name");
                    String passportNumber = resultSet.getString("PassportNumber");
                    String origin = resultSet.getString("Origin");
                    String destination = resultSet.getString("Destination");
                    String date = resultSet.getString("Date");
                    String address = resultSet.getString("Address");

                    System.out.println("Ticket Number: " + ticketNumber);
                    System.out.println("Name: " + name);
                    System.out.println("Passport Number: " + passportNumber);
                    System.out.println("From: " + origin);
                    System.out.println("To: " + destination);
                    System.out.println("Date: " + date);
                    System.out.println("Address: " + address);
                    System.out.println("--------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No past bookings found for User: " + loggedInUser);
            }

            // Closing the resources
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cancelTicket(Scanner sc) {
        System.out.print("Enter ticket number: ");
        int ticketNumber = sc.nextInt();

        try {
            // Establishing the database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            // Creating a prepared statement to delete booking data
            PreparedStatement statement = conn.prepareStatement("DELETE FROM reservation WHERE Username = ? AND TicketNumber = ?");
            statement.setString(1, loggedInUser);
            statement.setInt(2, ticketNumber);

            // Executing the query
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Ticket cancellation successful!");
            } else {
                System.out.println("Failed to cancel the ticket!");
            }

            // Closing the resources
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}