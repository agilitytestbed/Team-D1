package nl.utwente.ing.model.persistentmodel;

import nl.utwente.ing.model.bean.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The CustomORM class.
 * Serves as a connection between the PersistentModel class and the SQL database.
 * Contains methods that translate Java statements to SQL queries and updates.
 *
 * @author Daan Kooij
 */
public class CustomORM {

    private Connection connection;

    private static final String INCREASE_HIGHEST_TRANSACTION_ID =
            "UPDATE User_Table\n" +
                    "SET highest_transaction_id = highest_transaction_id + 1\n" +
                    "WHERE user_id = ?;";
    private static final String GET_HIGHEST_TRANSACTION_ID =
            "SELECT highest_transaction_id\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String CREATE_TRANSACTION =
            "INSERT INTO Transaction_Table (user_id, transaction_id, date, amount, description, external_iban, type)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_TRANSACTION =
            "SELECT transaction_id, date, amount, description, external_iban, type\n" +
                    "FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String UPDATE_TRANSACTION_DATE =
            "UPDATE Transaction_Table\n" +
                    "SET date = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String UPDATE_TRANSACTION_AMOUNT =
            "UPDATE Transaction_Table\n" +
                    "SET amount = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String UPDATE_TRANSACTION_DESCRIPTION =
            "UPDATE Transaction_Table\n" +
                    "SET description = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String UPDATE_TRANSACTION_EXTERNAL_IBAN =
            "UPDATE Transaction_Table\n" +
                    "SET external_iban = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String UPDATE_TRANSACTION_TYPE =
            "UPDATE Transaction_Table\n" +
                    "SET type = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String DELETE_TRANSACTION =
            "DELETE FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;\n";
    private static final String GET_TRANSACTIONS =
            "SELECT transaction_id, date, amount, description, external_iban, type\n" +
                    "FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String GET_TRANSACTIONS_BY_CATEGORY =
            "SELECT t.transaction_id, t.date, t.amount, t.description, t.external_iban, t.type\n" +
                    "FROM Transaction_Table t, Category_Table c, Transaction_Category tc\n" +
                    "WHERE t.transaction_id = tc.transaction_id\n" +
                    "AND tc.category_id = c.category_id\n" +
                    "AND t.user_id = tc.user_id\n" +
                    "AND tc.user_id = c.user_id\n" +
                    "AND t.user_id = ?\n" +
                    "AND c.name = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String INCREASE_HIGHEST_CATEGORY_ID =
            "UPDATE User_Table\n" +
                    "SET highest_category_id = highest_category_id + 1\n" +
                    "WHERE user_id = ?;";
    private static final String GET_HIGHEST_CATEGORY_ID =
            "SELECT highest_category_id\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String CREATE_CATEGORY =
            "INSERT INTO Category_Table (user_id, category_id, name)\n" +
                    "VALUES (?, ?, ?);";
    private static final String GET_CATEGORY =
            "SELECT category_id, name\n" +
                    "FROM Category_Table\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_id = ?;";
    private static final String UPDATE_CATEGORY_NAME =
            "UPDATE Category_Table\n" +
                    "SET name = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_id = ?;";
    private static final String DELETE_CATEGORY =
            "DELETE FROM Category_Table\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_id = ?;";
    private static final String GET_CATEGORIES =
            "SELECT category_id, name\n" +
                    "FROM Category_Table\n" +
                    "WHERE user_id = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String INCREASE_HIGHEST_CATEGORY_RULE_ID =
            "UPDATE User_Table\n" +
                    "SET highest_category_rule_id = highest_category_rule_id + 1\n" +
                    "WHERE user_id = ?;";
    private static final String GET_HIGHEST_CATEGORY_RULE_ID =
            "SELECT highest_category_rule_id\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String CREATE_CATEGORY_RULE =
            "INSERT INTO Category_Rule (user_id, category_rule_id, description, " +
                    "external_iban, type, category_id, apply_on_history)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_CATEGORY_RULE =
            "SELECT category_rule_id, description, external_iban, type, category_id, apply_on_history\n" +
                    "FROM Category_Rule\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_rule_id = ?;";
    private static final String UPDATE_CATEGORY_RULE =
            "UPDATE Category_Rule\n" +
                    "SET description = ?, external_iban = ?, type = ?, category_id = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_rule_id = ?;";
    private static final String DELETE_CATEGORY_RULE =
            "DELETE FROM Category_Rule\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_rule_id = ?;";
    private static final String GET_CATEGORY_RULES =
            "SELECT category_rule_id, description, external_iban, type, category_id, apply_on_history\n" +
                    "FROM Category_Rule\n" +
                    "WHERE user_id = ?;";
    private static final String GET_MATCHING_TRANSACTION_IDS =
            "SELECT transaction_id\n" +
                    "FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "AND description LIKE ?\n" +
                    "AND external_iban LIKE ?\n" +
                    "AND type LIKE ?;";
    private static final String GET_TRANSACTIONS_ASCENDING =
            "SELECT transaction_id, date, amount, description, external_iban, type\n" +
                    "FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "ORDER BY date ASC;";
    private static final String GET_CURRENT_DATE =
            "SELECT date\n" +
                    "FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "ORDER BY date DESC\n" +
                    "LIMIT 1";
    private static final String INCREASE_HIGHEST_SAVING_GOAL_ID =
            "UPDATE User_Table\n" +
                    "SET highest_saving_goal_id = highest_saving_goal_id + 1\n" +
                    "WHERE user_id = ?;";
    private static final String GET_HIGHEST_SAVING_GOAL_ID =
            "SELECT highest_saving_goal_id\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String CREATE_SAVING_GOAL =
            "INSERT INTO Saving_Goal (user_id, saving_goal_id, creation_date, name, goal, " +
                    "save_per_month, min_balance_required)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_SAVING_GOAL =
            "SELECT saving_goal_id, creation_date, deletion_date, name, goal, save_per_month, min_balance_required\n" +
                    "FROM Saving_Goal\n" +
                    "WHERE user_id = ?\n" +
                    "AND saving_goal_id = ?;";
    private static final String DELETE_SAVING_GOAL =
            "UPDATE Saving_Goal\n" +
                    "SET deletion_date = ?\n" +
                    "WHERE user_id = ?\n" +
                    "AND saving_goal_id = ?;";
    private static final String GET_SAVING_GOALS =
            "SELECT saving_goal_id, creation_date, deletion_date, name, goal, save_per_month, min_balance_required\n" +
                    "FROM Saving_Goal\n" +
                    "WHERE user_id = ?;";
    private static final String INCREASE_HIGHEST_PAYMENT_REQUEST_ID =
            "UPDATE User_Table\n" +
                    "SET highest_payment_request_id = highest_payment_request_id + 1\n" +
                    "WHERE user_id = ?;";
    private static final String GET_HIGHEST_PAYMENT_REQUEST_ID =
            "SELECT highest_payment_request_id\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String CREATE_PAYMENT_REQUEST =
            "INSERT INTO Payment_Request (user_id, payment_request_id, description, due_date, " +
                    "amount, number_of_requests, filled)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);\n";
    private static final String GET_PAYMENT_REQUEST =
            "SELECT payment_request_id, description, due_date, amount, number_of_requests, filled\n" +
                    "FROM Payment_Request\n" +
                    "WHERE user_id = ?\n" +
                    "AND payment_request_id = ?;";
    private static final String GET_PAYMENT_REQUESTS =
            "SELECT payment_request_id, description, due_date, amount, number_of_requests, filled\n" +
                    "FROM Payment_Request\n" +
                    "WHERE user_id = ?;";
    private static final String SET_PAYMENT_REQUEST_FILLED =
            "UPDATE Payment_Request\n" +
                    "SET filled = 1\n" +
                    "WHERE user_id = ?\n" +
                    "AND payment_request_id = ?;";
    private static final String GET_TRANSACTIONS_BY_PAYMENT_REQUEST =
            "SELECT t.transaction_id, t.date, t.amount, t.description, t.external_iban, t.type\n" +
                    "FROM Payment_Request pr, Payment_Request_Transaction prt, Transaction_Table t\n" +
                    "WHERE pr.payment_request_id = prt.payment_request_id\n" +
                    "AND prt.transaction_id = t.transaction_id\n" +
                    "AND pr.user_id = prt.user_id\n" +
                    "AND prt.user_id = t.user_id\n" +
                    "AND t.user_id = ?\n" +
                    "AND pr.payment_request_id = ?;";
    private static final String LINK_TRANSACTION_TO_PAYMENT_REQUEST =
            "INSERT INTO Payment_Request_Transaction (user_id, transaction_id, payment_request_id)\n" +
                    "VALUES (?, ?, ?);";
    private static final String INCREASE_HIGHEST_USER_MESSAGE_ID =
            "UPDATE User_Table\n" +
                    "SET highest_user_message_id = highest_user_message_id + 1\n" +
                    "WHERE user_id = ?;\n";
    private static final String GET_HIGHEST_USER_MESSAGE_ID =
            "SELECT highest_user_message_id\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String GET_USER_MESSAGE =
            "SELECT user_message_id, message, date, read, type\n" +
                    "FROM User_Message\n" +
                    "WHERE user_id = ?\n" +
                    "AND user_message_id = ?;";
    private static final String CREATE_USER_MESSAGE =
            "INSERT INTO User_Message (user_id, user_message_id, message, date, read, type)\n" +
                    "VALUES (?, ?, ?, ?, 0, ?);";
    private static final String GET_UNREAD_USER_MESSAGES =
            "SELECT user_message_id, message, date, read, type\n" +
                    "FROM User_Message\n" +
                    "WHERE user_id = ?\n" +
                    "AND read = 0;";
    private static final String GET_ALL_USER_MESSAGES =
            "SELECT user_message_id, message, date, read, type\n" +
                    "FROM User_Message\n" +
                    "WHERE user_id = ?;";
    private static final String SET_USER_MESSAGE_READ =
            "UPDATE User_Message\n" +
                    "SET read = 1\n" +
                    "WHERE user_id = ?\n" +
                    "AND user_message_id = ?;";
    private static final String GET_HIGHEST_LIFETIME_BALANCE =
            "SELECT highest_lifetime_balance\n" +
                    "FROM User_Table\n" +
                    "WHERE user_id = ?;";
    private static final String UPDATE_HIGHEST_LIFETIME_BALANCE =
            "UPDATE User_Table\n" +
                    "SET highest_lifetime_balance = \n" +
                    "  CASE WHEN ? > highest_lifetime_balance \n" +
                    "    THEN ? \n" +
                    "    ELSE highest_lifetime_balance \n" +
                    "  END\n" +
                    "WHERE user_id = ?;";
    private static final String LINK_TRANSACTION_TO_CATEGORY =
            "INSERT INTO Transaction_Category (user_id, transaction_id, category_id)\n" +
                    "VALUES (?, ?, ?);";
    private static final String UNLINK_TRANSACTION_FROM_CATEGORY =
            "DELETE FROM Transaction_Category\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?\n" +
                    "AND category_id = ?;";
    private static final String UNLINK_TRANSACTION_FROM_ALL_CATEGORIES =
            "DELETE FROM Transaction_Category\n" +
                    "WHERE user_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String UNLINK_CATEGORY_FROM_ALL_TRANSACTIONS =
            "DELETE FROM Transaction_Category\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_id = ?;";
    private static final String GET_CATEGORY_ID_BY_TRANSACTION_ID =
            "SELECT tc.category_id\n" +
                    "FROM Transaction_Table t, Transaction_Category tc\n" +
                    "WHERE t.transaction_id = tc.transaction_id\n" +
                    "AND t.user_id = tc.user_id\n" +
                    "AND t.user_id = ?\n" +
                    "AND t.transaction_id = ?;";
    private static final String CREATE_NEW_USER =
            "INSERT INTO User_Table (session_id, highest_lifetime_balance, highest_transaction_id, " +
                    "highest_category_id, highest_category_rule_id, highest_saving_goal_id, " +
                    "highest_payment_request_id, highest_user_message_id)\n" +
                    "VALUES (?, 0, 0, 0, 0, 0, 0, 0);";
    private static final String GET_USER_ID =
            "SELECT user_id\n" +
                    "FROM User_Table\n" +
                    "WHERE session_id = ?;";

    /**
     * The constructor of CustomORM.
     * Sets the connection field to the connection parameter.
     *
     * @param connection The database connection.
     */
    public CustomORM(Connection connection) {
        this.connection = connection;
    }

    /**
     * Method used to increase the highestTransactionID field of a certain user by one in the database.
     *
     * @param userID The id of the user whose highestTransactionID field should be increased.
     */
    public void increaseHighestTransactionID(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement(INCREASE_HIGHEST_TRANSACTION_ID);
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the highestTransactionID field of a certain user from the database.
     *
     * @param userID The id of the user whose highestTransactionID field should be retrieved.
     * @return The value of the highestTransactionID field of the user with userID.
     */
    public long getHighestTransactionID(int userID) {
        long highestTransactionID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_TRANSACTION_ID);
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            highestTransactionID = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestTransactionID;
    }

    /**
     * Method used to insert a Transaction into the database.
     *
     * @param userID        The id of the user to which this new Transaction will belong.
     * @param transactionID The transactionID of the to be inserted Transaction.
     * @param date          The date of the to be inserted Transaction.
     * @param amount        The amount of the to be inserted Transaction.
     * @param description   The description of the to be inserted Transaction.
     * @param externalIBAN  The externalIBAN of the to be inserted Transaction.
     * @param type          The type of the to be inserted Transaction.
     */
    public void createTransaction(int userID, long transactionID, String date, float amount, String description,
                                  String externalIBAN, String type) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_TRANSACTION);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.setString(3, date);
            statement.setFloat(4, amount);
            statement.setString(5, description);
            statement.setString(6, externalIBAN);
            statement.setString(7, type);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a Transaction from the database.
     *
     * @param userID        The id of the user from which a Transaction should be retrieved.
     * @param transactionID The id of the to be retrieved Transaction.
     * @return A Transaction object containing data retrieved from the database.
     */
    public Transaction getTransaction(int userID, long transactionID) {
        Transaction transaction = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTION);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String date = resultSet.getString(2);
                float amount = resultSet.getFloat(3);
                String description = resultSet.getString(4);
                String externalIBAN = resultSet.getString(5);
                String type = resultSet.getString(6);
                transaction = new Transaction(transactionID, date, amount, description, externalIBAN, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    /**
     * Method used to change the date of a Transaction in the database.
     *
     * @param date          The new date of the Transaction.
     * @param userID        The id of the user whose Transaction with transactionID will be updated.
     * @param transactionID The id of the to be updated Transaction.
     */
    public void updateTransactionDate(String date, int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_DATE);
            statement.setString(1, date);
            statement.setInt(2, userID);
            statement.setLong(3, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to change the amount of a Transaction in the database.
     *
     * @param amount        The new amount of the Transaction.
     * @param userID        The id of the user whose Transaction with transactionID will be updated.
     * @param transactionID The id of the to be updated Transaction.
     */
    public void updateTransactionAmount(float amount, int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_AMOUNT);
            statement.setFloat(1, amount);
            statement.setInt(2, userID);
            statement.setLong(3, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to change the description of a Transaction in the database.
     *
     * @param description   The new description of the Transaction.
     * @param userID        The id of the user whose Transaction with transactionID will be updated.
     * @param transactionID The id of the to be updated Transaction.
     */
    public void updateTransactionDescription(String description, int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_DESCRIPTION);
            statement.setString(1, description);
            statement.setInt(2, userID);
            statement.setLong(3, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to change the externalIBAN of a Transaction in the database.
     *
     * @param externalIBAN  The new externalIBAN of the Transaction.
     * @param userID        The id of the user whose Transaction with transactionID will be updated.
     * @param transactionID The id of the to be updated Transaction.
     */
    public void updateTransactionExternalIBAN(String externalIBAN, int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_EXTERNAL_IBAN);
            statement.setString(1, externalIBAN);
            statement.setInt(2, userID);
            statement.setLong(3, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to change the type of a Transaction in the database.
     *
     * @param type          The new type of the Transaction.
     * @param userID        The id of the user whose Transaction with transactionID will be updated.
     * @param transactionID The id of the to be updated Transaction.
     */
    public void updateTransactionType(String type, int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_TYPE);
            statement.setString(1, type);
            statement.setInt(2, userID);
            statement.setLong(3, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to delete a Transaction from the database.
     *
     * @param userID        The id of the user whose Transaction with transactionID will be deleted.
     * @param transactionID The id of the to be deleted Transaction.
     */
    public void deleteTransaction(int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_TRANSACTION);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of Transaction objects belonging to a certain user from the database.
     *
     * @param userID The id of the user to who the to be retrieved Transaction objects belong.
     * @param limit  The (maximum) amount of Transaction objects to be retrieved.
     * @param offset The starting index to retrieve Transaction objects.
     * @return An ArrayList of Transaction objects.
     */
    public ArrayList<Transaction> getTransactions(int userID, int limit, int offset) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS);
            statement.setInt(1, userID);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long transactionID = resultSet.getLong(1);
                String date = resultSet.getString(2);
                float amount = resultSet.getFloat(3);
                String description = resultSet.getString(4);
                String externalIBAN = resultSet.getString(5);
                String type = resultSet.getString(6);
                transactions.add(new Transaction(transactionID, date, amount, description, externalIBAN, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Method used to retrieve a batch of Transaction objects
     * belonging to a certain user and category from the database.
     *
     * @param userID       The id of the user to who the to be retrieved Transaction objects belong.
     * @param categoryName The name of the Category to which the retrieved Transaction objects belong.
     * @param limit        The (maximum) amount of Transaction objects to be retrieved.
     * @param offset       The starting index to retrieve Transaction objects.
     * @return An ArrayList of Transaction objects.
     */
    public ArrayList<Transaction> getTransactionsByCategory(int userID, String categoryName, int limit, int offset) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS_BY_CATEGORY);
            statement.setInt(1, userID);
            statement.setString(2, categoryName);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long transactionID = resultSet.getLong(1);
                String date = resultSet.getString(2);
                float amount = resultSet.getFloat(3);
                String description = resultSet.getString(4);
                String externalIBAN = resultSet.getString(5);
                String type = resultSet.getString(6);
                transactions.add(new Transaction(transactionID, date, amount, description, externalIBAN, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Method used to increase the highestCategoryID field of a certain user by one in the database.
     *
     * @param userID The id of the user whose highestCategoryID field should be increased.
     */
    public void increaseHighestCategoryID(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement(INCREASE_HIGHEST_CATEGORY_ID);
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the highestCategoryID field of a certain user from the database.
     *
     * @param userID The id of the user whose highestCategoryID field should be retrieved.
     * @return The value of the highestCategoryID field of the user with userID.
     */
    public long getHighestCategoryID(int userID) {
        long highestCategoryID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_CATEGORY_ID);
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            highestCategoryID = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestCategoryID;
    }

    /**
     * Method used to insert a new Category into the database.
     *
     * @param userID     The id of the user to which this new Category will belong.
     * @param categoryID The categoryID of the to be inserted Category.
     * @param name       The name of the to be inserted Category.
     */
    public void createCategory(int userID, long categoryID, String name) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY);
            statement.setInt(1, userID);
            statement.setLong(2, categoryID);
            statement.setString(3, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a Category from the database.
     *
     * @param userID     The id of the user from which a Category should be retrieved.
     * @param categoryID The id of the to be retrieved Category.
     * @return A Category object containing data retrieved from the database.
     */
    public Category getCategory(int userID, long categoryID) {
        Category category = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY);
            statement.setInt(1, userID);
            statement.setLong(2, categoryID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString(2);
                category = new Category(categoryID, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    /**
     * Method used to update the name of a Category in the database.
     *
     * @param name       The new name of the to be updated Category.
     * @param userID     The id of the user whose Category with categoryID will be updated.
     * @param categoryID The id of the to be updated Category.
     */
    public void updateCategoryName(String name, int userID, long categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY_NAME);
            statement.setString(1, name);
            statement.setInt(2, userID);
            statement.setLong(3, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to delete a Category from the database.
     *
     * @param userID     The id of the user whose Category with categoryID will me deleted.
     * @param categoryID The id of the to be deleted Category.
     */
    public void deleteCategory(int userID, long categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY);
            statement.setInt(1, userID);
            statement.setLong(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of Category objects belonging to a certain user from the database.
     *
     * @param userID The id of the user to who the to be retrieved Category objects belong.
     * @param limit  The (maximum) amount of Category objects to be retrieved.
     * @param offset The starting index to retrieve Category objects.
     * @return An ArrayList of Category objects.
     */
    public ArrayList<Category> getCategories(int userID, int limit, int offset) {
        ArrayList<Category> categories = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORIES);
            statement.setInt(1, userID);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long categoryID = resultSet.getLong(1);
                String name = resultSet.getString(2);
                categories.add(new Category(categoryID, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Method used to increase the highestCategoryRuleID field of a certain user by one in the database.
     *
     * @param userID The id of the user whose highestCategoryRuleID field should be increased.
     */
    public void increaseHighestCategoryRuleID(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement(INCREASE_HIGHEST_CATEGORY_RULE_ID);
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the highestCategoryRuleID field of a certain user from the database.
     *
     * @param userID The id of the user whose highestCategoryRuleID field should be retrieved.
     * @return The value of the highestCategoryRuleID field of the user with userID.
     */
    public long getHighestCategoryRuleID(int userID) {
        long highestTransactionID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_CATEGORY_RULE_ID);
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            highestTransactionID = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestTransactionID;
    }

    /**
     * Method used to insert a CategoryRule into the database.
     *
     * @param userID       The id of the user to which this new CategoryRule will belong.
     * @param categoryRule The CategoryRule object to be inserted into the database.
     */
    public void createCategoryRule(int userID, CategoryRule categoryRule) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY_RULE);
            statement.setInt(1, userID);
            statement.setLong(2, categoryRule.getId());
            statement.setString(3, categoryRule.getDescription());
            statement.setString(4, categoryRule.getiBAN());
            statement.setString(5, categoryRule.getType());
            statement.setLong(6, categoryRule.getCategory_id());
            statement.setBoolean(7, categoryRule.getApplyOnHistory());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a CategoryRule from the database.
     *
     * @param userID         The id of the user from which a CategoryRule should be retrieved.
     * @param categoryRuleID The id of the to be retrieved CategoryRule.
     * @return A CategoryRule object containing data retrieved from the database.
     */
    public CategoryRule getCategoryRule(int userID, long categoryRuleID) {
        CategoryRule categoryRule = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY_RULE);
            statement.setInt(1, userID);
            statement.setLong(2, categoryRuleID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String description = resultSet.getString(2);
                String externalIBAN = resultSet.getString(3);
                String type = resultSet.getString(4);
                long categoryID = resultSet.getLong(5);
                boolean applyOnHistory = resultSet.getBoolean(6);
                categoryRule = new CategoryRule(categoryRuleID, description, externalIBAN, type, categoryID, applyOnHistory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryRule;
    }

    /**
     * Method used to update a CategoryRule in the database.
     *
     * @param userID       The id of the user whose CategoryRule with categoryRuleID will be updated.
     * @param categoryRule The CategoryRule object to be updated in the database.
     */
    public void updateCategoryRule(int userID, CategoryRule categoryRule) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY_RULE);
            statement.setString(1, categoryRule.getDescription());
            statement.setString(2, categoryRule.getiBAN());
            statement.setString(3, categoryRule.getType());
            statement.setLong(4, categoryRule.getCategory_id());
            statement.setInt(5, userID);
            statement.setLong(6, categoryRule.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to delete a CategoryRule from the database.
     *
     * @param userID         The id of the user whose CategoryRule with categoryRuleID will be deleted.
     * @param categoryRuleID The id of the to be deleted CategoryRule.
     */
    public void deleteCategoryRule(int userID, long categoryRuleID) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY_RULE);
            statement.setInt(1, userID);
            statement.setLong(2, categoryRuleID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of CategoryRule objects belonging to a certain user from the database.
     *
     * @param userID The id of the user to who the to be retrieved CategoryRule objects belong.
     * @return An ArrayList of CategoryRule objects.
     */
    public ArrayList<CategoryRule> getCategoryRules(int userID) {
        ArrayList<CategoryRule> categoryRules = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY_RULES);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long categoryRuleID = resultSet.getLong(1);
                String description = resultSet.getString(2);
                String externalIBAN = resultSet.getString(3);
                String type = resultSet.getString(4);
                long categoryID = resultSet.getLong(5);
                boolean applyOnHistory = resultSet.getBoolean(6);
                categoryRules.add(new CategoryRule(categoryRuleID, description, externalIBAN, type,
                        categoryID, applyOnHistory));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryRules;
    }

    /**
     * Method used to retrieve the IDs of all Transactions belonging to the user with userID that match categoryRule.
     *
     * @param userID       The ID of the user whose transactionIDs of the Transactions
     *                     that match categoryRule will be retrieved.
     * @param categoryRule The CategoryRule to which Transactions of the user with userID will be tested
     *                     to check whether they match categoryRule.
     * @return An ArrayList consisting of the transactionIDs of the Transactions belonging to the user with userID
     * that match categoryRule.
     */
    public ArrayList<Long> getMatchingTransactionIDs(int userID, CategoryRule categoryRule) {
        ArrayList<Long> matchingTransactionIDs = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_MATCHING_TRANSACTION_IDS);
            statement.setInt(1, userID);
            statement.setString(2, "%" + categoryRule.getDescription() + "%");
            statement.setString(3, "%" + categoryRule.getiBAN() + "%");
            statement.setString(4, "%" + categoryRule.getType() + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                matchingTransactionIDs.add(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchingTransactionIDs;
    }

    /**
     * Method used to retrieve all Transaction objects belonging to a certain user in ascending order from the database.
     *
     * @param userID The id of the user to who the to be retrieved Transaction objects belong.
     * @return An ArrayList of Transaction objects.
     */
    public ArrayList<Transaction> getTransactionsAscending(int userID) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS_ASCENDING);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long transactionID = resultSet.getLong(1);
                String date = resultSet.getString(2);
                float amount = resultSet.getFloat(3);
                String description = resultSet.getString(4);
                String externalIBAN = resultSet.getString(5);
                String type = resultSet.getString(6);
                transactions.add(new Transaction(transactionID, date, amount, description, externalIBAN, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Method used to retrieve the highest date belonging to a Transaction of a certain user from the database.
     *
     * @param userID The ID of the user to who the to be retrieved Transaction date belongs.
     * @return A String object containing the highest Transaction date if the user has any Transactions, otherwise a
     * String object containing the start of UNIX time.
     */
    public String getCurrentDate(int userID) {
        String date = "1970-01-01T00:00:00.000Z";

        try {
            PreparedStatement statement = connection.prepareStatement(GET_CURRENT_DATE);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                date = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * Method used to increase the highestSavingGoalID field of a certain user by one in the database.
     *
     * @param userID The ID of the user whose highestSavingGoalID field should be increased.
     */
    public void increaseHighestSavingGoalID(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement(INCREASE_HIGHEST_SAVING_GOAL_ID);
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the highestSavingGoalID field of a certain user from the database.
     *
     * @param userID The ID of the user whose highestSavingGoalID field should be retrieved.
     * @return The value of the highestSavingGoalID field of the user with userID.
     */
    public long getHighestSavingGoalID(int userID) {
        long highestSavingGoalID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_SAVING_GOAL_ID);
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            highestSavingGoalID = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestSavingGoalID;
    }

    /**
     * Method used to insert a SavingGoal into the database.
     *
     * @param userID     The ID of the user to which this new SavingGoal will belong.
     * @param savingGoal The SavingGoal object to be inserted into the database.
     */
    public void createSavingGoal(int userID, SavingGoal savingGoal) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_SAVING_GOAL);
            statement.setInt(1, userID);
            statement.setLong(2, savingGoal.getId());
            statement.setString(3, savingGoal.getCreationDate());
            statement.setString(4, savingGoal.getName());
            statement.setFloat(5, savingGoal.getGoal());
            statement.setFloat(6, savingGoal.getSavePerMonth());
            statement.setFloat(7, savingGoal.getMinBalanceRequired());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a SavingGoal from the database.
     *
     * @param userID       The id of the user from which a SavingGoal should be retrieved.
     * @param savingGoalID The id of the to be retrieved SavingGoal.
     * @return A SavingGoal object containing data retrieved from the database.
     */
    public SavingGoal getSavingGoal(int userID, long savingGoalID) {
        SavingGoal savingGoal = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_SAVING_GOAL);
            statement.setInt(1, userID);
            statement.setLong(2, savingGoalID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                savingGoalID = resultSet.getLong(1);
                String creationDate = resultSet.getString(2);
                String deletionDate = resultSet.getString(3);
                String name = resultSet.getString(4);
                float goal = resultSet.getFloat(5);
                float savePerMonth = resultSet.getFloat(6);
                float minBalanceRequired = resultSet.getFloat(7);
                savingGoal = new SavingGoal(savingGoalID, creationDate, deletionDate,
                        name, goal, savePerMonth, minBalanceRequired);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return savingGoal;
    }

    /**
     * Method used to delete a SavingGoal from the database.
     *
     * @param userID       The ID of the user whose SavingGoal with savingGoalID will be deleted.
     * @param savingGoalID The ID of the to be deleted SavingGoal.
     */
    public void deleteSavingGoal(String deletionDate, int userID, long savingGoalID) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_SAVING_GOAL);
            statement.setString(1, deletionDate);
            statement.setInt(2, userID);
            statement.setLong(3, savingGoalID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of SavingGoal objects belonging to a certain user from the database.
     *
     * @param userID The ID of the user to who the to be retrieved SavingGoal objects belong.
     * @return An ArrayList of SavingGoal objects.
     */
    public ArrayList<SavingGoal> getSavingGoals(int userID) {
        ArrayList<SavingGoal> savingGoals = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_SAVING_GOALS);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long savingGoalID = resultSet.getLong(1);
                String creationDate = resultSet.getString(2);
                String deletionDate = resultSet.getString(3);
                String name = resultSet.getString(4);
                float goal = resultSet.getFloat(5);
                float savePerMonth = resultSet.getFloat(6);
                float minBalanceRequired = resultSet.getFloat(7);
                savingGoals.add(new SavingGoal(savingGoalID, creationDate, deletionDate,
                        name, goal, savePerMonth, minBalanceRequired));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return savingGoals;
    }

    /**
     * Method used to increase the highestPaymentRequestID field of a certain user by one in the database.
     *
     * @param userID The ID of the user whose highestPaymentRequestID field should be increased.
     */
    public void increaseHighestPaymentRequestID(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement(INCREASE_HIGHEST_PAYMENT_REQUEST_ID);
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the highestPaymentRequestID field of a certain user from the database.
     *
     * @param userID The ID of the user whose highestPaymentRequestID field should be retrieved.
     * @return The value of the highestPaymentRequestID field of the user with userID.
     */
    public long getHighestPaymentRequestID(int userID) {
        long highestPaymentRequestID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_PAYMENT_REQUEST_ID);
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            highestPaymentRequestID = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestPaymentRequestID;
    }

    /**
     * Method used to insert a PaymentRequest into the database.
     *
     * @param userID         The ID of the user to which this new PaymentRequest will belong.
     * @param paymentRequest The PaymentRequest object to be inserted into the database.
     */
    public void createPaymentRequest(int userID, PaymentRequest paymentRequest) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_PAYMENT_REQUEST);
            statement.setInt(1, userID);
            statement.setLong(2, paymentRequest.getID());
            statement.setString(3, paymentRequest.getDescription());
            statement.setString(4, paymentRequest.getDue_date());
            statement.setFloat(5, paymentRequest.getAmount());
            statement.setLong(6, paymentRequest.getNumber_of_requests());
            statement.setBoolean(7, paymentRequest.getFilled());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a PaymentRequest from the database.
     *
     * @param userID           The id of the user from which a PaymentRequest should be retrieved.
     * @param paymentRequestID The id of the to be retrieved PaymentRequest.
     * @return A PaymentRequest object containing data retrieved from the database.
     */
    public PaymentRequest getPaymentRequest(int userID, long paymentRequestID) {
        PaymentRequest paymentRequest = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_PAYMENT_REQUEST);
            statement.setInt(1, userID);
            statement.setLong(2, paymentRequestID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                paymentRequestID = resultSet.getLong(1);
                String description = resultSet.getString(2);
                String dueDate = resultSet.getString(3);
                float amount = resultSet.getFloat(4);
                long numberOfRequests = resultSet.getLong(5);
                boolean filled = resultSet.getBoolean(6);
                paymentRequest = new PaymentRequest(paymentRequestID, description, dueDate,
                        amount, numberOfRequests, filled);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentRequest;
    }

    /**
     * Method used to retrieve a batch of PaymentRequest objects belonging to a certain user from the database.
     *
     * @param userID The ID of the user to who the to be retrieved PaymentRequest objects belong.
     * @return An ArrayList of PaymentRequest objects.
     */
    public ArrayList<PaymentRequest> getPaymentRequests(int userID) {
        ArrayList<PaymentRequest> paymentRequests = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_PAYMENT_REQUESTS);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long paymentRequestID = resultSet.getLong(1);
                String description = resultSet.getString(2);
                String dueDate = resultSet.getString(3);
                float amount = resultSet.getFloat(4);
                long numberOfRequests = resultSet.getLong(5);
                boolean filled = resultSet.getBoolean(6);
                paymentRequests.add(new PaymentRequest(paymentRequestID, description, dueDate,
                        amount, numberOfRequests, filled));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentRequests;
    }

    /**
     * Method used to indicate that a certain PaymentRequest of a certain user has been filled.
     *
     * @param userID           The ID of the user to which the certain PaymentRequest belongs.
     * @param paymentRequestID The ID of the PaymentRequest for which it should be indicated that it is filled.
     */
    public void setPaymentRequestFilled(int userID, long paymentRequestID) {
        try {
            PreparedStatement statement = connection.prepareStatement(SET_PAYMENT_REQUEST_FILLED);
            statement.setInt(1, userID);
            statement.setLong(2, paymentRequestID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of Transaction objects belonging to a certain user
     * and fulfilling a certain PaymentRequest from the database.
     *
     * @param userID           The id of the user to who the to be retrieved Transaction objects belong.
     * @param paymentRequestID The ID of the PaymentRequest to which the retrieved Transaction objects belong.
     * @return An ArrayList of Transaction objects.
     */
    public ArrayList<Transaction> getTransactionsByPaymentRequest(int userID, long paymentRequestID) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS_BY_PAYMENT_REQUEST);
            statement.setInt(1, userID);
            statement.setLong(2, paymentRequestID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long transactionID = resultSet.getLong(1);
                String date = resultSet.getString(2);
                float amount = resultSet.getFloat(3);
                String description = resultSet.getString(4);
                String externalIBAN = resultSet.getString(5);
                String type = resultSet.getString(6);
                transactions.add(new Transaction(transactionID, date, amount, description, externalIBAN, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Method used to link a Transaction to a PaymentRequest in the database.
     *
     * @param userID           The ID of the user to who the to be linked Transaction and PaymentRequest objects belong.
     * @param transactionID    The ID of the Transaction that will be linked to a PaymentRequest.
     * @param paymentRequestID The ID of the PaymentRequest that will be linked to a Transaction.
     */
    public void linkTransactionToPaymentRequest(int userID, long transactionID, long paymentRequestID) {
        try {
            PreparedStatement statement = connection.prepareStatement(LINK_TRANSACTION_TO_PAYMENT_REQUEST);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.setLong(3, paymentRequestID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to increase the highestUserMessageID field of a certain user by one in the database.
     *
     * @param userID The ID of the user whose highestUserMessageID field should be increased.
     */
    public void increaseHighestUserMessageID(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement(INCREASE_HIGHEST_USER_MESSAGE_ID);
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the highestUserMessageID field of a certain user from the database.
     *
     * @param userID The ID of the user whose highestUserMessageID field should be retrieved.
     * @return The value of the highestUserMessageID field of the user with userID.
     */
    public long getHighestUserMessageID(int userID) {
        long highestUserMessageID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_USER_MESSAGE_ID);
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            highestUserMessageID = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestUserMessageID;
    }

    /**
     * Method used to insert a UserMessage into the database.
     *
     * @param userID      The ID of the user to which this new UserMessage will belong.
     * @param userMessage The UserMessage object to be inserted into the database.
     */
    public void createUserMessage(int userID, UserMessage userMessage) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_USER_MESSAGE);
            statement.setInt(1, userID);
            statement.setLong(2, userMessage.getID());
            statement.setString(3, userMessage.getMessage());
            statement.setString(4, userMessage.getDate());
            statement.setString(5, userMessage.getType());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a UserMessage from the database.
     *
     * @param userID        The id of the user from which a UserMessage should be retrieved.
     * @param userMessageID The id of the to be retrieved UserMessage.
     * @return A UserMessage object containing data retrieved from the database.
     */
    public UserMessage getUserMessage(int userID, long userMessageID) {
        UserMessage userMessage = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_USER_MESSAGE);
            statement.setInt(1, userID);
            statement.setLong(2, userMessageID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userMessageID = resultSet.getLong(1);
                String message = resultSet.getString(2);
                String date = resultSet.getString(3);
                boolean read = resultSet.getBoolean(4);
                String type = resultSet.getString(5);
                userMessage = new UserMessage(userMessageID, message, date, read, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userMessage;
    }

    /**
     * Method used to retrieve a batch of unread UserMessage objects belonging to a certain user from the database.
     *
     * @param userID The ID of the user to who the to be retrieved UserMessage objects belong.
     * @return An ArrayList of UserMessage objects.
     */
    public ArrayList<UserMessage> getUnreadUserMessages(int userID) {
        ArrayList<UserMessage> userMessages = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_UNREAD_USER_MESSAGES);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long userMessageID = resultSet.getLong(1);
                String message = resultSet.getString(2);
                String date = resultSet.getString(3);
                boolean read = resultSet.getBoolean(4);
                String type = resultSet.getString(5);
                userMessages.add(new UserMessage(userMessageID, message, date, read, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userMessages;
    }

    /**
     * Method used to retrieve a batch of UserMessage objects belonging to a certain user from the database.
     *
     * @param userID The ID of the user to who the to be retrieved UserMessage objects belong.
     * @return An ArrayList of UserMessage objects.
     */
    public ArrayList<UserMessage> getAllUserMessages(int userID) {
        ArrayList<UserMessage> userMessages = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_ALL_USER_MESSAGES);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long userMessageID = resultSet.getLong(1);
                String message = resultSet.getString(2);
                String date = resultSet.getString(3);
                boolean read = resultSet.getBoolean(4);
                String type = resultSet.getString(5);
                userMessages.add(new UserMessage(userMessageID, message, date, read, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userMessages;
    }

    /**
     * Method used to indicate that a certain UserMessage of a certain user has been read.
     *
     * @param userID        The ID of the user to which the certain UserMessage belongs.
     * @param userMessageID The ID of the UserMessage for which it should be indicated that it is read.
     */
    public void setUserMessageRead(int userID, long userMessageID) {
        try {
            PreparedStatement statement = connection.prepareStatement(SET_USER_MESSAGE_READ);
            statement.setInt(1, userID);
            statement.setLong(2, userMessageID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method user to retrieve the highest lifetime balance of a certain user.
     *
     * @param userID The ID of the user whose highest lifetime balance will be retrieved.
     * @return The highest lifetime balance of the user.
     */
    public float getHighestLifetimeBalance(int userID) {
        float highestLifetimeBalance = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(GET_HIGHEST_LIFETIME_BALANCE);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                highestLifetimeBalance = resultSet.getFloat(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return highestLifetimeBalance;
    }

    /**
     * Method used to potentially update the highest lifetime balance of a certain user.
     * The highest lifetime balance of the user will only be updated if the current balance is higher than the current
     * highest lifetime balance.
     *
     * @param userID         The ID of the user whose highest lifetime balance may be updated.
     * @param currentBalance The currentBalance of the user.
     */
    public void updateHighestLifetimeBalance(int userID, float currentBalance) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_HIGHEST_LIFETIME_BALANCE);
            statement.setFloat(1, currentBalance);
            statement.setFloat(2, currentBalance);
            statement.setInt(3, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to link a Transaction to a Category in the database.
     *
     * @param userID        The id of the user to who the to be linked Transaction and Category objects belong.
     * @param transactionID The id of the Transaction that will be linked to a Category.
     * @param categoryID    The id of the Category that will be linked to a Transaction.
     */
    public void linkTransactionToCategory(int userID, long transactionID, long categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(LINK_TRANSACTION_TO_CATEGORY);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.setLong(3, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Transaction from a Category in the database.
     *
     * @param userID        The id of the user to who the to be unlinked Transaction and Category objects belong.
     * @param transactionID The id of the Transaction that will be unlinked from a Category.
     * @param categoryID    The id of the Category from which the Transaction will be unlinked.
     */
    public void unlinkTransactionFromCategory(int userID, long transactionID, long categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_TRANSACTION_FROM_CATEGORY);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.setLong(3, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Transaction from all Category objects in the database.
     *
     * @param userID        The id of the user to who the to be unlinked Transaction object belongs.
     * @param transactionID The id of the Transaction that will be unlinked from all Category objects in the database.
     */
    public void unlinkTransactionFromAllCategories(int userID, long transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_TRANSACTION_FROM_ALL_CATEGORIES);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Category from all Transaction objects in the database.
     *
     * @param userID     The id of the user to who the to be unlinked Category object belongs.
     * @param categoryID The id of the Category that will be unlinked from all Transaction objects in the database.
     */
    public void unlinkCategoryFromAllTransactions(int userID, long categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_CATEGORY_FROM_ALL_TRANSACTIONS);
            statement.setInt(1, userID);
            statement.setLong(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the id of the Category that is linked to a certain Transaction from the database.
     *
     * @param userID        The id of the user who is the owner of the Transaction object with transactionID.
     * @param transactionID The id of the Transaction from which the linked Category id will be retrieved.
     * @return The id of the Category that is linked to the Transaction.
     */
    public long getCategoryIDByTransactionID(int userID, long transactionID) {
        long categoryID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY_ID_BY_TRANSACTION_ID);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                categoryID = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryID;
    }

    /**
     * Method used to add a new User with sessionID in the database.
     *
     * @param sessionID The sessionID of the to be created User.
     */
    public void createNewUser(String sessionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_NEW_USER);
            statement.setString(1, sessionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the userID of the user with sessionID from the database.
     *
     * @param sessionID The sessionID of the User whose userID will be retrieved.
     * @return The userID of the user with sessionID.
     */
    public int getUserID(String sessionID) {
        int userID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_USER_ID);
            statement.setString(1, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userID;
    }

}
