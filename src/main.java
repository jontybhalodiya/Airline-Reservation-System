import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class main {
    private static String loggedInUser;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your choice (1 - Signup, 2 - Login): ");
        int choice = sc.nextInt();

        if (choice == 1) {
            // Signup logic
            System.out.print("Enter your Username: ");
            String username = sc.next();
            System.out.print("Enter your password: ");
            String password = sc.next();
            signupUser(username, password);
        } else if (choice == 2) {
            // Login logic
            System.out.print("Enter your Username: ");
            String username = sc.next();
            System.out.print("Enter your password: ");
            String password = sc.next();
            if (loginUser(username, password)) {
                loggedInUser = username;
                System.out.println("Login successful! Welcome, " + username + "!");
                showTicketReservationMenu(sc);
            } else {
                System.out.println("Login failed! Invalid username or password.");
            }
        } else {
            System.out.println("Invalid choice!");
        }
    }

    private static void signupUser(String username, String password) {
        try {
            // Establishing the database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            // Creating a prepared statement to insert user data
            PreparedStatement statement = conn.prepareStatement("INSERT INTO credentials (Username, Passwd) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, password);

            // Executing the query and checking the result
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Signup successful! User added to the database.");
            } else {
                System.out.println("Signup failed! Unable to add user to the database.");
            }

            // Closing the resources
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean loginUser(String username, String password) {
        try {
            // Establishing the database  MySQL connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            // Creating a prepared statement to retrieve user data from MySQL
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM credentials WHERE Username = ? AND Passwd = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            // Executing the query
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Closing the resources
                resultSet.close();
                statement.close();
                conn.close();
                return true;
            } else {
                // Closing the resources
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

    private static void showTicketReservationMenu(Scanner sc) {
        System.out.println("Ticket Reservation Menu");
        System.out.println("1. Book Ticket");
        System.out.println("2. View Past Bookings");
        System.out.println("3. Cancel Ticket");
        System.out.println("4. Logout");

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
            // Establishing the database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/password", "root", "root");

            Random random = new Random();

            for (int i = 1; i <= numPassengers; i++) {
                System.out.println("Enter details for Passenger " + i);
                System.out.print("Name: ");
                String name = sc.next();
                System.out.print("Passport Number: ");
                String passportNumber = sc.next();
                System.out.print("From: ");
                String origin = sc.next();
                System.out.print("To: ");
                String destination = sc.next();
                System.out.print("Date: ");
                String date = sc.next();
                System.out.print("Address: ");
                String address = sc.next();

                // Generate a random 10-digit ticket number
                long ticketNumber = random.nextInt(900000000) + 1000000000;

                // Creating a prepared statement to insert ticket data
                PreparedStatement statement = conn.prepareStatement("INSERT INTO reservation (Username, Name, PassportNumber, Origin, Destination, Date, Address, TicketNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, loggedInUser);
                statement.setString(2, name);
                statement.setString(3, passportNumber);
                statement.setString(4, origin);
                statement.setString(5, destination);
                statement.setString(6, date);
                statement.setString(7, address);
                statement.setLong(8, ticketNumber);

                // Executing the query
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Ticket booked successfully for Passenger " + i + ". Ticket Number: " + ticketNumber);
                } else {
                    System.out.println("Failed to book the ticket for Passenger " + i);
                }

                // Closing the resources
                statement.close();
            }

            // Closing the database connection
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
