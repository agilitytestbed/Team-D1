package model.persistentmodel;

import model.Model;
import model.bean.Category;
import model.bean.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * The PersistentModel class, an implementation of the Model interface.
 * Implements the methods specified in the Model interface using persistent storage methods, meaning that the data
 * stored using the persistent model will exist over multiple executions of the application.
 * @author Daan Kooij
 */
public class PersistentModel implements Model {

    private SQLModel sqlModel;

    public PersistentModel() {
        this.sqlModel = new SQLModel();
    }

    /**
     * Method used to retrieve the transactions belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param category  The category to be filtered on (empty String if no filter).
     * @param limit     The maximum amount of transactions to be fetched.
     * @param offset    The starting index to fetch transactions.
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    public ArrayList<Transaction> getTransactions(String sessionID, String category, String limit, String offset) {
        return sqlModel.getTransactions(sessionID, category, limit, offset);
    }

    /**
     * Method used to create a new Transaction for a certain user.
     * @param sessionID The sessionID of the user.
     * @param name      The name of the to be created Transaction.
     * @param amount    The amount (in cents) of the to be created Transaction.
     * @return The Transaction created by this method.
     */
    public Transaction postTransaction(String sessionID, String name, String amount) {
        Transaction transaction = sqlModel.postTransaction(name, amount);
        int transactionID = transaction.getTransactionID();
        sqlModel.assignTransactionToUser(sessionID, transactionID);
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
        return sqlModel.getTransaction(sessionID, transactionID);
    }

    /**
     * Method used to update a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param name          The new name of the to be updated Transaction.
     * @param amount        The new amount (in cents) of the to be updated Transaction.
     * @return The Transaction updated by this method.
     */
    public Transaction putTransaction(String sessionID, String transactionID, String name, String amount) {
        return sqlModel.putTransaction(sessionID, transactionID, name, amount);
    }

    /**
     * Method used to remove a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String sessionID, String transactionID) {
        sqlModel.deleteTransaction(sessionID, transactionID);
    }

    /**
     * Method used to assign a Category to an Transaction.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryID    The categoryID of the Category which will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    public Transaction assignCategoryToTransaction(String sessionID, String transactionID, String categoryID) {
        return sqlModel.assignCategoryToTransaction(sessionID, transactionID, categoryID);
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
        return sqlModel.getCategories(sessionID, limit, offset);
    }

    /**
     * Method used to create a new Category for a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryName The name of the to be created category.
     * @return The Category created by this method.
     */
    public Category postCategory(String sessionID, String categoryName) {
        Category category = sqlModel.postCategory(sessionID, categoryName);
        int categoryID = category.getCategoryID();
        sqlModel.assignCategoryToUser(sessionID, categoryID);
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
        return sqlModel.getCategory(sessionID, categoryID);
    }

    /**
     * Method used to update a certain Category of a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryID   The categoryID of the Category that will be updated.
     * @param categoryName The new name of the to be updated Category.
     * @return The Category updated by this method.
     */
    public Category putCategory(String sessionID, String categoryID, String categoryName) {
        return sqlModel.putCategory(sessionID, categoryID, categoryName);
    }

    /**
     * Method used to remove a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be deleted.
     */
    public void deleteCategory(String sessionID, String categoryID) {
        sqlModel.deleteCategory(sessionID, categoryID);
    }

}
