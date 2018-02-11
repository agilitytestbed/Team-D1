package model.volatilemodel;

import model.bean.Category;
import model.Model;
import model.bean.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The VolatileModel class, an implementation of the Model interface.
 * Implements the methods specified in the Model interface using volatile storage methods, meaning that the data
 * stored using the volatile model will be discarded once the application terminates.
 * @author Daan Kooij
 */
public class VolatileModel implements Model {

    private Map<String, UserModel> userModelMap;

    /**
     * Constructor of the VolatileModel.
     * Initializes userModelMap.
     */
    public VolatileModel() {
        userModelMap = new HashMap<>();
    }

    /**
     * Method used to retrieve the UserModel belonging to a certain sessionID.
     * If UserModel with sessionID does not yet exist, it will be created.
     * @param sessionID The sessionID of the user.
     * @return The UserModel belonging to the user with sessionID.
     */
    private UserModel getUserModel(String sessionID) {
        if (!userModelMap.containsKey(sessionID)) {
            userModelMap.put(sessionID, new UserModel());
        }
        return userModelMap.get(sessionID);
    }

    /**
     * Method used to retrieve the transactions belonging to a certain user.
     * @param sessionID The sessionID of the user.
     * @param category The category to be filtered on (empty String if no filter).
     * @param limit The maximum amount of transactions to be fetched.
     * @param offset The starting index to fetch transactions.
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    @Override
    public ArrayList<Transaction> getTransactions(String sessionID, String category, String limit, String offset) {
        return this.getUserModel(sessionID).getTransactions(category, limit, offset);
    }

    /**
     * Method used to create a new Transaction for a certain user.
     * @param sessionID The sessionID of the user.
     * @param name The name of the to be created Transaction.
     * @param amount The amount (in cents) of the to be created Transaction.
     * @return The Transaction created by this method.
     */
    @Override
    public Transaction postTransaction(String sessionID, String name, String amount) {
        return this.getUserModel(sessionID).postTransaction(name, amount);
    }

    /**
     * Method used to retrieve a certain Transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user with sessionID.
     */
    @Override
    public Transaction getTransaction(String sessionID, String transactionID) {
        return this.getUserModel(sessionID).getTransaction(transactionID);
    }

    /**
     * Method used to update a certain Transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param name The new name of the to be updated Transaction.
     * @param amount The new amount (in cents) of the to be updated Transaction.
     * @return The Transaction updated by this method.
     */
    @Override
    public Transaction putTransaction(String sessionID, String transactionID, String name, String amount) {
        return this.getUserModel(sessionID).putTransaction(transactionID, name, amount);
    }

    /**
     * Method used to remove a certain Transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    @Override
    public void deleteTransaction(String sessionID, String transactionID) {
        this.getUserModel(sessionID).deleteTransaction(transactionID);
    }

    /**
     * Method used to assign a Category to an Transaction.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryID The categoryID of the Category which will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    @Override
    public Transaction assignCategoryToTransaction(String sessionID, String transactionID, String categoryID) {
        return this.getUserModel(sessionID).assignCategoryToTransaction(transactionID, categoryID);
    }

    /**
     * Method used to retrieve the categories belonging to a certain user.
     * @param sessionID The sessionID of the user.
     * @param limit The maximum amount of categories to be fetched.
     * @param offset The starting index to fetch categories.
     * @return An ArrayList of Category belonging to the user with sessionID.
     */
    @Override
    public ArrayList<Category> getCategories(String sessionID, String limit, String offset) {
        return this.getUserModel(sessionID).getCategories(limit, offset);
    }

    /**
     * Method used to create a new Category for a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryName The name of the to be created category.
     * @return The Category created by this method.
     */
    @Override
    public Category postCategory(String sessionID, String categoryName) {
        return this.getUserModel(sessionID).postCategory(categoryName);
    }

    /**
     * Method used to retrieve a certain Category of a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be retrieved.
     * @return The Category with categoryID belonging to the user with sessionID.
     */
    @Override
    public Category getCategory(String sessionID, String categoryID) {
        return this.getUserModel(sessionID).getCategory(categoryID);
    }

    /**
     * Method used to update a certain Category of a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be updated.
     * @param categoryName The new name of the to be updated Category.
     * @return The Category updated by this method.
     */
    @Override
    public Category putCategory(String sessionID, String categoryID, String categoryName) {
        return this.getUserModel(sessionID).putCategory(categoryID, categoryName);
    }

    /**
     * Method used to remove a certain Category of a certain user.
     * @param sessionID The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be deleted.
     */
    @Override
    public void deleteCategory(String sessionID, String categoryID) {
        this.getUserModel(sessionID).deleteCategory(categoryID);
    }

}
