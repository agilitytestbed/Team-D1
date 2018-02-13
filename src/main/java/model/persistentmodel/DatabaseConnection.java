package model.persistentmodel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The DatabaseConnection class.
 * Consists of methods that allow the application to set up a connection to the database and use this connection.
 *
 * @author Daan Kooij
 */
public class DatabaseConnection {

    private static Connection connection;

    /**
     * Method used to set up the connection to the SQLite database.
     * Furthermore, this method calls the createTables method, which initializes the tables of the database if necessary.
     * After using this method, the connection is stored in the connection field.
     *
     * @param databaseName The filename of the SQLite database that will be connected to.
     */
    public static void setUp(String databaseName) {
        try {
            String driver = "org.sqlite.JDBC";
            Class.forName(driver);
            String databaseURL = "jdbc:sqlite:" + databaseName;
            connection = DriverManager.getConnection(databaseURL);
            createTables();
            System.out.println("Setting up database connection complete");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to database");
        }
    }

    /**
     * Method used to create tables in the database if they do not yet exist.
     * If this method is called when the database is empty, the appropriate tables will be created in the database.
     * If this method is called when the database already contains the tables that this method tries to create,
     * nothing will change.
     */
    private static void createTables() {
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Money_Transaction(\n" +
                            "\ttransaction_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "\tname text,\n" +
                            "\tamount bigint\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Category(\n" +
                            "\tcategory_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "\tname text\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Transaction_Category(\n" +
                            "\ttransaction_id int,\n" +
                            "\tcategory_id int,\n" +
                            "\tFOREIGN KEY(transaction_id) REFERENCES Money_Transaction(transaction_id) ON DELETE CASCADE,\n" +
                            "\tFOREIGN KEY(category_id) REFERENCES Category(category_id) ON DELETE CASCADE,\n" +
                            "\tPRIMARY KEY(transaction_id, category_id)\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS User_Transaction(\n" +
                            "\tsession_id text,\n" +
                            "\ttransaction_id int,\n" +
                            "\tFOREIGN KEY(transaction_id) REFERENCES Money_Transaction(transaction_id) ON DELETE CASCADE,\n" +
                            "\tPRIMARY KEY(session_id, transaction_id)\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS User_Category(\n" +
                            "\tsession_id text,\n" +
                            "\tcategory_id int,\n" +
                            "\tFOREIGN KEY(category_id) REFERENCES Category(category_id) ON DELETE CASCADE,\n" +
                            "\tPRIMARY KEY(session_id, category_id)\n" +
                            ");"
            );
            statement.close();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Error creating tables");
        }
    }

    /**
     * Method used to retrieve the connection to the database.
     *
     * @return The Connection to the database.
     */
    public static Connection getDatabaseConnection() {
        return connection;
    }

}
