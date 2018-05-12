package nl.utwente.ing.model.bean;

/**
 * The CategoryRule class.
 * Used to store information about a CategoryRule.
 *
 * @author Daan Kooij
 */
public class CategoryRule {

    private long id;
    private String description;
    private String iBAN;
    private String type;
    private long category_id;
    private boolean applyOnHistory;

    /**
     * An empty constructor of CategoryRule.
     * Used by the Spring framework.
     */
    public CategoryRule() {
        this.category_id = 0;
        this.applyOnHistory = false;
    }

    /**
     * A constructor of CategoryRule.
     *
     * @param id             The id of the to be created CategoryRule.
     * @param description    The description of the to be created CategoryRule.
     * @param iBAN           The iBAN of the to be created CategoryRule.
     * @param type           The type of the to be created CategoryRule.
     * @param category_id    The category_id of the to be created CategoryRule.
     * @param applyOnHistory The boolean indicating whether this CategoryRule is applied on previous Transactions.
     */
    public CategoryRule(long id, String description, String iBAN, String type,
                        long category_id, boolean applyOnHistory) {
        this.id = id;
        this.description = description;
        this.iBAN = iBAN;
        this.type = type;
        this.category_id = category_id;
        this.applyOnHistory = applyOnHistory;
    }

    /**
     * Method used to retrieve the ID of CategoryRule.
     *
     * @return The ID of CategoryRule.
     */
    public long getId() {
        return id;
    }

    /**
     * Method used to update the ID of CategoryRule.
     *
     * @param id The new ID of CategoryRule.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Method used to retrieve the description of CategoryRule.
     *
     * @return The description of CategoryRule.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method used to update the description of CategoryRule.
     *
     * @param description The new description of CategoryRule.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method used to retrieve the iBAN of CategoryRule.
     *
     * @return The iBAN of CategoryRule.
     */
    public String getiBAN() {
        return iBAN;
    }

    /**
     * Method used to update the iBAN of CategoryRule.
     *
     * @param iBAN The new iBAN of CategoryRule.
     */
    public void setiBAN(String iBAN) {
        this.iBAN = iBAN;
    }

    /**
     * Method used to retrieve the type of CategoryRule.
     *
     * @return The type of CategoryRule.
     */
    public String getType() {
        return type;
    }

    /**
     * Method used to update the type of CategoryRule.
     *
     * @param type The new type of CategoryRule.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method used to retrieve the category_id of CategoryRule.
     *
     * @return The category_id of CategoryRule.
     */
    public long getCategory_id() {
        return category_id;
    }

    /**
     * Method used to update the category_id of CategoryRule.
     *
     * @param category_id The new category_id of CategoryRule.
     */
    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    /**
     * Method used to retrieve the boolean indicating whether this CategoryRule is applied on previous Transactions.
     *
     * @return The boolean indicating whether this CategoryRule is applied on previous Transactions.
     */
    public boolean getApplyOnHistory() {
        return applyOnHistory;
    }

    /**
     * Method used to update the boolean indicating whether this CategoryRule is applied on previous Transactions.
     *
     * @param applyOnHistory The new boolean indicating whether this CategoryRule is applied on previous Transactions.
     */
    public void setApplyOnHistory(boolean applyOnHistory) {
        this.applyOnHistory = applyOnHistory;
    }

}
