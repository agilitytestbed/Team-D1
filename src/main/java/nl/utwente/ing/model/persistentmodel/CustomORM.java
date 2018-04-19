package nl.utwente.ing.model.persistentmodel;

import nl.utwente.ing.model.bean.Category;
import nl.utwente.ing.model.bean.Transaction;

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
            "INSERT INTO Transaction_Table (user_id, transaction_id, date, amount, external_iban, type)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?);";
    private static final String GET_TRANSACTION =
            "SELECT transaction_id, date, amount, external_iban, type\n" +
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
            "SELECT transaction_id, date, amount, external_iban, type\n" +
                    "FROM Transaction_Table\n" +
                    "WHERE user_id = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String GET_TRANSACTIONS_BY_CATEGORY =
            "SELECT t.transaction_id, t.date, t.amount, t.external_iban, t.type\n" +
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
                    "AND category_id = ?;";
    private static final String DELETE_CATEGORY_RULE =
            "DELETE FROM Category_Rule\n" +
                    "WHERE user_id = ?\n" +
                    "AND category_id = ?;";
    private static final String GET_CATEGORY_RULES =
            "SELECT category_rule_id, description, external_iban, type, category_id, apply_on_history\n" +
                    "FROM Category_Rule\n" +
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
            "INSERT INTO User_Table (session_id, highest_transaction_id, highest_category_id)\n" +
                    "VALUES (?, 0, 0);";
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
     * @param externalIBAN  The externalIBAN of the to be inserted Transaction.
     * @param type          The type of the to be inserted Transaction.
     */
    public void createTransaction(int userID, long transactionID, String date, float amount, String externalIBAN,
                                  String type) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_TRANSACTION);
            statement.setInt(1, userID);
            statement.setLong(2, transactionID);
            statement.setString(3, date);
            statement.setFloat(4, amount);
            statement.setString(5, externalIBAN);
            statement.setString(6, type);
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
                String externalIBAN = resultSet.getString(4);
                String type = resultSet.getString(5);
                transaction = new Transaction(transactionID, date, amount, externalIBAN, type);
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
                String externalIBAN = resultSet.getString(4);
                String type = resultSet.getString(5);
                transactions.add(new Transaction(transactionID, date, amount, externalIBAN, type));
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
                String externalIBAN = resultSet.getString(4);
                String type = resultSet.getString(5);
                transactions.add(new Transaction(transactionID, date, amount, externalIBAN, type));
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
                int categoryID = resultSet.getInt(1);
                String name = resultSet.getString(2);
                categories.add(new Category(categoryID, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
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
