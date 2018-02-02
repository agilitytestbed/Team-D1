package model.volatilemodel;

import java.util.ArrayList;

/**
 * The UserModel class.
 * Used to interact with transactions and categories of a certain user.
 * @author Daan Kooij
 */
public class UserModel {

    private ArrayList<Transaction> transactions;
    private ArrayList<Category> categories;

    /**
     * Constructor of UserModel.
     * Initializes transactions and categories.
     */
    public UserModel() {
        transactions = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * Method used to retrieve the transactions of this UserModel.
     * @return An ArrayList of Transaction of this UserModel.
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Method used to create a new transaction in this UserModel.
     * @param name The name of the to be created Transaction.
     * @param amount The amount (in cents) of the to be created Transaction.
     * @return The Transaction created by this method.
     */
    public Transaction postTransaction(String name, String amount) {
        Transaction transaction = new Transaction(name, Long.parseLong(amount));
        transactions.add(transaction);
        return transaction;
    }

    /**
     * Method used to retrieve a certain transaction of this UserModel.
     * @param transactionIDString The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID contained in this UserModel.
     */
    public Transaction getTransaction(String transactionIDString) {
        int transactionID = Integer.parseInt(transactionIDString);
        if (transactionID < transactions.size()) {
            return transactions.get(transactionID);
        } else {
            return null;
        }
    }

    /**
     * Method used to update a certain transaction of this UserModel.
     * Will not update unspecified fields.
     * @param transactionIDString The transactionID of the Transaction that will be updated.
     * @param name The new name of the to be updated Transaction.
     * @param amount The new amount (in cents) of the to be updated Transaction.
     * @return The Transaction updated by this method.
     */
    public Transaction putTransaction(String transactionIDString, String name, String amount) {
        int transactionID = Integer.parseInt(transactionIDString);
        if (transactionID < transactions.size()) {
            Transaction transaction = transactions.get(transactionID);
            if (name != null && !name.equals("")) {
                transaction.setName(name);
            }
            if (amount != null && !amount.equals("")) {
                transaction.setAmount(Long.parseLong(amount));
            }
            return transaction;
        } else {
            return null;
        }
    }

    /**
     * Method used to remove a certain transaction of this UserModel.
     * @param transactionIDString The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String transactionIDString) {
        int transactionID = Integer.parseInt(transactionIDString);
        if (transactionID < transactions.size()) {
            transactions.remove(transactionID);
        }
    }

    /**
     * Method used to retrieve the categories of this UserModel.
     * @return An ArrayList of Category of this UserModel.
     */
    public ArrayList<Category> getCategories() {
        return categories;
    }

}
