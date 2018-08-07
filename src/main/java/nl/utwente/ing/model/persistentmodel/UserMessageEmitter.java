package nl.utwente.ing.model.persistentmodel;

import nl.utwente.ing.misc.date.IntervalHelper;
import nl.utwente.ing.model.bean.Category;
import nl.utwente.ing.model.bean.Transaction;
import nl.utwente.ing.model.bean.UserMessage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * The UserMessageEmitter class.
 * Used to create and emit UserMessages.
 *
 * @author Daan Kooij
 */
public class UserMessageEmitter {

    private Connection connection;
    private CustomORM customORM;

    private static final String EVENT_BALANCE_DROP_BELOW_ZERO = "Balance drop below zero.";
    private static final String EVENT_BALANCE_REACH_NEW_HIGH = "Balance reach new high.";
    private static final String EVENT_PAYMENT_REQUEST_FILLED = "Payment request filled: ";
    private static final String EVENT_PAYMENT_REQUEST_NOT_FILLED = "Payment request not filled: ";
    private static final String EVENT_SAVING_GOAL_REACHED = "Saving goal reached: ";

    private static final String RULE_CATEGORY_LIMIT_REACHED = "Category limit reached: ";

    /**
     * The constructor of UserMessageEmitter.
     *
     * @param connection The database connection.
     * @param customORM  The CustomORM.
     */
    public UserMessageEmitter(Connection connection, CustomORM customORM) {
        this.connection = connection;
        this.customORM = customORM;
    }

    /**
     * Method used to emit a UserMessage for a certain user saying that his/her balance dropped below zero.
     *
     * @param userID The ID of the user for which the UserMessage will be emitted.
     */
    public void eventBalanceBelowZero(int userID) {
        this.emitUserMessage(userID, "warning", EVENT_BALANCE_DROP_BELOW_ZERO);
    }

    /**
     * Method used to emit a UserMessage for a certain user saying that his/her balance reached a new high.
     * If such a message already exists for the user and is still unread, nothing will be emitted.
     * If the Transactions of the user do not span at least three months, nothing will be emitted.
     *
     * @param userID The ID of the user for which the UserMessage may be emitted.
     */
    public void eventBalanceNewHigh(int userID) {
        ArrayList<UserMessage> userMessages = customORM.getUnreadUserMessages(userID);
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getMessage().equals(EVENT_BALANCE_REACH_NEW_HIGH)) {
                return;
            }
        }
        ArrayList<Transaction> transactions = customORM.getTransactionsAscending(userID);
        if (transactions.size() == 0) {
            return;
        }

        LocalDateTime firstDatePlusThreeMonths =
                IntervalHelper.toLocalDateTime(transactions.get(0).getDate()).plusMonths(3);
        LocalDateTime currentDate = IntervalHelper.toLocalDateTime(customORM.getCurrentDate(userID));
        if (firstDatePlusThreeMonths.compareTo(currentDate) <= 0) {
            this.emitUserMessage(userID, "info", EVENT_BALANCE_REACH_NEW_HIGH);
        }
    }

    /**
     * Method used to emit a UserMessage for a certain user saying that a PaymentRequest has been filled.
     * If such a message is already emitted for the same PaymentRequest, nothing will be emitted.
     *
     * @param userID             The ID of the user for which the UserMessage may be emitted.
     * @param paymentRequestID   The ID of the PaymentRequest for which a UserMessage may be emitted.
     * @param paymentRequestName The name of the PaymentRequest for which a UserMessage may be emitted.
     */
    public void eventPaymentRequestFilled(int userID, long paymentRequestID, String paymentRequestName) {
        String message = EVENT_PAYMENT_REQUEST_FILLED + paymentRequestName + " (ID = " + paymentRequestID + ").";
        ArrayList<UserMessage> userMessages = customORM.getAllUserMessages(userID);
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getMessage().equals(message)) {
                return;
            }
        }
        this.emitUserMessage(userID, "info", message);
    }

    /**
     * Method used to emit a UserMessage for a certain user saying that a PaymentRequest has not been filled in time.
     * If such a message is already emitted for the same PaymentRequest, nothing will be emitted.
     *
     * @param userID             The ID of the user for which the UserMessage may be emitted.
     * @param paymentRequestID   The ID of the PaymentRequest for which a UserMessage may be emitted.
     * @param paymentRequestName The name of the PaymentRequest for which a UserMessage may be emitted.
     */
    public void eventPaymentRequestNotFilled(int userID, long paymentRequestID, String paymentRequestName) {
        String message = EVENT_PAYMENT_REQUEST_NOT_FILLED + paymentRequestName + " (ID = " + paymentRequestID + ").";
        ArrayList<UserMessage> userMessages = customORM.getAllUserMessages(userID);
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getMessage().equals(message)) {
                return;
            }
        }
        this.emitUserMessage(userID, "warning", message);
    }

    /**
     * Method used to emit a UserMessage for a certain user saying that a SavingGoal has been reached.
     * If such a message is already emitted for the same SavingGoal, nothing will be emitted.
     *
     * @param userID         The ID of the user for which the UserMessage may be emitted.
     * @param savingGoalID   The ID of the SavingGoal for which a UserMessage may be emitted.
     * @param savingGoalName The name of the SavingGoal for which a UserMessage may be emitted.
     */
    public void eventSavingGoalReached(int userID, long savingGoalID, String savingGoalName) {
        String message = EVENT_SAVING_GOAL_REACHED + savingGoalName + " (ID = " + savingGoalID + ").";
        ArrayList<UserMessage> userMessages = customORM.getAllUserMessages(userID);
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getMessage().equals(message)) {
                return;
            }
        }
        this.emitUserMessage(userID, "info", message);
    }

    /**
     * Method used to emit a UserMessage for a certain user saying that a Category limit as specified by the user
     * has been reached.
     *
     * @param userID            The ID of the user for which the UserMessage will be emitted.
     * @param type              The type of the to be emitted UserMessage.
     * @param violatingCategory The Category object for whcih the Category limit as specified by the user
     *                          has been reached.
     */
    public void ruleCategoryLimitReached(int userID, String type, Category violatingCategory) {
        String message = RULE_CATEGORY_LIMIT_REACHED + violatingCategory.getName() +
                " (ID = " + violatingCategory.getID() + ").";
        this.emitUserMessage(userID, type, message);
    }

    /**
     * Method used to emit a UserMessage for a certain user.
     *
     * @param userID  The ID of the user for which the UserMessage will be emitted.
     * @param type    The type of the to be emitted UserMessage.
     * @param message The message of the to be emitted UserMessage.
     */
    private void emitUserMessage(int userID, String type, String message) {
        try {
            connection.setAutoCommit(false);
            customORM.increaseHighestUserMessageID(userID);
            long userMessageID = customORM.getHighestUserMessageID(userID);
            connection.commit();
            connection.setAutoCommit(true);
            String date = customORM.getCurrentDate(userID);
            customORM.createUserMessage(userID, new UserMessage(userMessageID, message, date, false, type));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
