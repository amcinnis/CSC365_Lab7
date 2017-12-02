# CSC365_Lab7

## Using the JDBC

Team: Austin McInnis (amcinnis), Maxwell Taylor (mtaylo32)

Database: mtaylo32

jdbcURL: APP_JDBC_URL=jdbc:mysql://csc365fall2017.webredirect.org/mtaylo32?autoReconnect=true

Known bugs: Reservations option not fully functional. SQL statements exist in code, but not implemented in UI.

Three classes:
- InnReservations: a driver class that handles user input
- DatabaseObject: a standard jdbc database object interface
- DatabaseCommunicator: a database connection and statement preparer class

