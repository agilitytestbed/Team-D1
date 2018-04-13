package nl.utwente.ing.model.bean;

/**
 * The Session class.
 * Used to store information about a Session.
 *
 * @author Daan Kooij
 */
public class Session {

    private String id;

    /**
     * A constructor of Session
     *
     * @param id The id of the to be created Session.
     */
    public Session(String id) {
        this.id = id;
    }

    /**
     * Method used to retrieve the id of Session.
     *
     * @return The id of Session.
     */
    public String getID() {
        return id;
    }

}
