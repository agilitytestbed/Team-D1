package model.persistentmodel;

import model.Model;
import model.bean.Category;
import model.bean.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The PersistentModel class, an implementation of the Model interface.
 * Implements the methods specified in the Model interface using persistent storage methods, meaning that the data
 * stored using the persistent model will exist over multiple executions of the application.
 *
 * @author Daan Kooij
 */
public class PersistentModel implements Model {

    private Connection connection;
    private CustomORM customORM;

    /**
     * The constructor of PersistentModel.
     * Retrieves the database connection from the DatabaseConnection class and initializes a CustomORM object.
     */
    public PersistentModel() {
        this.connection = DatabaseConnection.getDatabaseConnection();
        this.customORM = new CustomORM(connection);
    }

    /**
     * Method used to retrieve the transactions belonging to a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID to be filtered on (empty String if no filter).
     * @param limit      The maximum amount of transactions to be fetched.
     * @param offset     The starting index to fetch transactions.
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    public ArrayList<Transaction> getTransactions(String sessionID, String categoryID, String limit, String offset) {
        ArrayList<Transaction> transactions;
        if (categoryID.equals("")) {
            transactions = customORM.getTransactions(sessionID, Integer.parseInt(limit), Integer.parseInt(offset));
        } else {
            transactions = customORM.getTransactionsByCategoryID(sessionID, Integer.parseInt(categoryID),
                    Integer.parseInt(limit), Integer.parseInt(offset));
        }
        for (Transaction transaction : transactions) {
            this.populateCategory(transaction);
        }
        return transactions;
    }

    /**
     * Method used to create a new Transaction for a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param name      The name of the to be created Transaction.
     * @param amount    The amount (in cents) of the to be created Transaction.
     * @return The Transaction created by this method.
     */
    public Transaction postTransaction(String sessionID, String name, String amount) {
        Transaction transaction = null;
        try {
            connection.setAutoCommit(false);
            customORM.createTransaction(name, Long.parseLong(amount));
            int transactionID = customORM.getNewestTransactionID();
            connection.commit();
            connection.setAutoCommit(true);
            customORM.linkUserToTransaction(sessionID, transactionID);
            transaction = customORM.getTransaction(transactionID);
            this.populateCategory(transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    /**
     * Method used to retrieve a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user with sessionID.
     */
    public Transaction getTransaction(String sessionID, String transactionID) {
        return customORM.getTransactionBySessionID(sessionID, Integer.parseInt(transactionID));
    }

    /**
     * Method used to update a certain Transaction of a certain user.
     *
     * @param sessionID           The sessionID of the user.
     * @param transactionIDString The transactionID of the Transaction that will be updated.
     * @param name                The new name of the to be updated Transaction.
     * @param amount              The new amount (in cents) of the to be updated Transaction.
     * @return The Transaction updated by this method.
     */
    public Transaction putTransaction(String sessionID, String transactionIDString, String name, String amount) {
        int transactionID = Integer.parseInt(transactionIDString);
        Transaction transaction = customORM.getTransactionBySessionID(sessionID, transactionID);
        if (transaction != null) {
            if (!name.equals("")) {
                customORM.updateTransactionName(name, transactionID);
            }
            if (!amount.equals("")) {
                customORM.updateTransactionAmount(Long.parseLong(amount), transactionID);
            }
            transaction = customORM.getTransaction(transactionID);
            this.populateCategory(transaction);
            return transaction;
        } else {
            return null;
        }
    }

    /**
     * Method used to remove a certain Transaction of a certain user.
     *
     * @param sessionID           The sessionID of the user.
     * @param transactionIDString The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String sessionID, String transactionIDString) {
        int transactionID = Integer.parseInt(transactionIDString);
        Transaction transaction = customORM.getTransactionBySessionID(sessionID, transactionID);
        if (transaction != null) {
            customORM.unlinkTransactionFromAllCategories(transactionID);
            customORM.unlinkUserFromTransaction(sessionID, transactionID);
            customORM.deleteTransaction(transactionID);
        }
    }

    /**
     * Method used to assign a Category to an Transaction.
     *
     * @param sessionID           The sessionID of the user.
     * @param transactionIDString The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryIDString    The categoryIDString of the Category which will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    public Transaction assignCategoryToTransaction(String sessionID, String transactionIDString, String categoryIDString) {
        int transactionID = Integer.parseInt(transactionIDString);
        Transaction transaction = customORM.getTransactionBySessionID(sessionID, transactionID);
        if (!transaction.equals(null)) {
            int categoryID = Integer.parseInt(categoryIDString);
            Category category = customORM.getCategoryBySessionID(sessionID, categoryID);
            if (!category.equals(null)) {
                customORM.unlinkTransactionFromAllCategories(transactionID);
                customORM.linkTransactionToCategory(transactionID, categoryID);
                transaction.setCategory(category);
                return transaction;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Method used to retrieve the categories belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param limit     The maximum amount of categories to be fetched.
     * @param offset    The starting index to fetch categories.
     * @return An ArrayList of Category belonging to the user with sessionID.
     */
    public ArrayList<Category> getCategories(String sessionID, String limit, String offset) {
        return customORM.getCategories(sessionID, Integer.parseInt(limit), Integer.parseInt(offset));
    }

    /**
     * Method used to create a new Category for a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param name      The name of the to be created category.
     * @return The Category created by this method.
     */
    public Category postCategory(String sessionID, String name) {
        Category category = null;
        try {
            connection.setAutoCommit(false);
            customORM.createCategory(name);
            int categoryID = customORM.getNewestCategoryID();
            connection.commit();
            connection.setAutoCommit(true);
            customORM.linkUserToCategory(sessionID, categoryID);
            category = customORM.getCategory(categoryID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    /**
     * Method used to retrieve a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be retrieved.
     * @return The Category with categoryID belonging to the user with sessionID.
     */
    public Category getCategory(String sessionID, String categoryID) {
        return customORM.getCategoryBySessionID(sessionID, Integer.parseInt(categoryID));
    }

    /**
     * Method used to update a certain Category of a certain user.
     *
     * @param sessionID        The sessionID of the user.
     * @param categoryIDString The categoryID of the Category that will be updated.
     * @param name             The new name of the to be updated Category.
     * @return The Category updated by this method.
     */
    public Category putCategory(String sessionID, String categoryIDString, String name) {
        int categoryID = Integer.parseInt(categoryIDString);
        Category category = customORM.getCategoryBySessionID(sessionID, categoryID);
        if (category != null) {
            if (!name.equals("")) {
                customORM.updateCategoryName(name, categoryID);
            }
            category = customORM.getCategory(categoryID);
        }
        return category;
    }

    /**
     * Method used to remove a certain Category of a certain user.
     *
     * @param sessionID        The sessionID of the user.
     * @param categoryIDString The categoryID of the Category that will be deleted.
     */
    public void deleteCategory(String sessionID, String categoryIDString) {
        int categoryID = Integer.parseInt(categoryIDString);
        Category category = customORM.getCategoryBySessionID(sessionID, categoryID);
        if (category != null) {
            customORM.unlinkCategoryFromAllTransactions(categoryID);
            customORM.unlinkUserFromCategory(sessionID, categoryID);
            customORM.deleteCategory(categoryID);
        }
    }

    /**
     * Method used to populate a Transaction object with a Category object.
     *
     * @param transaction The Transaction object that will be populated by a Category object.
     */
    private void populateCategory(Transaction transaction) {
        int categoryID = customORM.getCategoryIDByTransactionID(transaction.getTransactionID());
        Category category = customORM.getCategory(categoryID);
        transaction.setCategory(category);
    }

}
