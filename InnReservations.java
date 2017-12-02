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
import java.util.Comparator;
import java.util.Collections;

import com.mysql.jdbc.ResultSetRow;


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

         /*DatabaseCommunicator comm = new DatabaseCommunicator(jdbcUrl, dbUsername, dbPassword, dbname);

         List<String> fields = new ArrayList<String>();
         List<String> values = new ArrayList<String>();
         
         fields.add("RoomCode");
         fields.add("maxOcc");
         values.add("AWE");
         values.add("5");
         
         Room newRoom = new Room();
         
         DatabaseObject dbObj = newRoom;
         
         comm.insertDatabase(dbObj, fields, values);*/

	/* -------------------------- */

         Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

         System.out.println("Connection Established Successful and the DATABASE NAME IS:"
                 + conn.getMetaData().getDatabaseProductName() + '\n');

         boolean chosen = false;

         while (chosen == false) {
            System.out.println("Welcome to the Reservation System. Please select a number for the following options:");
            System.out.println("1. Rooms and Rates");
            System.out.println("2. Reservations");
            System.out.println("3. Detailed Reservation Information");
            System.out.println("4. Revenue");
            System.out.println("5. (Exit)");
            System.out.println();
            System.out.print("Select a number: ");

            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();
            scanner.nextLine();
            System.out.println();

            switch (input) {
               case 1:
                  chosen = true;
                  InnReservations.roomsAndRates(conn);
                  chosen = false;
                  break;
               case 2:
                  chosen = true;
                  Reservation res = InnReservations.newReservation(conn);
                  System.out.println();
                  if (res == null) {
                     chosen = false;
                     break;
                  }
                  break;
               case 3:
                  chosen = true;
                  System.out.println("Detailed Reservation Information System");
                  System.out.println("Leave any field blank (press enter) you do not wish to search on.");
                  System.out.println("Specify First Name: ");
                  String firstName = scanner.nextLine().trim();
                  System.out.println("Specify Last Name: ");
                  String lastName = scanner.nextLine().trim();
                  System.out.println("Specify Check In Date (YYYY-MM-DD): ");
                  Date checkin = null;
                  try { 
                     checkin = Date.valueOf(scanner.nextLine());
                 } catch (IllegalArgumentException e) {
                     System.out.println("Invalid date entered.");
                 }
                           System.out.println("Specify Check Out Date (YYYY-MM-DD): ");
                 Date checkout = null;
                 try { 
                     checkout = Date.valueOf(scanner.nextLine());
                 } catch (IllegalArgumentException e) {
                     System.out.println("Invalid date entered.");
                 }
                  System.out.println("Specify Room Code: ");
                  String roomCode = scanner.nextLine().trim();
                  System.out.println("Specify Reservation Code: ");
                  String resCode = scanner.nextLine().trim();
                  
                  DatabaseCommunicator comm = new DatabaseCommunicator(jdbcUrl, dbUsername, dbPassword, dbname);
                  
                  ResultSet driResults = DatabaseCommunicator.resInfo(firstName, lastName, checkin, checkout, roomCode, resCode);
                  
                  ResultSetMetaData driMetaData = driResults.getMetaData();
                  int columnsNumber = driMetaData.getColumnCount();
                  while (driResults.next()) {
                      for (int i = 1; i <= columnsNumber; i++) {
                          if (i > 1) System.out.print(",  ");
                          String columnValue = driResults.getString(i);
                          System.out.print(driMetaData.getColumnName(i) + " " + columnValue);
                      }
                      System.out.println();
                  }
                  break;
               case 4:
                  chosen = true;
                  System.out.println("Enter a year: ");
                  int year = scanner.nextInt();
                  DatabaseCommunicator.yearlyRevenue(year);
                  break;
               case 5:
                  chosen = true;
                  System.out.println("Goodbye!");
                  break;
               default:
                  System.out.println("Invalid input - please try again" + '\n');
                  chosen = false;
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

   private static void roomsAndRates(Connection conn) {
      try {
         Statement stmt = conn.createStatement();
         ResultSet roomSet = stmt.executeQuery("select * from lab7_rooms");
         ArrayList<Room> rooms = new ArrayList<Room>();

         while (roomSet.next()) {
            Room room = new Room();
            room.setRoomCode(roomSet.getString("RoomCode"));
            room.setRoomName(roomSet.getString("RoomName"));
            room.setBeds(roomSet.getInt("Beds"));
//            room.setBeds(Integer.parseInt((roomSet.getString("Beds"))));
            room.setBedType(roomSet.getString("bedType"));
            room.setMaxOcc(roomSet.getInt("maxOcc"));
//            room.setMaxOcc(Integer.parseInt(roomSet.getString("maxOcc")));
            room.setBasePrice(roomSet.getDouble("basePrice"));
//            room.setBasePrice(Double.parseDouble(roomSet.getString("basePrice")));
            room.setDecor(roomSet.getString("decor"));
            rooms.add(room);
//            System.out.format("%s %s %d %s %d %.2f %s\n", room.getRoomCode(), room.getRoomName(), room.getBeds(),
//                    room.getBedType(), room.getMaxOcc(), room.getBasePrice(), room.getDecor());
         }

//         System.out.println("Popularity Score");
         ResultSet left = stmt.executeQuery("select Room, CheckIn, Checkout, datediff(Checkout, date_sub(curdate(), " +
                 "interval 180 day)) as \"Days Occupied\" from lab7_reservations where " +
                 "(date_sub(curdate(), interval 180 day) between CheckIn and Checkout);");
         while (left.next()) {
            String roomCode = left.getString("Room");
            int daysOccupied = left.getInt("Days Occupied");
            for (Room room : rooms) {
               if (room.getRoomCode().equals(roomCode)) {
                  room.setPopularity((double)daysOccupied);
               }
            }
         }

         ResultSet middle = stmt.executeQuery("select Room, CheckIn, Checkout, sum(datediff(Checkout, CheckIn)) " +
                 "as \"Days Occupied\" from lab7_reservations where " +
                 "(Checkin > (date_sub(curdate(), interval 180 day))) and (Checkout < curdate()) group by Room;");
         while (middle.next()) {
            String roomCode = middle.getString("Room");
            int daysOccupied = middle.getInt("Days Occupied");
            for (Room room : rooms) {
               if (room.getRoomCode().equals(roomCode)) {
                  daysOccupied += room.getPopularity();
                  room.setPopularity((double)daysOccupied);
//                  System.out.println(room.getRoomCode() + ": " + room.getPopularity());
               }
            }
         }

         ResultSet right = stmt.executeQuery("select Room, CheckIn, Checkout, datediff(curdate(), CheckIn) as " +
                 "\"Days Occupied\" from lab7_reservations where curdate() between CheckIn and Checkout;");
         while (right.next()) {
            String roomCode = right.getString("Room");
            int daysOccupied = right.getInt("Days Occupied");
            for (Room room : rooms) {
               if (room.getRoomCode().equals(roomCode)) {
                  daysOccupied += room.getPopularity();
                  room.setPopularity((double)daysOccupied);
               }
            }
         }

         for (Room room : rooms) {
            double pop = room.getPopularity();
            room.setPopularity(pop / 180.0);
//            System.out.println(room.getRoomCode() + ": " + room.getPopularity());
         }

//         System.out.println("\nMost recent checkout and length of stay:");
         ResultSet recentStay = stmt.executeQuery("select Room, max(CheckIn) as CheckIn, max(Checkout) as Checkout, " +
                 "datediff(max(Checkout), max(CheckIn)) as NumDays from lab7_reservations " +
                 "where Checkout <= curdate() group by Room;");
         while (recentStay.next()) {
            String roomCode = recentStay.getString("Room");
            java.sql.Date checkOut = recentStay.getDate("Checkout");
            int numDays = recentStay.getInt("NumDays");
//            System.out.format("%s: %s %d\n", roomCode, checkOut, numDays);
            for (Room room : rooms) {
               if (room.getRoomCode().equals(roomCode)) {
                  room.setRecentCheckout(checkOut);
                  room.setNumDays(numDays);
               }
            }
         }

//         System.out.println("\nNext available date:");
         ResultSet nextSet = stmt.executeQuery("select cout.Room, min(cout.Checkout) as 'nextDate' from lab7_reservations as cout where cout.Checkout >= curdate() and cout.Checkout not in (select cin.CheckIn from lab7_reservations as cin where cin.CheckIn >= curdate() and cin.Room = cout.Room order by cin.CheckIn) group by cout.Room order by cout.Checkout;");
         while (nextSet.next()) {
            String roomCode = nextSet.getString("Room");
            java.sql.Date nextDate = nextSet.getDate("nextDate");
            for (Room room : rooms) {
               if (room.getRoomCode().equals(roomCode)) {
                  room.setNextDate(nextDate);
//                  System.out.format("%s: %s\n", room.getRoomCode(), room.getNextDate());
               }
            }
         }

         Collections.sort(rooms, new sortByPopularity());

         System.out.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s\n", "RoomCode", "RoomName", "Beds", "bedType", "maxOcc",
                 "basePrice", "decor", "PopularityScore", "Next Available Date", "Most Recent Stay Length\n");
         for (Room room : rooms) {
            System.out.format("%s %s %d %s %d %.2f %s %.2f %s %d\n", room.getRoomCode(), room.getRoomName(),
                    room.getBeds(), room.getBedType(), room.getMaxOcc(), room.getBasePrice(), room.getDecor(),
                    room.getPopularity(), room.getNextDate(), room.getNumDays());
         }
         System.out.println();

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

      boolean proceed = false;
      while (proceed == false) {
         System.out.print("Ready to proceed? (y/n) - (type n to cancel reservation): ");
         char input = scanner.next().charAt(0);
         if (input == 'n' || input == 'N') {
            return null;
         }
         if (input == 'y' || input == 'Y') {
            proceed = true;
         }
         else {
            System.out.println("Invalid input.");
         }
      }

      return res;
   }
}

class sortByPopularity implements Comparator<Room> {
   public int compare(Room a, Room b) {
      if (a.getPopularity() < b.getPopularity()) {
         return 1;
      }
      else if (a.getPopularity() > b.getPopularity()) {
         return -1;
      }
      else {
         return 0;
      }
   }
}
