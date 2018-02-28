package nl.utwente.ing.exception;

/**
 * The ResourceNotFoundException class.
 * Extends Exception and implements APIException.
 * ResourceNotFoundException is thrown whenever a resource is not found.
 *
 * @author Daan Kooij
 */
public class ResourceNotFoundException extends Exception implements APIException {

    /**
     * The empty constructor of ResourceNotFoundException.
     */
    public ResourceNotFoundException() {

    }

}
