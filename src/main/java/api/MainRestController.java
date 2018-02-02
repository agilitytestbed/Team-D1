package api;

import model.Model;
import model.volatilemodel.Transaction;
import model.volatilemodel.VolatileModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;

/**
 * The MainRestController class.
 * Consists of methods that allow the application to make use of REST using the Spring framework.
 * Makes use of the Model interface (and an implementation of it) to store data.
 * @author Daan Kooij
 */
@RestController
public class MainRestController {

    private Model model;

    /**
     * The constructor of MainRestController.
     * Initializes the model.
     */
    public MainRestController() {
        model = new VolatileModel();
    }

    /**
     * Method used to retrieve the sessionID of the user issuing the current request.
     * @return The sessionID of the user issuing the current request.
     */
    public String getSessionID() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    /**
     * Method used to retrieve the transactions belonging to the user issuing the current request.
     * @return An ArrayList of Transaction belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/transaction")
    public ArrayList<Transaction> getTransactions() {
        return model.getTransactions(this.getSessionID());
    }

    /**
     * Method used to create a new Transaction for the user issuing the current request.
     * @param name The name of the to be created Transaction.
     * @param amount The amount (in cents) of the to be created Transaction.
     * @return The Transaction created using this method.
     */
    @RequestMapping(method = RequestMethod.POST,
            value = RestControllerConstants.URI_PREFIX + "/transaction")
    public Transaction addTransaction(@RequestParam(value="name") String name,
                                      @RequestParam(value="amount") String amount) {
        String sessionID = RequestContextHolder.currentRequestAttributes().getSessionId();
        return model.postTransaction(this.getSessionID(), name, amount);
    }

    /**
     * Method used to retrieve a certain transaction of the user issuing the current request.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return The Transaction with transactionID belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/transaction/{transactionID}")
    public Transaction getTransaction(@PathVariable String transactionID) {
        return model.getTransaction(this.getSessionID(), transactionID);
    }

    /**
     * Method used to update a certain transaction of the user issuing the current request.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param name The new name of the to be updated Transaction (empty String if not updated).
     * @param amount The new amount (in cents) of the to be updated Transaction (empty String if not updated).
     * @return The Transaction updated using this method.
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = RestControllerConstants.URI_PREFIX + "/transaction/{transactionID}")
    public Transaction putTransaction(@PathVariable String transactionID,
                                      @RequestParam(value="name", defaultValue="") String name,
                                      @RequestParam(value="amount", defaultValue="") String amount) {
        return model.putTransaction(this.getSessionID(), transactionID, name, amount);
    }

    /**
     * Method used to remove a certain transaction of the user issuing the current request.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     */
    @RequestMapping(method = RequestMethod.DELETE,
            value = RestControllerConstants.URI_PREFIX + "/transaction/{transactionID}")
    public void deleteTransaction(@PathVariable String transactionID) {
        model.deleteTransaction(this.getSessionID(), transactionID);
    }

}
