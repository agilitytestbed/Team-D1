package nl.utwente.ing.model.bean;

/**
 * The MessageRule class.
 * Used to store information about a MessageRule.
 *
 * @author Daan Kooij
 */
public class MessageRule {

    private long id;
    private long category_id;
    private String type;
    private float value;

    /**
     * An empty constructor of MessageRule.
     * Used by the Spring framework.
     */
    public MessageRule() {

    }

    /**
     * A constructor of MessageRule.
     *
     * @param id          The ID of the to be created MessageRule.
     * @param category_id The category_id of the to be created MessageRule.
     * @param type        The type of the to be created MessageRule.
     * @param value       The value of the to be created MessageRule.
     */
    public MessageRule(long id, long category_id, String type, float value) {
        this.id = id;
        this.category_id = category_id;
        this.type = type;
        this.value = value;
    }

    /**
     * Method used to retrieve the ID of MessageRule.
     *
     * @return The ID of MessageRule.
     */
    public long getID() {
        return id;
    }

    /**
     * Method used to update the ID of MessageRule.
     *
     * @param id The new ID of MessageRule.
     */
    public void setID(long id) {
        this.id = id;
    }

    /**
     * Method used to retrieve the category_id of MessageRule.
     *
     * @return The category_id of MessageRule.
     */
    public long getCategory_id() {
        return category_id;
    }

    /**
     * Method used to update the category_id of MessageRule.
     *
     * @param category_id The new category_id of MessageRule.
     */
    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }


    /**
     * Method used to retrieve the type of MessageRule.
     *
     * @return The type of MessageRule.
     */
    public String getType() {
        return type;
    }

    /**
     * Method used to update the type of MessageRule.
     *
     * @param type The new type of MessageRule.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method used to retrieve the value of MessageRule.
     *
     * @return The value of MessageRule.
     */
    public float getValue() {
        return value;
    }

    /**
     * Method used to update the value of MessageRule.
     *
     * @param value The new value of MessageRule.
     */
    public void setValue(float value) {
        this.value = value;
    }

}
