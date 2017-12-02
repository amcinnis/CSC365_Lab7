import java.sql.Date;

public class Room implements DatabaseObject {

   private String roomCode;
   private String roomName;
   private int beds;
   private String bedType; 
   private int maxOcc; 
   private double basePrice;
   private String decor;
   private double popularity;
   private java.sql.Date nextDate;
   private java.sql.Date recentCheckout;
   private int numDays;
   
   public Room(String roomCode, String roomName, int beds, String bedType, int maxOcc, double basePrice, String decor,
               double popularity, java.sql.Date nextDate, java.sql.Date recentCheckout, int numDays) {
      super();
      this.roomCode = roomCode;
      this.roomName = roomName;
      this.beds = beds;
      this.bedType = bedType;
      this.maxOcc = maxOcc;
      this.basePrice = basePrice;
      this.decor = decor;
      this.popularity = popularity;
      this.nextDate = nextDate;
      this.recentCheckout = recentCheckout;
      this.numDays = numDays;
   }
   
   public Room() {
   }

   public String getRoomCode() {
      return roomCode;
   }

   public void setRoomCode(String roomCode) {
      this.roomCode = roomCode;
   }

   public String getRoomName() {
      return roomName;
   }

   public void setRoomName(String roomName) {
      this.roomName = roomName;
   }

   public int getBeds() {
      return beds;
   }

   public void setBeds(int beds) {
      this.beds = beds;
   }

   public String getBedType() {
      return bedType;
   }

   public void setBedType(String bedType) {
      this.bedType = bedType;
   }

   public int getMaxOcc() {
      return maxOcc;
   }

   public void setMaxOcc(int maxOcc) {
      this.maxOcc = maxOcc;
   }

   public double getBasePrice() {
      return basePrice;
   }

   public void setBasePrice(double basePrice) {
      this.basePrice = basePrice;
   }

   public String getDecor() {
      return decor;
   }

   public void setDecor(String decor) {
      this.decor = decor;
   }

   public double getPopularity() {
      return popularity;
   }

   public void setPopularity(double popularity) {
      this.popularity = popularity;
   }

   public java.sql.Date getNextDate() {
      return nextDate;
   }

   public void setNextDate(java.sql.Date nextDate) {
      this.nextDate = nextDate;
   }

   public java.sql.Date getRecentCheckout() {
      return recentCheckout;
   }

   public void setRecentCheckout(java.sql.Date recentCheckout) {
      this.recentCheckout = recentCheckout;
   }

   public int getNumDays() {
      return numDays;
   }

   public void setNumDays(int numDays) {
      this.numDays = numDays;
   }

   public String getKeys(){
      return "RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor"; 
   }
   
   public String getValues() {
      return roomCode + ", " + roomName + ", " + beds + ", '" + bedType + "', '" + 
            maxOcc + ", " + basePrice + ", " + decor + "'";
   }
   
   public String getTable() {
      return "lab7_rooms"; 
   }
   
   public void updateRoom() {
      //DatabaseCommunicator.replaceDatabase(this);
   }
   
   public String getKeyIdentifier() {
      return "RoomCode='" + roomCode + "'";
   }
   
}
