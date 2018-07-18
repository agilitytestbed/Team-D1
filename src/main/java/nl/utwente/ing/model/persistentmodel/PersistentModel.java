package nl.utwente.ing.model.persistentmodel;

import nl.utwente.ing.exception.InvalidSessionIDException;
import nl.utwente.ing.exception.ResourceNotFoundException;
import nl.utwente.ing.misc.date.IntervalHelper;
import nl.utwente.ing.misc.date.IntervalPeriod;
import nl.utwente.ing.model.Model;
import nl.utwente.ing.model.bean.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;

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
     * Method used to create and retrieve a new Session.
     *
     * @return A new Session.
     */
    public Session getSession() {
        /*
        In an exceptionally rare case, it can happen that there will be two users with the same sessionID.
        This only happens when two sessionIDs are generated at exactly the same time and they are the same.
         */
        String sessionID = "";
        boolean unique = false;
        while (!unique) {
            sessionID = UUID.randomUUID().toString();
            if (customORM.getUserID(sessionID) == -1) {
                unique = true;
            }
        }
        customORM.createNewUser(sessionID);
        return new Session(sessionID);
    }

    /**
     * Method used to retrieve the transactions belonging to a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryName The category to be filtered on (empty String if no filter).
     * @param limit        The maximum amount of transactions to be fetched.
     * @param offset       The starting index to fetch transactions.
     * @return An ArrayList of Transaction belonging to the user with sessionID.
     */
    public ArrayList<Transaction> getTransactions(String sessionID, String categoryName, int limit, int offset)
            throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        ArrayList<Transaction> transactions;
        if (categoryName.equals("")) {
            transactions = customORM.getTransactions(userID, limit, offset);
        } else {
            transactions = customORM.getTransactionsByCategory(userID, categoryName, limit, offset);
        }
        for (Transaction transaction : transactions) {
            this.populateCategory(userID, transaction);
        }
        return transactions;
    }

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
    public Transaction postTransaction(String sessionID, String date, float amount, String description,
                                       String externalIBAN, String type, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Transaction transaction = null;
        try {
            connection.setAutoCommit(false);
            customORM.increaseHighestTransactionID(userID);
            long transactionID = customORM.getHighestTransactionID(userID);
            connection.commit();
            connection.setAutoCommit(true);
            customORM.createTransaction(userID, transactionID, date, amount, description, externalIBAN, type);
            transaction = customORM.getTransaction(userID, transactionID);
            if (categoryID != 0) {
                this.assignCategoryToTransaction(sessionID, transactionID, categoryID);
            } else {
                // Check if there is a CategoryRule that applies to this Transaction
                ArrayList<CategoryRule> categoryRules = customORM.getCategoryRules(userID);

                boolean found = false;
                for (int i = 0; i < categoryRules.size() && !found; i++) {
                    CategoryRule categoryRule = categoryRules.get(i);
                    if (transaction.getDescription().contains(categoryRule.getDescription()) &&
                            transaction.getExternalIBAN().contains(categoryRule.getiBAN()) &&
                            transaction.getType().contains(categoryRule.getType()) &&
                            customORM.getCategory(userID, categoryRule.getCategory_id()) != null) {
                        customORM.linkTransactionToCategory(userID, transactionID, categoryRule.getCategory_id());
                        found = true;
                    }
                }
            }
            this.populateCategory(userID, transaction);
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
    public Transaction getTransaction(String sessionID, long transactionID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Transaction transaction = customORM.getTransaction(userID, transactionID);
        this.populateCategory(userID, transaction);
        if (transaction != null) {
            return transaction;
        } else {
            throw new ResourceNotFoundException();
        }
    }

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
    public Transaction putTransaction(String sessionID, long transactionID, String date, float amount,
                                      String description, String externalIBAN, String type, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Transaction transaction = customORM.getTransaction(userID, transactionID);
        if (transaction != null) {
            if (date != null && !date.equals("")) {
                customORM.updateTransactionDate(date, userID, transactionID);
            }
            if (amount != 0) {
                customORM.updateTransactionAmount(amount, userID, transactionID);
            }
            if (description != null) {
                customORM.updateTransactionDescription(description, userID, transactionID);
            }
            if (externalIBAN != null && !externalIBAN.equals("")) {
                customORM.updateTransactionExternalIBAN(externalIBAN, userID, transactionID);
            }
            if (type != null && !type.equals("")) {
                customORM.updateTransactionType(type, userID, transactionID);
            }
            if (categoryID != 0) {
                this.assignCategoryToTransaction(sessionID, transactionID, categoryID);
            }
            transaction = customORM.getTransaction(userID, transactionID);
            this.populateCategory(userID, transaction);
            return transaction;
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to delete a certain Transaction of a certain user.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    public void deleteTransaction(String sessionID, long transactionID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Transaction transaction = customORM.getTransaction(userID, transactionID);
        if (transaction != null) {
            customORM.unlinkTransactionFromAllCategories(userID, transactionID);
            customORM.deleteTransaction(userID, transactionID);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to assign a Category to an Transaction.
     * Currently, first all other assigned Category objects to Transaction are unassigned.
     *
     * @param sessionID     The sessionID of the user.
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryID    The categoryID of the Category which will be assigned to the Transaction.
     * @return The Transaction to which the Category is assigned.
     */
    public Transaction assignCategoryToTransaction(String sessionID, long transactionID, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Transaction transaction = customORM.getTransaction(userID, transactionID);
        if (transaction != null) {
            Category category = customORM.getCategory(userID, categoryID);
            if (category != null) {
                customORM.unlinkTransactionFromAllCategories(userID, transactionID);
                customORM.linkTransactionToCategory(userID, transactionID, categoryID);
                transaction.setCategory(category);
                return transaction;
            } else {
                throw new ResourceNotFoundException();
            }
        } else {
            throw new ResourceNotFoundException();
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
    public ArrayList<Category> getCategories(String sessionID, int limit, int offset)
            throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        return customORM.getCategories(userID, limit, offset);
    }

    /**
     * Method used to create a new Category for a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @param name      The name of the to be created category.
     * @return The Category created by this method.
     */
    public Category postCategory(String sessionID, String name) throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        Category category = null;
        try {
            connection.setAutoCommit(false);
            customORM.increaseHighestCategoryID(userID);
            long categoryID = customORM.getHighestCategoryID(userID);
            connection.commit();
            connection.setAutoCommit(true);
            customORM.createCategory(userID, categoryID, name);
            category = customORM.getCategory(userID, categoryID);
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
    public Category getCategory(String sessionID, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Category category = customORM.getCategory(userID, categoryID);
        if (category != null) {
            return category;
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to update a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be updated.
     * @param name       The new name of the to be updated Category.
     * @return The Category updated by this method.
     */
    public Category putCategory(String sessionID, long categoryID, String name)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Category category = customORM.getCategory(userID, categoryID);
        if (category != null) {
            if (name != null && !name.equals("")) {
                customORM.updateCategoryName(name, userID, categoryID);
            }
            category = customORM.getCategory(userID, categoryID);
        } else {
            throw new ResourceNotFoundException();
        }
        return category;
    }

    /**
     * Method used to remove a certain Category of a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param categoryID The categoryID of the Category that will be deleted.
     */
    public void deleteCategory(String sessionID, long categoryID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        Category category = customORM.getCategory(userID, categoryID);
        if (category != null) {
            customORM.unlinkCategoryFromAllTransactions(userID, categoryID);
            customORM.deleteCategory(userID, categoryID);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to retrieve the CategoryRules belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of CategoryRules belonging to the user with sessionID.
     */
    public ArrayList<CategoryRule> getCategoryRules(String sessionID) throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        return customORM.getCategoryRules(userID);
    }

    /**
     * Method used to create a new CategoryRule for a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryRule The CategoryRule object to be used to create the new CategoryRule.
     * @return The CategoryRule created by this method.
     */
    public CategoryRule postCategoryRule(String sessionID, CategoryRule categoryRule) throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        CategoryRule createdCategoryRule = null;
        try {
            connection.setAutoCommit(false);
            customORM.increaseHighestCategoryRuleID(userID);
            long categoryRuleID = customORM.getHighestCategoryRuleID(userID);
            connection.commit();
            connection.setAutoCommit(true);
            categoryRule.setId(categoryRuleID);
            customORM.createCategoryRule(userID, categoryRule);
            createdCategoryRule = customORM.getCategoryRule(userID, categoryRule.getId());

            // If applyOnHistory is true and the Category exists, assign Category to all matching Transactions.
            if (createdCategoryRule != null) {
                long categoryID = createdCategoryRule.getCategory_id();

                if (createdCategoryRule.getApplyOnHistory() && customORM.getCategory(userID, categoryID) != null) {
                    ArrayList<Long> matchingTransactionIDs =
                            customORM.getMatchingTransactionIDs(userID, createdCategoryRule);
                    for (Long transactionID : matchingTransactionIDs) {
                        customORM.unlinkTransactionFromAllCategories(userID, transactionID);
                        customORM.linkTransactionToCategory(userID, transactionID, categoryID);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return createdCategoryRule;
    }

    /**
     * Method used to retrieve a certain CategoryRule of a certain user.
     *
     * @param sessionID      The sessionID of the user.
     * @param categoryRuleID The categoryRuleID of the CategoryRule that will be retrieved.
     * @return The CategoryRule with categoryRuleID belonging to the user with sessionID.
     */
    public CategoryRule getCategoryRule(String sessionID, long categoryRuleID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        CategoryRule categoryRule = customORM.getCategoryRule(userID, categoryRuleID);
        if (categoryRule != null) {
            return categoryRule;
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to update a certain CategoryRule of a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param categoryRule The CategoryRule object that will be used to update the CategoryRule with ID of this object.
     * @return The CategoryRule updated by this method.
     */
    public CategoryRule putCategoryRule(String sessionID, CategoryRule categoryRule)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        CategoryRule updatedCategoryRule = customORM.getCategoryRule(userID, categoryRule.getId()); // Not updated here
        if (updatedCategoryRule != null) {
            customORM.updateCategoryRule(userID, categoryRule);
            updatedCategoryRule = customORM.getCategoryRule(userID, categoryRule.getId());
        } else {
            throw new ResourceNotFoundException();
        }
        return updatedCategoryRule;
    }

    /**
     * Method used to remove a certain CategoryRule of a certain user.
     *
     * @param sessionID      The sessionID of the user.
     * @param categoryRuleID The categoryRuleID of the CategoryRule that will be deleted.
     */
    public void deleteCategoryRule(String sessionID, long categoryRuleID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        CategoryRule categoryRule = customORM.getCategoryRule(userID, categoryRuleID);
        if (categoryRule != null) {
            customORM.deleteCategoryRule(userID, categoryRuleID);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to retrieve balance history information of a certain user in the form of a list of
     * BalanceCandlesticks.
     *
     * @param sessionID The sessionID of the user.
     * @param intervalPeriod The IntervalPeriod specifying the span of intervals.
     * @param amount The amount of intervals for which BalanceCandlesticks should be generated.
     * @return The balance history information of a certain user in the form of a list of BalanceCandlesticks.
     */
    public ArrayList<BalanceCandlestick> getBalanceHistory(String sessionID, IntervalPeriod intervalPeriod, int amount)
            throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);

        LocalDateTime[] intervals = IntervalHelper.getIntervals(intervalPeriod, amount);

        ArrayList<Transaction> transactions = customORM.getTransactionsAscending(userID);
        ArrayList<SavingGoal> savingGoals = customORM.getSavingGoals(userID);
        ArrayList<BalanceCandlestick> candlesticks = new ArrayList<>();

        int previousMonthIdentifier = 0;
        if (transactions.size() > 0) {
            previousMonthIdentifier = transactions.get(0).getMonthIdentifier();
        }

        float balance = 0;
        int index = 0;
        for (int i = 1; i <= amount + 1; i++) {
            LocalDateTime startInterval = intervals[i - 1];
            LocalDateTime endInterval = intervals[i];
            long startUnixTime = startInterval.toEpochSecond(ZoneOffset.UTC); // Convert start of interval to UNIX time
            BalanceCandlestick candlestick = new BalanceCandlestick(balance, startUnixTime);

            while (index < transactions.size() &&
                    !IntervalHelper.isSmallerThan(endInterval, transactions.get(index).getDate())) {
                Transaction transaction = transactions.get(index);
                for (int j = previousMonthIdentifier; j < transaction.getMonthIdentifier(); j++) {
                    // For every saving goal, check if money should be set apart
                    for (SavingGoal savingGoal : savingGoals) {
                        if (transaction.getMonthIdentifier() > savingGoal.getMonthIdentifier() &&
                                balance >= savingGoal.getMinBalanceRequired()) {
                            // Set apart money and update balance accordingly
                            float mutation = -savingGoal.setApart();
                            balance += mutation;
                            candlestick.mutation(mutation);
                        }
                    }
                }
                previousMonthIdentifier = transaction.getMonthIdentifier();

                if (transaction.getType().equals("deposit")) {
                    candlestick.mutation(transaction.getAmount());
                } else {
                    candlestick.mutation(transaction.getAmount() * (-1));
                }
                balance = candlestick.getClose();
                index++;



            }
            candlesticks.add(candlestick);
        }

        candlesticks.remove(0);

        return candlesticks;
    }

    /**
     * Method used to retrieve the SavingGoals belonging to a certain user.
     *
     * @param sessionID The sessionID of the user.
     * @return An ArrayList of SavingGoals belonging to the user with sessionID.
     */
    public ArrayList<SavingGoal> getSavingGoals(String sessionID) throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        ArrayList<SavingGoal> savingGoals = customORM.getSavingGoals(userID);
        ArrayList<Transaction> transactions = customORM.getTransactionsAscending(userID);

        if (transactions.size() > 0) {
            float balance = 0;
            int previousMonthIdentifier = transactions.get(0).getMonthIdentifier();
            for (Transaction transaction : transactions) {
                // For every month elapsed since last transaction, check if money should be set apart
                for (int i = previousMonthIdentifier; i < transaction.getMonthIdentifier(); i++) {
                    // For every saving goal, check if money should be set apart
                    for (SavingGoal savingGoal : savingGoals) {
                        if (transaction.getMonthIdentifier() > savingGoal.getMonthIdentifier() &&
                                balance >= savingGoal.getMinBalanceRequired()) {
                            // Set apart money and update balance accordingly
                            float mutation = -savingGoal.setApart();
                            balance += mutation;
                        }
                    }
                }
                previousMonthIdentifier = transaction.getMonthIdentifier();

                // Update balance according to transaction
                float amount = transaction.getAmount();
                if (transaction.getType().equals("deposit")) {
                    balance += amount;
                } else {
                    balance -= amount;
                }
            }
        }

        return savingGoals;
    }

    /**
     * Method used to create a new SavingGoal for a certain user.
     *
     * @param sessionID  The sessionID of the user.
     * @param savingGoal The SavingGoal object to be used to create the new SavingGoal.
     * @return The SavingGoal created by this method.
     */
    public SavingGoal postSavingGoal(String sessionID, SavingGoal savingGoal) throws InvalidSessionIDException {
        int userID = this.getUserID(sessionID);
        SavingGoal createdSavingGoal = null;
        try {
            connection.setAutoCommit(false);
            customORM.increaseHighestSavingGoalID(userID);
            long savingGoalID = customORM.getHighestSavingGoalID(userID);
            connection.commit();
            connection.setAutoCommit(true);
            savingGoal.setId(savingGoalID);

            // Set creation date to highest date Transaction if it exists, otherwise set to start of UNIX time
            Transaction newestTransaction = customORM.getNewestTransaction(userID);
            if (newestTransaction == null) {
                savingGoal.setCreationDate("1970-01-01T00:00:00.000Z");
            } else {
                savingGoal.setCreationDate(newestTransaction.getDate());
            }

            customORM.createSavingGoal(userID, savingGoal);
            createdSavingGoal = customORM.getSavingGoal(userID, savingGoal.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return createdSavingGoal;
    }

    /**
     * Method used to remove a certain SavingGoal of a certain user.
     *
     * @param sessionID    The sessionID of the user.
     * @param savingGoalID The savingGoalID of the SavingGoal that will be deleted.
     */
    public void deleteSavingGoal(String sessionID, long savingGoalID)
            throws InvalidSessionIDException, ResourceNotFoundException {
        int userID = this.getUserID(sessionID);
        SavingGoal savingGoal = customORM.getSavingGoal(userID, savingGoalID);
        if (savingGoal != null) {
            customORM.deleteSavingGoal(userID, savingGoalID);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Method used to populate a Transaction object with a Category object.
     *
     * @param transaction The Transaction object that will be populated by a Category object.
     */
    private void populateCategory(int userID, Transaction transaction) {
        if (transaction != null) {
            long categoryID = customORM.getCategoryIDByTransactionID(userID, transaction.getID());
            Category category = customORM.getCategory(userID, categoryID);
            transaction.setCategory(category);
        }
    }

    /**
     * Method used to retrieve the userID belonging to a certain sessionID.
     *
     * @param sessionID The sessionID from which the belonging userID will be retrieved.
     * @return The userID belonging to sessionID.
     */
    private int getUserID(String sessionID) throws InvalidSessionIDException {
        int userID = customORM.getUserID(sessionID);
        if (userID == -1) {
            throw new InvalidSessionIDException();
        }
        return userID;
    }

}
