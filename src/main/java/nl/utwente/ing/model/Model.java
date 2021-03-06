package nl.utwente.ing.model;

import nl.utwente.ing.exception.InvalidSessionIDException;
import nl.utwente.ing.exception.ResourceNotFoundException;
import nl.utwente.ing.misc.date.IntervalPeriod;
import nl.utwente.ing.model.bean.*;

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
     * Method used to create and retrieve a new Session.
     *
     * @return A new Session.
     */
    Session getSession();

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
     * @param description  The description of the to be created Transaction.
     * @param externalIBAN The external IBAN of the to be created Transaction.
     * @param type         The type of the to be created Transaction.
     * @param categoryID   The categoryID of the Category that will be assigned to the to be created Transaction
     *                     (0 if no Category).
     * @return The Transaction created by this method.
     */
    Transaction postTransaction(String sessionID, String date, float amount, String description, String externalIBAN,
                                String type, long categoryID) throws InvalidSessionIDException, ResourceNotFoundException;

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
     * @param description   The new description of the to be updated Transaction.
     * @param externalIBAN  The new external IBAN of the to be updated Transaction.
     * @param type          The new type of the to be updated Transaction.
     * @param categoryID    The new categoryID of the Category that will be assigned to the to be updated Transaction
     *                      (0 if no Category).
     * @return The Transaction updated by this method.
     */
    Transaction putTransaction(String sessionID, long transactionID, String date, float amount, String description,
                               String externalIBAN, String type, long categoryID)
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
     * Method used to retrieve the CategoryRules belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of CategoryRules belonging to the user with sessionID.
     */
    ArrayList<CategoryRule> getCategoryRules(String sessionID) throws InvalidSessionIDException;

    /**
     * Method used to create a new CategoryRule for a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryRule The CategoryRule object to be used to create the new CategoryRule.
     * @return The CategoryRule created by this method.
     */
    CategoryRule postCategoryRule(String sessionID, CategoryRule categoryRule) throws InvalidSessionIDException;

    /**
     * Method used to retrieve a certain CategoryRule of a certain user.
     *
     * @param sessionID      The sessionID of the user.
     * @param categoryRuleID The categoryRuleID of the CategoryRule that will be retrieved.
     * @return The CategoryRule with categoryRuleID belonging to the user with sessionID.
     */
    CategoryRule getCategoryRule(String sessionID, long categoryRuleID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to update a certain CategoryRule of a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryRule The CategoryRule object that will be used to update the CategoryRule with ID of this object.
     * @return The CategoryRule updated by this method.
     */
    CategoryRule putCategoryRule(String sessionID, CategoryRule categoryRule)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to remove a certain CategoryRule of a certain user.
     *
     * @param sessionID      The sessionID of the user.
     * @param categoryRuleID The categoryRuleID of the CategoryRule that will be deleted.
     */
    void deleteCategoryRule(String sessionID, long categoryRuleID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to retrieve balance history information of a certain user in the form of a list of
     * BalanceCandlesticks.
     *
     * @param sessionID      The sessionID of the user.
     * @param intervalPeriod The IntervalPeriod specifying the span of intervals.
     * @param amount         The amount of intervals for which BalanceCandlesticks should be generated.
     * @return The balance history information of a certain user in the form of a list of BalanceCandlesticks.
     */
    ArrayList<BalanceCandlestick> getBalanceHistory(String sessionID, IntervalPeriod intervalPeriod, int amount)
            throws InvalidSessionIDException;

    /**
     * Method used to retrieve the SavingGoals belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of SavingGoals belonging to the user with sessionID.
     */
    ArrayList<SavingGoal> getSavingGoals(String sessionID) throws InvalidSessionIDException;

    /**
     * Method used to create a new SavingGoal for a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param savingGoal The SavingGoal object to be used to create the new SavingGoal.
     * @return The SavingGoal created by this method.
     */
    SavingGoal postSavingGoal(String sessionID, SavingGoal savingGoal) throws InvalidSessionIDException;

    /**
     * Method used to remove a certain SavingGoal of a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param savingGoalID The savingGoalID of the SavingGoal that will be deleted.
     */
    void deleteSavingGoal(String sessionID, long savingGoalID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to retrieve the PaymentRequests belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of PaymentRequest belonging to the user with sessionID.
     */
    ArrayList<PaymentRequest> getPaymentRequests(String sessionID) throws InvalidSessionIDException;

    /**
     * Method used to create a new PaymentRequest for a certain user.
     *
     * @param sessionID      The sessionID of the user.
     * @param paymentRequest The PaymentRequest object to be used to create the new PaymentRequest.
     * @return The PaymentRequest created by this method.
     */
    PaymentRequest postPaymentRequest(String sessionID, PaymentRequest paymentRequest) throws InvalidSessionIDException;

    /**
     * Method used to retrieve the unread UserMessages belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of UserMessages belonging to the user with sessionID.
     */
    ArrayList<UserMessage> getUnreadUserMessages(String sessionID) throws InvalidSessionIDException;

    /**
     * Method used to indicate that a certain UserMessage of a certain user is read.
     *
     * @param sessionID     The sessionID of the user.
     * @param userMessageID The ID of the UserMessage of the certain user that should be marked as read.
     */
    void setUserMessageRead(String sessionID, long userMessageID)
            throws InvalidSessionIDException, ResourceNotFoundException;

    /**
     * Method used to create a new MessageRule for a certain user.
     *
     * @param sessionID   The sessionID of the user.
     * @param messageRule The MessageRule object to be used to create the new MessageRule.
     * @return The MessageRule created by this method.
     */
    MessageRule postMessageRule(String sessionID, MessageRule messageRule) throws InvalidSessionIDException;

}
