package model.persistentmodel;

import model.bean.Category;
import model.bean.Transaction;

import java.sql.*;
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

    private static final String CREATE_TRANSACTION =
            "INSERT INTO Money_Transaction (name, amount)\n" +
                    "VALUES (?, ?);";
    private static final String GET_TRANSACTION =
            "SELECT transaction_id, name, amount\n" +
                    "FROM Money_Transaction\n" +
                    "WHERE transaction_id = ?;";
    private static final String GET_TRANSACTION_BY_SESSION_ID =
            "SELECT t.transaction_id, t.name, t.amount\n" +
                    "FROM Money_Transaction t, User_Transaction ut\n" +
                    "WHERE t.transaction_id = ut.transaction_id\n" +
                    "AND ut.session_id = ?\n" +
                    "AND t.transaction_id = ?;";
    private static final String GET_NEWEST_TRANSACTION_ID =
            "SELECT MAX(transaction_id)\n" +
                    "FROM Money_Transaction;";
    private static final String UPDATE_TRANSACTION_NAME =
            "UPDATE Money_Transaction\n" +
                    "SET name = ?\n" +
                    "WHERE transaction_id = ?;";
    private static final String UPDATE_TRANSACTION_AMOUNT =
            "UPDATE Money_Transaction\n" +
                    "SET amount = ?\n" +
                    "WHERE transaction_id = ?;";
    private static final String DELETE_TRANSACTION =
            "DELETE FROM Money_Transaction\n" +
                    "WHERE transaction_id = ?;";
    private static final String GET_TRANSACTIONS =
            "SELECT t.transaction_id, t.name, t.amount\n" +
                    "FROM Money_Transaction t, User_Transaction ut\n" +
                    "WHERE t.transaction_id = ut.transaction_id\n" +
                    "AND ut.session_id = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String GET_TRANSACTIONS_BY_CATEGORY_ID =
            "SELECT t.transaction_id, t.name, t.amount\n" +
                    "FROM Money_Transaction t, User_Transaction ut, Transaction_Category tc\n" +
                    "WHERE t.transaction_id = ut.transaction_id\n" +
                    "AND t.transaction_id = tc.transaction_id\n" +
                    "AND ut.session_id = ?\n" +
                    "AND tc.category_id = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String LINK_USER_TO_TRANSACTION =
            "INSERT INTO User_Transaction (session_id, transaction_id)\n" +
                    "VALUES (?, ?);";
    private static final String UNLINK_USER_FROM_TRANSACTION =
            "DELETE FROM User_Transaction\n" +
                    "WHERE session_id = ?\n" +
                    "AND transaction_id = ?;";
    private static final String CREATE_CATEGORY =
            "INSERT INTO Category (name)\n" +
                    "VALUES (?);";
    private static final String GET_CATEGORY =
            "SELECT category_id, name\n" +
                    "FROM Category\n" +
                    "WHERE category_id = ?;";
    private static final String GET_CATEGORY_BY_SESSION_ID =
            "SELECT c.category_id, c.name\n" +
                    "FROM Category c, User_Category uc\n" +
                    "WHERE c.category_id = uc.category_id\n" +
                    "AND uc.session_id = ?\n" +
                    "AND c.category_id = ?;";
    private static final String GET_NEWEST_CATEGORY_ID =
            "SELECT MAX(category_id)\n" +
                    "FROM Category;";
    private static final String UPDATE_CATEGORY_NAME =
            "UPDATE Category\n" +
                    "SET name = ?\n" +
                    "WHERE category_id = ?;";
    private static final String DELETE_CATEGORY =
            "DELETE FROM Category\n" +
                    "WHERE category_id = ?;";
    private static final String GET_CATEGORIES =
            "SELECT c.category_id, c.name\n" +
                    "FROM Category c, User_Category uc\n" +
                    "WHERE c.category_id = uc.category_id\n" +
                    "AND uc.session_id = ?\n" +
                    "LIMIT ?\n" +
                    "OFFSET ?;";
    private static final String LINK_TRANSACTION_TO_CATEGORY =
            "INSERT INTO Transaction_Category (transaction_id, category_id)\n" +
                    "VALUES (?, ?);";
    private static final String UNLINK_TRANSACTION_FROM_CATEGORY =
            "DELETE FROM Transaction_Category\n" +
                    "WHERE transaction_id = ?\n" +
                    "AND category_id = ?;";
    private static final String UNLINK_TRANSACTION_FROM_ALL_CATEGORIES =
            "DELETE FROM Transaction_Category\n" +
                    "WHERE transaction_id = ?;";
    private static final String UNLINK_CATEGORY_FROM_ALL_TRANSACTIONS =
            "DELETE FROM Transaction_Category\n" +
                    "WHERE category_id = ?;";
    private static final String LINK_USER_TO_CATEGORY =
            "INSERT INTO User_Category (session_id, category_id)\n" +
                    "VALUES (?, ?);";
    private static final String UNLINK_USER_FROM_CATEGORY =
            "DELETE FROM User_Category\n" +
                    "WHERE session_id = ?\n" +
                    "AND category_id = ?;";
    private static final String GET_CATEGORY_ID_BY_TRANSACTION_ID =
            "SELECT tc.category_id\n" +
                    "FROM Money_Transaction t, Transaction_Category tc\n" +
                    "WHERE t.transaction_id = tc.transaction_id\n" +
                    "AND t.transaction_id = ?;";

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
     * Method used to insert a Transaction into the database.
     *
     * @param name   The name of the to be inserted Transaction.
     * @param amount The amount (in cents) of the to be inserted Transaction.
     */
    public void createTransaction(String name, long amount) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_TRANSACTION);
            statement.setString(1, name);
            statement.setLong(2, amount);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a Transaction from the database.
     *
     * @param transactionID The id of the to be retrieved Transaction.
     * @return A Transaction object containing data retrieved from the database.
     */
    public Transaction getTransaction(int transactionID) {
        Transaction transaction = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTION);
            statement.setInt(1, transactionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString(2);
                long amount = resultSet.getLong(3);
                transaction = new Transaction(transactionID, name, amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    /**
     * Method used to retrieve a Transaction from the database,
     * on the condition that the user has rights to retrieve the Transaction.
     *
     * @param sessionID     The sessionID of the user.
     *                      Used to check whether the user has rights to retrieve the Transaction.
     * @param transactionID The id of the to be retrieved Transaction.
     * @return A Transaction object containing data retrieved from the database.
     */
    public Transaction getTransactionBySessionID(String sessionID, int transactionID) {
        Transaction transaction = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTION_BY_SESSION_ID);
            statement.setString(1, sessionID);
            statement.setInt(2, transactionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString(2);
                long amount = resultSet.getLong(3);
                transaction = new Transaction(transactionID, name, amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    /**
     * Method used to retrieve the highest Transaction id from the database.
     *
     * @return The highest Transaction id from the database.
     */
    public int getNewestTransactionID() {
        int transactionID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_NEWEST_TRANSACTION_ID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                transactionID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionID;
    }

    /**
     * Method used to change the name of a Transaction in the database.
     *
     * @param name          The new name of the Transaction.
     * @param transactionID The id of the to be updated Transaction.
     */
    public void updateTransactionName(String name, int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_NAME);
            statement.setString(1, name);
            statement.setInt(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to update the amount of a Transaction in the database.
     *
     * @param amount        The new amount (in cents) of the Transaction.
     * @param transactionID The id of the to be updated transaction.
     */
    public void updateTransactionAmount(long amount, int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_AMOUNT);
            statement.setLong(1, amount);
            statement.setInt(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to delete a Transaction from the database.
     *
     * @param transactionID The id of the to be deleted Transaction.
     */
    public void deleteTransaction(int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_TRANSACTION);
            statement.setInt(1, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of Transaction objects belonging to a certain user from the database.
     *
     * @param sessionID The sessionID of the user to who the to be retrieved Transaction objects belong.
     * @param limit     The (maximum) amount of Transaction objects to be retrieved.
     * @param offset    The starting index to retrieve Transaction objects.
     * @return An ArrayList of Transaction objects.
     */
    public ArrayList<Transaction> getTransactions(String sessionID, int limit, int offset) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS);
            statement.setString(1, sessionID);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int transactionID = resultSet.getInt(1);
                String name = resultSet.getString(2);
                long amount = resultSet.getLong(3);
                transactions.add(new Transaction(transactionID, name, amount));
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
     * @param sessionID  The sessionID of the user to who the to be retrieved Transaction objects belong.
     * @param categoryID The id of the Category to which the retrieved Transaction objects belong.
     * @param limit      The (maximum) amount of Transaction objects to be retrieved.
     * @param offset     The starting index to retrieve Transaction objects.
     * @return An ArrayList of Transaction objects.
     */
    public ArrayList<Transaction> getTransactionsByCategoryID(String sessionID, int categoryID, int limit, int offset) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS_BY_CATEGORY_ID);
            statement.setString(1, sessionID);
            statement.setInt(2, categoryID);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int transactionID = resultSet.getInt(1);
                String name = resultSet.getString(2);
                long amount = resultSet.getLong(3);
                transactions.add(new Transaction(transactionID, name, amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Method used to link a Transaction to a certain user in the database.
     *
     * @param sessionID     The sessionID of the certain user that will be linked to a Transaction.
     * @param transactionID The id of the Transaction to which the user will be linked.
     */
    public void linkUserToTransaction(String sessionID, int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(LINK_USER_TO_TRANSACTION);
            statement.setString(1, sessionID);
            statement.setInt(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Transaction from a certain user in the database.
     *
     * @param sessionID     The sessionID of the certain user that will be unlinked from the Transaction.
     * @param transactionID The id of the Transaction from which the user will be unlinked.
     */
    public void unlinkUserFromTransaction(String sessionID, int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_USER_FROM_TRANSACTION);
            statement.setString(1, sessionID);
            statement.setInt(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to insert a new Category into the database.
     *
     * @param name The name of the to be inserted Category.
     */
    public void createCategory(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a Category from the database.
     *
     * @param categoryID The id of the to be retrieved Category.
     * @return A Category object containing data retrieved from the database.
     */
    public Category getCategory(int categoryID) {
        Category category = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY);
            statement.setInt(1, categoryID);
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
     * Method used to retrieve a Category from the database,
     * on the condition that the user has rights to retrieve the Category.
     *
     * @param sessionID  The sessionID of the user.
     *                   Used to check whether the user has rights to retrieve the Category.
     * @param categoryID The id of the to be retrieved Category.
     * @return A Category object containing data retrieved from the database.
     */
    public Category getCategoryBySessionID(String sessionID, int categoryID) {
        Category category = null;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY_BY_SESSION_ID);
            statement.setString(1, sessionID);
            statement.setInt(2, categoryID);
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
     * Method used to retrieve the highest Category id from the database.
     *
     * @return The highest Category id from the database.
     */
    public int getNewestCategoryID() {
        int categoryID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_NEWEST_CATEGORY_ID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                categoryID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryID;
    }

    /**
     * Method used to update the name of a Category in the database.
     *
     * @param name       The new name of the to be updated Category.
     * @param categoryID The id of the to be updated Category.
     */
    public void updateCategoryName(String name, int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY_NAME);
            statement.setString(1, name);
            statement.setInt(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to delete a Category from the database.
     *
     * @param categoryID The id of the to be deleted Category.
     */
    public void deleteCategory(int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY);
            statement.setInt(1, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve a batch of Category objects belonging to a certain user from the database.
     *
     * @param sessionID The sessionID of the user to who the to be retrieved Category objects belong.
     * @param limit     The (maximum) amount of Category objects to be retrieved.
     * @param offset    The starting index to retrieve Category objects.
     * @return An ArrayList of Category objects.
     */
    public ArrayList<Category> getCategories(String sessionID, int limit, int offset) {
        ArrayList<Category> categories = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORIES);
            statement.setString(1, sessionID);
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
     * @param transactionID The id of the Transaction that will be linked to a Category.
     * @param categoryID    The id of the Category to which the Transaction will be linked.
     */
    public void linkTransactionToCategory(int transactionID, int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(LINK_TRANSACTION_TO_CATEGORY);
            statement.setInt(1, transactionID);
            statement.setInt(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Transaction from a Category in the database.
     *
     * @param transactionID The id of the Transaction that will be unlinked from a Category.
     * @param categoryID    The id of the Category from which the Transaction will be unlinked.
     */
    public void unlinkTransactionFromCategory(int transactionID, int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_TRANSACTION_FROM_CATEGORY);
            statement.setInt(1, transactionID);
            statement.setInt(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Transaction from all Category objects in the database.
     *
     * @param transactionID The id of the Transaction that will be unlinked from all Category objects in the database.
     */
    public void unlinkTransactionFromAllCategories(int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_TRANSACTION_FROM_ALL_CATEGORIES);
            statement.setInt(1, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Category from all Transaction objects in the database.
     *
     * @param categoryID The id of the Category that will be unlinked from all Transaction objects in the database.
     */
    public void unlinkCategoryFromAllTransactions(int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_CATEGORY_FROM_ALL_TRANSACTIONS);
            statement.setInt(1, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to link a Category to a certain user in the database.
     *
     * @param sessionID  The sessionID of the certain user that will be linked to a Category.
     * @param categoryID The id of the Category to which the user will be linked.
     */
    public void linkUserToCategory(String sessionID, int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(LINK_USER_TO_CATEGORY);
            statement.setString(1, sessionID);
            statement.setInt(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to unlink a Category from a certain user in the database.
     *
     * @param sessionID  The sessionID of the certain user that will be unlinked from a Category.
     * @param categoryID The id of the Category from which the user will be unlinked.
     */
    public void unlinkUserFromCategory(String sessionID, int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(UNLINK_USER_FROM_CATEGORY);
            statement.setString(1, sessionID);
            statement.setInt(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to retrieve the id of the Category that is linked to a certain Transaction.
     *
     * @param transactionID The id of the Transaction from which the linked Category id will be retrieved.
     * @return The id of the Category that is linked to the Transaction.
     */
    public int getCategoryIDByTransactionID(int transactionID) {
        int categoryID = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(GET_CATEGORY_ID_BY_TRANSACTION_ID);
            statement.setInt(1, transactionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                categoryID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryID;
    }

}
