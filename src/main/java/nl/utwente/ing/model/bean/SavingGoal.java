package nl.utwente.ing.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.utwente.ing.misc.date.IntervalHelper;

/**
 * The SavingGoal class.
 * Used to store information about a SavingGoal.
 *
 * @author Daan Kooij
 */
public class SavingGoal {

    private long id;
    private String creationDate;
    private String name;
    private float goal;
    private float savePerMonth;
    private float minBalanceRequired;
    private float balance;

    /**
     * An empty constructor of SavingGoal.
     * Used by the Spring framework.
     */
    public SavingGoal() {

    }

    public SavingGoal(long id, String creationDate, String name, float goal,
                      float savePerMonth, float minBalanceRequired) {
        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
        this.goal = goal;
        this.savePerMonth = savePerMonth;
        this.minBalanceRequired = minBalanceRequired;
    }

    /**
     * Method used to retrieve the ID of SavingGoal.
     *
     * @return The ID of SavingGoal.
     */
    public long getId() {
        return id;
    }

    /**
     * Method used to update the ID of SavingGoal.
     *
     * @param id The new ID of SavingGoal.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Method used to retrieve the CreationDate of SavingGoal.
     * Ignored by the REST Controller.
     *
     * @return The CreationDate of SavingGoal.
     */
    @JsonIgnore
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Method used to update the CreationDate of SavingGoal.
     *
     * @param creationDate The new CreationDate of SavingGoal.
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Method used to retrieve the Name of SavingGoal.
     *
     * @return The Name of SavingGoal.
     */
    public String getName() {
        return name;
    }

    /**
     * Method used to update the Name of SavingGoal.
     *
     * @param name The new Name of SavingGoal.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method used to retrieve the Goal of SavingGoal.
     *
     * @return The Goal of SavingGoal.
     */
    public float getGoal() {
        return goal;
    }

    /**
     * Method used to update the Goal of SavingGoal.
     *
     * @param goal The new Goal of SavingGoal.
     */
    public void setGoal(float goal) {
        this.goal = goal;
    }

    /**
     * Method used to retrieve the amount that should be saved per month of SavingGoal.
     *
     * @return The amount that should be saved per month of SavingGoal.
     */
    public float getSavePerMonth() {
        return savePerMonth;
    }

    /**
     * Method used to update the amount that should be saved per month of SavingGoal.
     *
     * @param savePerMonth The new amount that should be saved per month of SavingGoal.
     */
    public void setSavePerMonth(float savePerMonth) {
        this.savePerMonth = savePerMonth;
    }

    /**
     * Method used to retrieve the minimum balance to put aside money of SavingGoal.
     *
     * @return The minimum balance to put aside money of SavingGoal.
     */
    public float getMinBalanceRequired() {
        return minBalanceRequired;
    }

    /**
     * Method used to update the minimum balance to put aside money of SavingGoal.
     *
     * @param minBalanceRequired The new minimum balance to put aside money of SavingGoal.
     */
    public void setMinBalanceRequired(float minBalanceRequired) {
        this.minBalanceRequired = minBalanceRequired;
    }

    /**
     * Method used to retrieve the Balance of SavingGoal.
     *
     * @return The Balance of SavingGoal.
     */
    public float getBalance() {
        return balance;
    }

    /**
     * Method used to put aside one month worth of savings as long as the goal is not met.
     *
     * @return The amount put aside by this method call.
     */
    public float setApart() {
        float leftToSave = goal - balance;
        float saved = Math.min(leftToSave, savePerMonth);
        balance += saved;
        return saved;
    }

    /**
     * Method used to retrieve the month identifier of the creation date of SavingGoal.
     * Ignored by the REST Controller.
     *
     * @return The month identifier of the creation date of SavingGoal.
     */
    @JsonIgnore
    public int getMonthIdentifier() {
        return IntervalHelper.getMonthIdentifier(creationDate);
    }

}
