package nl.utwente.ing.model.persistentmodel;

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
                    "CREATE TABLE IF NOT EXISTS User_Table(\n" +
                            "  user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "  session_id TEXT,\n" +
                            "  highest_transaction_id BIGINT,\n" +
                            "  highest_category_id BIGINT,\n" +
                            "  highest_category_rule_id BIGINT\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Transaction_Table(\n" +
                            "  user_id INTEGER,\n" +
                            "  transaction_id BIGINT,\n" +
                            "  date DATETIME,\n" +
                            "  amount FLOAT,\n" +
                            "  description TEXT,\n" +
                            "  external_iban TEXT,\n" +
                            "  type TEXT,\n" +
                            "  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),\n" +
                            "  PRIMARY KEY(user_id, transaction_id)\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Category_Table(\n" +
                            "  user_id INTEGER,\n" +
                            "  category_id BIGINT,\n" +
                            "  name TEXT,\n" +
                            "  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),\n" +
                            "  PRIMARY KEY(user_id, category_id)\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Transaction_Category(\n" +
                            "  user_id INTEGER,\n" +
                            "  transaction_id BIGINT,\n" +
                            "  category_id BIGINT,\n" +
                            "  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),\n" +
                            "  FOREIGN KEY(transaction_id) REFERENCES Transaction_Table(transaction_id),\n" +
                            "  FOREIGN KEY(category_id) REFERENCES Category_Table(category_id),\n" +
                            "  PRIMARY KEY(user_id, transaction_id, category_id)\n" +
                            ");"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Category_Rule(\n" +
                            "  user_id INTEGER,\n" +
                            "  category_rule_id BIGINT,\n" +
                            "  description TEXT,\n" +
                            "  external_iban TEXT,\n" +
                            "  type TEXT,\n" +
                            "  category_id BIGINT,\n" +
                            "  apply_on_history BOOLEAN,\n" +
                            "  FOREIGN KEY(user_id) REFERENCES User_Table(user_id),\n" +
                            "  FOREIGN KEY(category_id) REFERENCES Category_Table(category_id),\n" +
                            "  PRIMARY KEY(user_id, category_rule_id)\n" +
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
