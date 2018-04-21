package nl.utwente.ing.model.bean;

/**
 * The Transaction class.
 * Used to store information about a Transaction.
 *
 * @author Daan Kooij
 */
public class Transaction {

    private long id;
    private String date;
    private float amount;
    private String description;
    private String externalIBAN;
    private String type;
    private Category category;

    /**
     * An empty constructor of Transaction.
     * Used by the Spring framework.
     */
    public Transaction() {

    }

    /**
     * A constructor of Transaction.
     *
     * @param id           The id of the to be created Transaction.
     * @param date         The date of the to be created Transaction.
     * @param amount       The amount of the to be created Transaction.
     * @param description  The description of the to be created Transaction.
     * @param externalIBAN The externalIBAN of the to be created Transaction.
     * @param type         The type of the to be created Transaction.
     */
    public Transaction(long id, String date, float amount, String description, String externalIBAN, String type) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.externalIBAN = externalIBAN;
        this.type = type;
    }

    /**
     * Method used to retrieve the ID of Transaction.
     *
     * @return The ID of Transaction.
     */
    public long getID() {
        return id;
    }

    /**
     * Method used to update the id of Transaction.
     *
     * @param id The new id of Transaction.
     */
    public void setID(long id) {
        this.id = id;
    }

    /**
     * Method used to retrieve the date of Transaction.
     *
     * @return The date of Transaction.
     */
    public String getDate() {
        return date;
    }

    /**
     * Method used to update the date of Transaction.
     *
     * @param date The new date of Transaction.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Method used to retrieve the amount of Transaction.
     *
     * @return The amount of Transaction.
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Method used to update the amount of Transaction.
     *
     * @param amount The new amount of Transaction.
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Method used to retrieve the description of Transaction.
     *
     * @return The description of Transaction.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method used to update the description of Transaction.
     *
     * @param description The new description of Transaction.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method used to retrieve the externalIBAN of Transaction.
     *
     * @return The externalIBAN of Transaction.
     */
    public String getExternalIBAN() {
        return externalIBAN;
    }

    /**
     * Method used to update the externalIBAN of Transaction.
     *
     * @param externalIBAN The new externalIBAN of Transaction.
     */
    public void setExternalIBAN(String externalIBAN) {
        this.externalIBAN = externalIBAN;
    }

    /**
     * Method used to retrieve the type of Transaction.
     *
     * @return The type of Transaction.
     */
    public String getType() {
        return type;
    }

    /**
     * Method used to update the type of Transaction.
     *
     * @param type The new type of Transaction.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method used to retrieve the Category assigned to Transaction.
     *
     * @return The Category assigned to Transaction.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Method used to update the Category assigned to Transaction.
     *
     * @param category The new Category that will be assigned to Transaction.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
}
