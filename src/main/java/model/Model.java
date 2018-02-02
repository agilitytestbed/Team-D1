package model;

import model.volatilemodel.Category;
import model.volatilemodel.Transaction;

import java.util.ArrayList;

/**
 * The Model interface.
 * Consists of method specifications to facilitate the controller part of the application to interact with
 * transactions and categories.
 * @author Daan Kooij
 */
public interface Model {

    /**
     * Method used to retrieve the transactions belonging to a certain user.
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    public ArrayList<Transaction> getTransactions(String sessionID);

    /**
     * Method used to create a new Transaction for a certain user.
     * @param sessionID The sessionID of the user.
     * @param name The name of the to be created Transaction.
     * @param amount The amount (in cents) of the to be created Transaction.
     * @return The Transaction created by this method.
     */
    public Transaction postTransaction(String sessionID, String name, String amount);

    /**
     * Method used to retrieve a certain Transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user with sessionID.
     */
    public Transaction getTransaction(String sessionID, String transactionID);

    /**
     * Method used to update a certain Transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param name The new name of the to be updated Transaction.
     * @param amount The new amount (in cents) of the to be updated Transaction.
     * @return The Transaction updated by this method.
     */
    public Transaction putTransaction(String sessionID, String transactionID, String name, String amount);

    /**
     * Method used to remove a certain Transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String sessionID, String transactionID);

    /**
     * Method used to assign a Category to an Transaction.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryID The categoryID of the Category which will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    public Transaction assignCategoryToTransaction(String sessionID, String transactionID, String categoryID);

    /**
     * Method used to retrieve the categories belonging to a certain user.
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of Category belonging to the user with sessionID.
     */
    public ArrayList<Category> getCategories(String sessionID);

    /**
     * Method used to create a new Category for a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryName The name of the to be created category.
     * @return The Category created by this method.
     */
    public Category postCategory(String sessionID, String categoryName);

    /**
     * Method used to retrieve a certain Category of a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be retrieved.
     * @return The Category with categoryID belonging to the user with sessionID.
     */
    public Category getCategory(String sessionID, String categoryID);

    /**
     * Method used to update a certain Category of a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be updated.
     * @param categoryName The new name of the to be updated Category.
     * @return The Category updated by this method.
     */
    public Category putCategory(String sessionID, String categoryID, String categoryName);

    /**
     * Method used to remove a certain Category of a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be deleted.
     */
    public void deleteCategory(String sessionID, String categoryID);

}
