package model.volatilemodel;

/**
 * The Transaction class.
 * Used to store information about a Transaction.
 * @author Daan Kooij
 */
public class Transaction {

    private String name;
    private long amount;
    private Category category;

    /**
     * The constructor of Transaction.
     * @param name The name of the to be created Transaction.
     * @param amount The amount (in cents) of the to be created Transaction.
     */
    public Transaction(String name, long amount) {
        this.name = name;
        this.amount = amount;
    }

    /**
     * Method used to retrieve the name of Transaction.
     * @return The name of Transaction.
     */
    public String getName() {
        return name;
    }

    /**
     * Method used to update the name of Transaction.
     * @param name The new name of Transaction.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method used to retrieve the amount (in cents) of Transaction.
     * @return The amount (in cents) of Transaction.
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Method used to update the amount of Transaction.
     * @param amount The new amount (in cents) of Transaction.
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * Method used to retrieve the Category assigned to Transaction.
     * @return The Category assigned to Transaction.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Method used to update the Category assigned to Transaction.
     * @param category The new Category that will be assigned to Transaction.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
}
