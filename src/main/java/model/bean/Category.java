package model.bean;

/**
 * The Category class.
 * Used to store information about a Category.
 * @author Daan Kooij
 */
public class Category {

    private String name;

    /**
     * The constructor of Category.
     * @param name The name of the to be created Category.
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Method used to retrieve the name of Category.
     * @return The name of Category.
     */
    public String getName() {
        return name;
    }

    /**
     * Method used to update the name of Category.
     * @param name The new name of Category.
     */
    public void setName(String name) {
        this.name = name;
    }

}
