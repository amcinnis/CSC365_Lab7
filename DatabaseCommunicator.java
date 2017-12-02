import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseCommunicator {
   
   private static Connection connection = null;

   static String url = "";
   static String username = "";
   static String password = "";
   static String dbName = "";

   /** Constructor
    *  Necessary to grab environment variables
    */
   public DatabaseCommunicator(String url, String username, String password, String dbname) {
      this.url = url;
      this.username = username;
      this.password = password;
      this.dbName = dbname;
   }
   

   /** Helper method so we only connect once */
   private static void connect() {
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = (Connection) DriverManager.getConnection(url, username, password);
      } catch (SQLException | ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

   /** Helper method to close connection and reset local variable */
   private static void disconnect() {
      try {
         connection.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      connection = null;
   }

   public static Connection getCurrentConnection() {
      if (connection == null) {
         connect();
      }
      return connection;
   }
   
   /*CHANGE ALL THESE TO PREPARED STATEMENTS */
 
   public static int insertDatabase(DatabaseObject object, List<String> fields, List<String> values) {
      Connection currConn = DatabaseCommunicator.getCurrentConnection();
      PreparedStatement insert = null;
      String qMarks = "?";
      
      if (fields.size() != values.size()) {
         System.out.println("Error: insertDatabase(): fields and values different sizes");
         return 1;
      }
      
      
      for (int i = 1; i < fields.size(); i++) {
         qMarks += ", ?";
      }
      
      String baseStatement = "INSERT INTO " + object.getTable() + " (" + qMarks + ") VALUES (" + qMarks + ");";

      try {
         insert = currConn.prepareStatement(baseStatement);
//         insert.setString(0, fieldString);
//         insert.setString(1, valueString);
      } catch (SQLException e) {
         e.printStackTrace();
      }
      databaseAction(insert);
      return 0;
   }
   
   
//   public static Date nextAvailable() {
//      Connection currConn = DatabaseCommunicator.getCurrentConnection();
//      Statement query = null;
//      ResultSet results = null;
//      
//      try {
//         query = currConn.createStatement();
////         results = query.executeQuery();
//      } catch (SQLException e) {
//         e.printStackTrace();
//      }
//   }
   
   //fix prepared statement, return float score
   public static void roomPopScore(DatabaseObject object) {
      Connection currConn = DatabaseCommunicator.getCurrentConnection();
      PreparedStatement query = null;
      
      try {
         query = currConn.prepareStatement("SELECT * FROM lab7_reservations WHERE DATEDIFF(Checkout, CURDATE()) <= 180;");
      } catch (SQLException e) {
         e.printStackTrace();
      }
      databaseAction(query);
   }
   
   public static ResultSet resInfo(String firstName, String lastName, Date checkin, Date checkout, String roomCode, String resCode) {
      Connection currConn = DatabaseCommunicator.getCurrentConnection();
      PreparedStatement query = null;
      
      String stmt = "SELECT CODE, RoomCode, CheckIn, Checkout, Rate, LastName, FirstName, Adults, Kids, RoomName, Beds, bedType, maxOcc, decor " +
                     "FROM lab7_reservations " +
                     "JOIN lab7_rooms ON Room = RoomCode " +
                     "WHERE FirstName LIKE ? AND LastName LIKE ? AND " +
                     "RoomCode LIKE ? AND CODE LIKE ?";
      if (checkin != null) {
         stmt += " AND CheckIn = ?";
      }
      if (checkout != null) {
         stmt += " AND Checkout = ?";
      }
      stmt += ";";
 
      try {
         query = currConn.prepareStatement(stmt);
         if (firstName == null || firstName.equals("")) {
            query.setString(1, "%");
         } else {
            query.setString(1, firstName);
         }
         if (lastName == null || lastName.equals("")) {
            query.setString(2, "%");
         } else {
            query.setString(2, lastName);
         }
         if (roomCode == null || roomCode.equals("")) {
            query.setString(3, "%");
         } else {
            query.setString(3, roomCode);
         }
         if (resCode == null || resCode.equals("")) {
            query.setString(4, "%");
         } else {
            query.setString(4, resCode);
         }
         if (checkin != null) {
            query.setDate(5, checkin);
         }
         if (checkout != null) {
            if (checkin != null) {
               query.setDate(5, checkout);
            } else {
               query.setDate(6, checkout);
            }
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return databaseQuery(query);
   }
   
   public static void yearlyRevenue(int year) {
      Connection currConn = DatabaseCommunicator.getCurrentConnection();
      PreparedStatement monthQuery = null;
      PreparedStatement yearQuery = null;
      
      String monthlyRevStmt = "SELECT Room, CheckIn, Checkout, SUM(Rate * datediff(Checkout, CheckIn)) AS 'Monthly Revenue', monthname(Checkout) AS 'Month'" + 
            "FROM lab7_reservations WHERE YEAR(Checkout) = ? GROUP BY Room, MONTH(Checkout);";
      String yearlyRevStmt = "SELECT Room, SUM(Revenue) AS 'Yearly Revenue' FROM " + monthlyRevStmt.substring(0, monthlyRevStmt.length() - 1) + " GROUP BY Room;";
      
      try {
         monthQuery = currConn.prepareStatement(monthlyRevStmt);
         monthQuery.setInt(1, year);
         yearQuery = currConn.prepareStatement(yearlyRevStmt);
         yearQuery.setInt(1, year);
      } catch (SQLException e) {
         e.printStackTrace();
      }
      System.out.println(databaseQuery(monthQuery));
      System.out.println(databaseQuery(yearQuery));
   }
   
   //helper function for similar results
//   private ResultSet similarDates(int range) {
//      
//   }
   
   public static ResultSet makeRes(String firstName, String lastName, String roomCode, String bedType, int adults, int children, Date checkin, Date checkout) {
      Connection currConn = DatabaseCommunicator.getCurrentConnection();
      PreparedStatement query = null;
      ResultSet topFive = null;
      
      String stmt = "SELECT * FROM lab7_rooms WHERE RoomCode NOT IN (" +
            "SELECT RoomCode, CheckIn, Checkout, bedType, maxOcc " +
            "FROM lab7_reservations " +
            "JOIN lab7_rooms ON Room = RoomCode " +
            "WHERE RoomCode LIKE ? AND bedType LIKE ? " +
            "AND maxOcc >= ? AND (CheckIn BETWEEN ? AND DATE_SUB(?, INTERVAL 1 DAY) " + 
            "OR (Checkout BETWEEN DATE_ADD(?, INTERVAL 1 DAY) AND ?)) " +
            "LIMIT 5";
      
      stmt += ";";
      
      try {
         query = currConn.prepareStatement(stmt);
         if (roomCode == null || roomCode.equals("")) {
            query.setString(1, "%");
         } else {
            query.setString(1, roomCode);
         }
         if (bedType == null || bedType.equals("")) {
            query.setString(2, "%");
         } else {
            query.setString(2, lastName);
         }
         query.setInt(3, adults + children);
         query.setDate(4, checkin);
         query.setDate(5, checkout);
         query.setDate(6, checkin);
         query.setDate(7, checkout);
         
         topFive = databaseQuery(query);
         
         int i = 0;
         while (topFive.next()) {
            i++;
         }
         while (i < 5) {
            i = 0;
            //query again with additional rooms based on desired room
            //if it exists, otherwise move directly to expanded the date range
            String stmt2 = "SELECT * FROM lab7_rooms WHERE RoomCode NOT IN (" +
                  "SELECT RoomCode, CheckIn, Checkout, bedType, maxOcc, decor " +
                  "FROM lab7_reservations " +
                  "JOIN lab7_rooms ON Room = RoomCode " +
                  "WHERE decor = (SELECT decor from lab7_rooms WHERE RoomCode = ?) " + 
                  "OR bedType = (SELECT bedType from lab7_rooms WHERE RoomCode = ?) " +
                  "AND maxOcc >= ? AND (CheckIn BETWEEN ? AND DATE_SUB(?, INTERVAL 1 DAY) " + 
                  "OR (Checkout BETWEEN DATE_ADD(?, INTERVAL 1 DAY) AND ?)) " +
                  "LIMIT 5";
            
            query = currConn.prepareStatement(stmt2);
            if (roomCode == null || roomCode.equals("")) {
               query.setString(1, "%");
            } else {
               query.setString(1, roomCode);
            }
            if (bedType == null || bedType.equals("")) {
               query.setString(2, "%");
            } else {
               query.setString(2, lastName);
            }
            query.setInt(3, adults + children);
            query.setDate(4, checkin);
            query.setDate(5, checkout);
            
            topFive = databaseQuery(query);
            while (topFive.next()) {
               i++;
            }
         }
      } catch(SQLException e) {
         e.printStackTrace();
      }
      
      return topFive;
   }
   
   private static ResultSet databaseQuery(PreparedStatement pstmt) {
      ResultSet results = null;
      try {
         results = pstmt.executeQuery();
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
//         try {
//         } catch (SQLException e) {
//            e.printStackTrace();
//         }
      }
      return results;
   }

   private static int databaseAction(PreparedStatement pstmt) {
      int result = 0;
      try {
         result = pstmt.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            pstmt.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return result;
   }
   
   private static void databaseActionDC(PreparedStatement insert) {
      try {
         int result = insert.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            insert.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
         DatabaseCommunicator.disconnect();
      }
   }
}
