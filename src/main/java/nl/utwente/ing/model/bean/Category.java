package nl.utwente.ing.model.bean;

/**
 * The Category class.
 * Used to store information about a Category.
 *
 * @author Daan Kooij
 */
public class Category {

    private long id;
    private String name;

    /**
     * An empty constructor of Category.
     * Used by the Spring framework.
     */
    public Category() {

    }

    /**
     * A constructor of Category.
     * Used when created a Category with categoryID.
     *
     * @param id   The ID of the to be created Category.
     * @param name The name of the to be created Category.
     */
    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Method used to retrieve the id of Category.
     *
     * @return The id of Category.
     */
    public long getID() {
        return id;
    }

    /**
     * Method used to update the id of Category.
     *
     * @param id The new id of Category.
     */
    public void setID(long id) {
        this.id = id;
    }

    /**
     * Method used to retrieve the name of Category.
     *
     * @return The name of Category.
     */
    public String getName() {
        return name;
    }

    /**
     * Method used to update the name of Category.
     *
     * @param name The new name of Category.
     */
    public void setName(String name) {
        this.name = name;
    }

}
