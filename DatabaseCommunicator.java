import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

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
         insert.setString(0, fieldString);
         insert.setString(1, valueString);
      } catch (SQLException e) {
         e.printStackTrace();
      }
      databaseAction(insert);
      return 0;
   }
   
   
   /*	public static List<HashMap<String, Object>> queryDatabase(String query)
   {
      ResultSet rs = null;
      Connection connection = null;
      Statement stmt = null;
      
      List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
      
      connection = connect();
      if(connection != null)
   {
   try {
   stmt = (Statement) connection.createStatement();
   rs = stmt.executeQuery(query);

   ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
   int col = md.getColumnCount();

   while (rs.next()) {
   HashMap<String, Object> row = new HashMap<String, Object>(col);
   for(int i = 1; i <= col; ++i) {
   row.put(md.getColumnName(i), rs.getObject(i));
   }
   result.add(row);
   }

   rs.close();
   stmt.close();
   connection.close();

   } catch (Exception e) {
   e.printStackTrace();
   }
   }
   return result;
   }
   */	


   /*
   public static void deleteDatabase(String tableName, String value)
   {
   String delete = "DELETE FROM " + tableName + " WHERE " + value;
   databaseAction(delete);
   }

   public static void updateDatabase(DatabaseObject object, String newValue)
   {
   String update = "UPDATE " + object.getTable() + " SET " + newValue  + " WHERE " + object.getKeyIdentifier() + ";";
   databaseAction(update);
   }

   public static void editDatabase(DatabaseObject object, String newValue)
   {
   String update = "UPDATE " + object.getTable() + " SET " + newValue  + " WHERE " + object.getKeyIdentifier() + ";";
   databaseAction(update);
   }

   public static void replaceDatabase(DatabaseObject object)
   {
   String update = ("REPLACE INTO " + object.getTable() + " (" + object.getKeys() + ") "
   + "VALUES (" + object.getValues() + ");");
   databaseAction(update);
   }*/ 

   /*	public static boolean resourceExists(String tableName, String uniqueIdentifier) {
   List<HashMap<String, Object>> list = queryDatabase("SELECT count(*) FROM " + tableName + " WHERE " + uniqueIdentifier + ";");
   if (list.size() == 0)
   return false;
   return Integer.parseInt(list.get(0).get("count(*)").toString()) == 1; 
   }
   */	

   public static void roomPopScore(DatabaseObject object) {
      Connection currConn = DatabaseCommunicator.getCurrentConnection();
      PreparedStatement query = null;

      try {
         query = currConn.prepareStatement("SELECT * FROM lab7_reservations WHERE DATEDIFF(Checkout, CURDATE()) <= 180");
      } (SQLException e) {
         e.printStackTrace();
      }
      databaseAction(query);
   }

   private static void databaseAction(PreparedStatement insert) {
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
      }
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

   /**
    * Adds a list of DatabaseObjects to the database
    * @param objectList List of DatabaseObjects to be added to the database
    */
   /*	public static void addAllToDatabase(List<DatabaseObject> objectList) {
      Connection connection = null;
      Statement stmt = null;

      connection = connect();
      if(connection != null) {
         try {
            stmt = (Statement) connection.createStatement();

            for (DatabaseObject object: objectList)
            {
            String replace = "REPLACE INTO " + object.getTable() + " (" + object.getKeys() + ") "
            + "VALUES (" + object.getValues() + ");";
            stmt.executeUpdate(replace);
            }
            stmt.close();
            connection.close();

         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
   */
}

