package model.bean;

/**
 * The Category class.
 * Used to store information about a Category.
 *
 * @author Daan Kooij
 */
public class Category {

    private int categoryID;
    private String name;

    /**
     * A constructor of Category.
     * Used when created a Category without categoryID.
     *
     * @param name The name of the to be created Category.
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * A constructor of Category.
     * Used when created a Category with categoryID.
     *
     * @param categoryID The ID of the to be created Category.
     * @param name       The name of the to be created Category.
     */
    public Category(int categoryID, String name) {
        this.categoryID = categoryID;
        this.name = name;
    }

    /**
     * Method used to retrieve the ID of Category.
     *
     * @return The ID of Category.
     */
    public int getCategoryID() {
        return categoryID;
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
