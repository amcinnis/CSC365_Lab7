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

public class InnReservations {
   /** 
    * main driver - handles user commands/ui
    * sends database communicator arguments
    * to build queries
    */
   public static void main(String args[]) {
      System.out.println("testing...");

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
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
