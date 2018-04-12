package nl.utwente.ing.model;

import nl.utwente.ing.exception.InvalidSessionIDException;
import nl.utwente.ing.exception.ResourceNotFoundException;
import nl.utwente.ing.model.bean.Category;
import nl.utwente.ing.model.bean.Session;
import nl.utwente.ing.model.bean.Transaction;

import java.util.ArrayList;

/**
 * The Model interface.
 * Consists of method specifications to facilitate the controller part of the application to interact with
 * transactions and categories.
 *
 * @author Daan Kooij
 */
public interface Model {

    /**
     * Method used to retrieve the transactions belonging to a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryName The category to be filtered on (empty String if no filter).
     * @param limit        The maximum amount of transactions to be fetched.
     * @param offset       The starting index to fetch transactions.
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    ArrayList<Transaction> getTransactions(String sessionID, String categoryName, int limit, int offset)
            throws InvalidSessionIDException;

    /**
     * Method used to create a new Transaction for a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param date         The date of the to be created Transaction.
     * @param amount       The amount of the to be created Transaction.
     * @param externalIBAN The external IBAN of the to be created Transaction.
     * @param type         The type of the to be created Transaction.
     * @param categoryID   The categoryID of the Category that will be assigned to the to be created Transaction
     *                     (0 if no Category).
     * @return The Transaction created by this method.
     */
    Transaction postTransaction(String sessionID, String date, float amount, String externalIBAN, String type,
                                long categoryID) throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to retrieve a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user with sessionID.
     */
    Transaction getTransaction(String sessionID, long transactionID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to update a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param date          The new date of the to be updated Transaction.
     * @param amount        The new amount of the to be updated Transaction.
     * @param externalIBAN  The new external IBAN of the to be updated Transaction.
     * @param type          The new type of the to be updated Transaction.
     * @param categoryID    The new categoryID of the Category that will be assigned to the to be updated Transaction
     *                      (0 if no Category).
     * @return The Transaction updated by this method.
     */
    Transaction putTransaction(String sessionID, long transactionID, String date, float amount, String externalIBAN,
                               String type, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to delete a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    void deleteTransaction(String sessionID, long transactionID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to assign a Category to an Transaction.
     * Currently, first all other assigned Category objects to Transaction are unassigned.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryID    The categoryID of the Category which will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    Transaction assignCategoryToTransaction(String sessionID, long transactionID, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to retrieve the categories belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param limit     The maximum amount of categories to be fetched.
     * @param offset    The starting index to fetch categories.
     * @return An ArrayList of Category belonging to the user with sessionID.
     */
    ArrayList<Category> getCategories(String sessionID, int limit, int offset) throws InvalidSessionIDException;

    /**
     * Method used to create a new Category for a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param name      The name of the to be created category.
     * @return The Category created by this method.
     */
    Category postCategory(String sessionID, String name) throws InvalidSessionIDException;

    /**
     * Method used to retrieve a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be retrieved.
     * @return The Category with categoryID belonging to the user with sessionID.
     */
    Category getCategory(String sessionID, long categoryID) throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to update a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be updated.
     * @param name       The new name of the to be updated Category.
     * @return The Category updated by this method.
     */
    Category putCategory(String sessionID, long categoryID, String name)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to remove a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be deleted.
     */
    void deleteCategory(String sessionID, long categoryID) throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to create and retrieve a new Session.
     *
     * @return A new Session.
     */
    Session getSession();

}
