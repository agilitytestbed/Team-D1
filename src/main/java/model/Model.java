package model;

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
     * Method used to create a new transaction for a certain user.
     * @param sessionID The sessionID of the user.
     * @param name The name of the to be created Transaction.
     * @param amount The amount (in cents) of the to be created Transaction.
     * @return The Transaction created by this method.
     */
    public Transaction postTransaction(String sessionID, String name, String amount);

    /**
     * Method used to retrieve a certain transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user with sessionID.
     */
    public Transaction getTransaction(String sessionID, String transactionID);

    /**
     * Method used to update a certain transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param name The new name of the to be updated Transaction.
     * @param amount The new amount (in cents) of the to be updated Transaction.
     * @return The Transaction updated by this method.
     */
    public Transaction putTransaction(String sessionID, String transactionID, String name, String amount);

    /**
     * Method used to remove a certain transaction of a certain user
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String sessionID, String transactionID);

}
