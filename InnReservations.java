/**
 * Lab 7 - JDBC
 * @author Maxwell Taylor, Austin McInnis
 */

// imports
import java.sql.*;

public class InnReservations {
   /** 
    * main driver - handles user commands/ui
    * sends database communicator arguments
    * to build queries
    */
   public static void main(String args[]) {

      DatabaseCommunicator comm = new DatabaseCommunicator();


      System.out.println("test");

      try {
         String jdbcUrl = System.getenv("APP_JDBC_URL");
         String dbUsername = System.getenv("APP_JDBC_USER");
         String dbPassword = System.getenv("APP_JDBC_PW");

         Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

         System.out.println("Connection Established Successful and the DATABASE NAME IS:"
                 + conn.getMetaData().getDatabaseProductName());
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
