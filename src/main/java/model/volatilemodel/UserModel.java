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
     * @param limitString The maximum amount of transactions to be fetched.
     * @param offsetString The starting index to fetch transactions.
     * @return An ArrayList of Transaction of this UserModel.
     */
    public ArrayList<Transaction> getTransactions(String limitString, String offsetString) {
        int offset = Integer.parseInt(offsetString);
        int calculatedLimit = offset + Integer.parseInt(limitString);

        ArrayList<Transaction> limitedTransactions = new ArrayList<>();
        for (int position = offset; position < transactions.size() && position < calculatedLimit; position++) {
            limitedTransactions.add(transactions.get(position));
        }
        return limitedTransactions;
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
     * Method used to remove a certain Transaction of this UserModel.
     * @param transactionIDString The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String transactionIDString) {
        int transactionID = Integer.parseInt(transactionIDString);
        if (transactionID < transactions.size()) {
            transactions.remove(transactionID);
        }
    }

    /**
     * Method used to assign a Category to a Transaction of this UserModel.
     * @param transactionIDString The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryIDString The categoryID of the Category that will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    public Transaction assignCategoryToTransaction(String transactionIDString, String categoryIDString) {
        int transactionID = Integer.parseInt(transactionIDString);
        if (transactionID < transactions.size()) {
            Transaction transaction = this.transactions.get(transactionID);
            int categoryID = Integer.parseInt(categoryIDString);
            if (categoryID < categories.size()) {
                transaction.setCategory(this.categories.get(categoryID));
            } else {
                transaction.setCategory(null);
            }
            return transaction;
        } else {
            return null;
        }
    }

    /**
     * Method used to retrieve the categories of this UserModel.
     * @param limitString The maximum amount of categories to be fetched.
     * @param offsetString The starting index to fetch categories.
     * @return An ArrayList of Category of this UserModel.
     */
    public ArrayList<Category> getCategories(String limitString, String offsetString) {
        int offset = Integer.parseInt(offsetString);
        int calculatedLimit = offset + Integer.parseInt(limitString);

        ArrayList<Category> limitedCategories = new ArrayList<>();
        for (int position = offset; position < categories.size() && position < calculatedLimit; position++) {
            limitedCategories.add(categories.get(position));
        }
        return limitedCategories;
    }

    /**
     * Method used to create a new Category in this UserModel.
     * @param categoryName The name of the Category that will be created.
     * @return The Category created by this method.
     */
    public Category postCategory(String categoryName) {
        Category category = new Category(categoryName);
        categories.add(category);
        return category;
    }

    /**
     * Method used to retrieve a certain Category from this UserModel.
     * @param categoryIDString The categoryID of the Category that will be retrieved.
     * @return The Category with categoryID contained in this UserModel.
     */
    public Category getCategory(String categoryIDString) {
        int categoryID = Integer.parseInt(categoryIDString);
        if (categoryID < categories.size()) {
            return categories.get(categoryID);
        } else {
            return null;
        }
    }

    /**
     * Method used to update a certain Category in this UserModel.
     * @param categoryIDString The categoryID of the Category that will be updated.
     * @param categoryName The new name of the Category that will be updated.
     * @return The Category updated by this method.
     */
    public Category putCategory(String categoryIDString, String categoryName) {
        int categoryID = Integer.parseInt(categoryIDString);
        if (categoryID < categories.size()) {
            Category category = categories.get(categoryID);
            if (categoryName != null && !categoryName.equals("")) {
                category.setName(categoryName);
            }
            return category;
        } else {
            return null;
        }
    }

    /**
     * Method used to remove a certain Category of this UserModel.
     * @param categoryIDString The categoryID of the Category that will be deleted.
     */
    public void deleteCategory(String categoryIDString) {
        int categoryID = Integer.parseInt(categoryIDString);
        if (categoryID < categories.size()) {
            Category category = categories.get(categoryID);
            for (Transaction transaction : transactions) {
                if (transaction.getCategory() == category) {
                    transaction.setCategory(null);
                }
            }
            categories.remove(categoryID);
        }
    }

}
