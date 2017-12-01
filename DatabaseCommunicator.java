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
         connection = (Connection) DriverManager.getConnection(url + dbName, username, password);
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
                     "JOIN lab7_rooms ON Room = RoomCode" +
                     "WHERE FirstName LIKE ? AND LastName LIKE ? AND " +
                     "Room LIKE ? AND CODE LIKE ?";
      if (checkin != null) {
         stmt += " AND CheckIn = ?";
      }
      if (checkout != null) {
         stmt += " AND Checkout = ?";
      }
      
      try {
         query = currConn.prepareStatement(stmt);
         if (firstName == null || firstName == "") {
            query.setString(1, "%");
         } else {
            query.setString(1, firstName);
         }
         if (lastName == null || lastName == "") {
            query.setString(2, "%");
         } else {
            query.setString(2, lastName);
         }
         if (roomCode == null || roomCode == "") {
            query.setString(3, "%");
         } else {
            query.setString(3, roomCode);
         }
         if (resCode == null || resCode == "") {
            query.setString(4, "%");
         } else {
            query.setString(4, resCode);
         }
         if (checkin != null) {
            query.setDate(5, checkin);
         }
         if (checkout != null) {
            query.setDate(6, checkout);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return databaseQuery(query);
   }
   
   private static ResultSet databaseQuery(PreparedStatement pstmt) {
      ResultSet results = null;
      try {
         results = pstmt.executeQuery();
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            pstmt.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
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
