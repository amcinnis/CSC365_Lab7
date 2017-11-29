/**
 * Lab 7 - JDBC
 * @author Maxwell Taylor, Austin McInnis
 */

// imports
import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.util.Scanner;

public class InnReservations {
   /** 
    * main driver - handles user commands/ui
    * sends database communicator arguments
    * to build queries
    */

   public static void main(String args[]) {
      try {
         String jdbcUrl = System.getenv("APP_JDBC_URL");
         String dbUsername = System.getenv("APP_JDBC_USER");
         String dbPassword = System.getenv("APP_JDBC_PW");
         String dbname = System.getenv("APP_JDBC_DBNAME");

         DatabaseCommunicator comm = new DatabaseCommunicator(jdbcUrl, dbUsername, dbPassword, dbname);

         List<String> fields = new ArrayList<String>();
         List<String> values = new ArrayList<String>();
         
         fields.add("RoomCode");
         fields.add("maxOcc");
         values.add("AWE");
         values.add("5");
         
         Room newRoom = new Room();
         
         DatabaseObject dbObj = newRoom;
         
         comm.insertDatabase(dbObj, fields, values);

	/* -------------------------- */

         Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

         System.out.println("Connection Established Successful and the DATABASE NAME IS:"
                 + conn.getMetaData().getDatabaseProductName() + '\n');

         boolean chosen = false;

         while (chosen == false) {
            System.out.println("Welcome to the Reservation System. Please select from the following options:");
            System.out.println("Rooms and Rates");
            System.out.println("Reservations");
            System.out.println("Detailed Reservation Information");
            System.out.println("Revenue");
            System.out.println();
            System.out.println("Enter selection: ");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().toLowerCase();
            System.out.println();

            switch (input) {
               case "rooms and rates":
                  chosen = true;
                  //InnReservations.selectAll(conn);
                  break;
               case "reservations":
                  chosen = true;
                  Reservation res = InnReservations.newReservation(conn);
                  break;
               case "detailed reservation information":
                  chosen = true;
                  break;
               case "revenue":
                  chosen = true;
                  break;
               default:
                  System.out.println("Invalid input - please try again" + '\n');
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void selectAll(Connection conn) {
      try {
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("select * from lab7_rooms");
         while (rs.next()) {
            String roomCode = rs.getString("RoomCode");
            System.out.println(roomCode);
         }
      }
      catch (SQLException exception) {
         exception.printStackTrace();
      }
   }

   private static Reservation newReservation(Connection conn) {
      Reservation res = new Reservation();
      Scanner scanner = new Scanner(System.in);

      System.out.println("New Reservation:");
      System.out.print("First Name: ");
      res.firstName = scanner.nextLine();
      System.out.print("Last Name: ");
      res.lastName = scanner.nextLine();
      System.out.print("Room Preference: ");
      res.roomPref = scanner.nextLine();
      System.out.print("Bed Preference: ");
      res.bedPref = scanner.nextLine();
      System.out.print("Check-in Date (yyyy-MM-dd): ");
      String checkIn = scanner.nextLine();
      res.checkIn = java.sql.Date.valueOf(checkIn);
      System.out.print("Check-out Date (yyyy-MM-dd): ");
      String checkOut = scanner.nextLine();
      res.checkOut = java.sql.Date.valueOf(checkOut);
      System.out.print("Number of Adults: ");
      res.numAdults = scanner.nextInt();
      System.out.print("Number of Children: ");
      res.numChildren = scanner.nextInt();

      return res;
   }
}
