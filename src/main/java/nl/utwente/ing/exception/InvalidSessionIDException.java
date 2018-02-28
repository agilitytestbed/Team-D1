package nl.utwente.ing.exception;

/**
 * The InvalidSessionIDException class.
 * Extends Exception and implements APIException.
 * InvalidSessionIDException is thrown whenever a sessionID is missing or invalid.
 *
 * @author Daan Kooij
 */
public class InvalidSessionIDException extends Exception implements APIException {

    /**
     * The empty constructor of InvalidSessionIDException.
     */
    public InvalidSessionIDException() {

    }

}
