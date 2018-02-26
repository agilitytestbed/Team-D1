package nl.utwente.ing.api;

import nl.utwente.ing.model.Model;
import nl.utwente.ing.model.bean.Category;
import nl.utwente.ing.model.bean.Transaction;
import nl.utwente.ing.model.persistentmodel.PersistentModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The MainRestController class.
 * Consists of methods that allow the application to make use of REST using the Spring framework.
 * Makes use of the Model interface (and an implementation of it) to store data.
 *
 * @author Daan Kooij
 */
@RestController
public class MainRestController {

    private Model model;
    private HashMap<String, String> sessionIDMap;

    /**
     * The constructor of MainRestController.
     * Initializes the model and the sessionIDMap.
     */
    public MainRestController() {
        model = new PersistentModel();
        sessionIDMap = new HashMap<>();
    }

    /**
     * Method used to retrieve the sessionID of the user issuing the current request.
     *
     * @return The sessionID of the user issuing the current request.
     */
    private String getSessionID(String parameterSessionID, String headerSessionID) {
        String realSessionID = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!parameterSessionID.equals("")) {
            sessionIDMap.put(realSessionID, parameterSessionID);
        } else if (!headerSessionID.equals("")) {
            sessionIDMap.put(realSessionID, headerSessionID);
        }
        return sessionIDMap.getOrDefault(realSessionID, realSessionID);
    }

    /**
     * Method used to retrieve the transactions belonging to the user issuing the current request.
     *
     * @param category   The category to be filtered on (empty String if no filter).
     * @param limit      The maximum amount of transactions to be fetched.
     * @param offset     The starting index to fetch transactions.
     * @param pSessionID The custom sessionID specified as a request parameter.
     * @param hSessionID The custom sessionID specified in the HTTP header.
     * @return An ArrayList of Transaction belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/transactions")
    public ArrayList<Transaction> getTransactions(@RequestParam(value = "category", defaultValue = "") String category,
                                                  @RequestParam(value = "limit", defaultValue = "20") String limit,
                                                  @RequestParam(value = "offset", defaultValue = "0") String offset,
                                                  @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                                  @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.getTransactions(this.getSessionID(pSessionID, hSessionID), category, limit, offset);

    }

    /**
     * Method used to create a new Transaction for the user issuing the current request.
     *
     * @param name       The name of the to be created Transaction.
     * @param amount     The amount (in cents) of the to be created Transaction.
     * @param pSessionID The custom sessionID specified as a request parameter.
     * @param hSessionID The custom sessionID specified in the HTTP header.
     * @return The Transaction created using this method.
     */
    @RequestMapping(method = RequestMethod.POST,
            value = RestControllerConstants.URI_PREFIX + "/transactions")
    public Transaction postTransaction(@RequestParam(value = "name") String name,
                                       @RequestParam(value = "amount") String amount,
                                       @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                       @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.postTransaction(this.getSessionID(pSessionID, hSessionID), name, amount);
    }

    /**
     * Method used to retrieve a certain Transaction of the user issuing the current request.
     *
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @param pSessionID    The custom sessionID specified as a request parameter.
     * @param hSessionID    The custom sessionID specified in the HTTP header.
     * @return The Transaction with transactionID belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}")
    public Transaction getTransaction(@PathVariable String transactionID,
                                      @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                      @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.getTransaction(this.getSessionID(pSessionID, hSessionID), transactionID);
    }

    /**
     * Method used to update a certain transaction of the user issuing the current request.
     *
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param name          The new name of the to be updated Transaction (empty String if not updated).
     * @param amount        The new amount (in cents) of the to be updated Transaction (empty String if not updated).
     * @param pSessionID    The custom sessionID specified as a request parameter.
     * @param hSessionID    The custom sessionID specified in the HTTP header.
     * @return The Transaction updated using this method.
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}")
    public Transaction putTransaction(@PathVariable String transactionID,
                                      @RequestParam(value = "name", defaultValue = "") String name,
                                      @RequestParam(value = "amount", defaultValue = "") String amount,
                                      @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                      @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.putTransaction(this.getSessionID(pSessionID, hSessionID), transactionID, name, amount);
    }

    /**
     * Method used to remove a certain transaction of the user issuing the current request.
     *
     * @param transactionID The transactionID of the Transaction that will be deleted.
     * @param pSessionID    The custom sessionID specified as a request parameter.
     * @param hSessionID    The custom sessionID specified in the HTTP header.
     */
    @RequestMapping(method = RequestMethod.DELETE,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}")
    public void deleteTransaction(@PathVariable String transactionID,
                                  @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                  @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        model.deleteTransaction(this.getSessionID(pSessionID, hSessionID), transactionID);
    }

    /**
     * Method used to assign a certain category to a certain transaction of the user issuing the current request.
     *
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param categoryID    The categoryID of the Category which will be assigned to a Transaction.
     * @param pSessionID    The custom sessionID specified as a request parameter.
     * @param hSessionID    The custom sessionID specified in the HTTP header.
     * @return The Transaction to which a Category is assigned.
     */
    @RequestMapping(method = RequestMethod.POST,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}/assignCategory")
    public Transaction assignCategoryToTransaction(@PathVariable String transactionID,
                                                   @RequestParam(value = "categoryID") String categoryID,
                                                   @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                                   @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.assignCategoryToTransaction(this.getSessionID(pSessionID, hSessionID), transactionID, categoryID);
    }

    /**
     * Method used to retrieve the categories belonging to the user issuing the current request.
     *
     * @param limit      The maximum amount of categories to be fetched.
     * @param offset     The starting index to fetch categories.
     * @param pSessionID The custom sessionID specified as a request parameter.
     * @param hSessionID The custom sessionID specified in the HTTP header.
     * @return An ArrayList of Category belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/categories")
    public ArrayList<Category> getCategories(@RequestParam(value = "limit", defaultValue = "20") String limit,
                                             @RequestParam(value = "offset", defaultValue = "0") String offset,
                                             @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                             @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.getCategories(this.getSessionID(pSessionID, hSessionID), limit, offset);
    }

    /**
     * Method used to create a new Category for the user issuing the current request.
     *
     * @param categoryName The name of the to be created Category.
     * @param pSessionID   The custom sessionID specified as a request parameter.
     * @param hSessionID   The custom sessionID specified in the HTTP header.
     * @return The Category created by using this method.
     */
    @RequestMapping(method = RequestMethod.POST,
            value = RestControllerConstants.URI_PREFIX + "/categories")
    public Category postCategory(@RequestParam(value = "name") String categoryName,
                                 @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                 @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.postCategory(this.getSessionID(pSessionID, hSessionID), categoryName);
    }

    /**
     * Method used to retrieve a certain Category belonging to the user issuing the current request.
     *
     * @param categoryID The categoryID of the Category that will be retrieved.
     * @param pSessionID The custom sessionID specified as a request parameter.
     * @param hSessionID The custom sessionID specified in the HTTP header.
     * @return The Category with categoryID belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/categories/{categoryID}")
    public Category getCategory(@PathVariable String categoryID,
                                @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.getCategory(this.getSessionID(pSessionID, hSessionID), categoryID);
    }

    /**
     * Method used to update a certain Category belonging to the user issuing the current request.
     *
     * @param categoryID   The categoryID of the Category that will be updated.
     * @param categoryName The new name of the to be updated Category (empty String if not updated).
     * @param pSessionID   The custom sessionID specified as a request parameter.
     * @param hSessionID   The custom sessionID specified in the HTTP header.
     * @return The Category updated using this method.
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = RestControllerConstants.URI_PREFIX + "/categories/{categoryID}")
    public Category putCategory(@PathVariable String categoryID,
                                @RequestParam(value = "name", defaultValue = "") String categoryName,
                                @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                                @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        return model.putCategory(this.getSessionID(pSessionID, hSessionID), categoryID, categoryName);
    }

    /**
     * Method used to remove a certain Category belonging to the user issuing the current request.
     *
     * @param categoryID The categoryID of the Category that will be deleted.
     * @param pSessionID The custom sessionID specified as a request parameter.
     * @param hSessionID The custom sessionID specified in the HTTP header.
     */
    @RequestMapping(method = RequestMethod.DELETE,
            value = RestControllerConstants.URI_PREFIX + "/categories/{categoryID}")
    public void deleteCategory(@PathVariable String categoryID,
                               @RequestParam(value = "sessionID", defaultValue = "") String pSessionID,
                               @RequestHeader(value = "sessionID", defaultValue = "") String hSessionID) {
        model.deleteCategory(this.getSessionID(pSessionID, hSessionID), categoryID);
    }

}
