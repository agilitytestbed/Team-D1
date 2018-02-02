package model.volatilemodel;

import model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The VolataleModel class, an implementation of the Model interface.
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
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    @Override
    public ArrayList<Transaction> getTransactions(String sessionID) {
        return this.getUserModel(sessionID).getTransactions();
    }

    /**
     * Method used to create a new transaction for a certain user.
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
     * Method used to retrieve a certain transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user with sessionID.
     */
    @Override
    public Transaction getTransaction(String sessionID, String transactionID) {
        return this.getUserModel(sessionID).getTransaction(transactionID);
    }

    /**
     * Method used to update a certain transaction of a certain user.
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
     * Method used to remove a certain transaction of a certain user.
     * @param sessionID The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    @Override
    public void deleteTransaction(String sessionID, String transactionID) {
        this.getUserModel(sessionID).deleteTransaction(transactionID);
    }

}
