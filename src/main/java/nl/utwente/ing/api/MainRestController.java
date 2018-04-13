package nl.utwente.ing.api;

import nl.utwente.ing.exception.InvalidSessionIDException;
import nl.utwente.ing.exception.ResourceNotFoundException;
import nl.utwente.ing.model.Model;
import nl.utwente.ing.model.bean.Category;
import nl.utwente.ing.model.bean.Transaction;
import nl.utwente.ing.model.persistentmodel.PersistentModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * The constructor of MainRestController.
     * Initializes the model.
     */
    public MainRestController() {
        model = new PersistentModel();
    }

    /**
     * Method used to retrieve the sessionID of the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @return The sessionID of the user issuing the current request.
     */
    private String getSessionID(String pSessionID, String hSessionID) throws InvalidSessionIDException {
        if (!pSessionID.equals("")) {
            return pSessionID;
        } else {
            if (!hSessionID.equals("")) {
                return hSessionID;
            } else {
                throw new InvalidSessionIDException();
            }
        }
    }

    /**
     * Method used to retrieve the transactions belonging to the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param category   The category to be filtered on (empty String if no filter).
     * @param limit      The maximum amount of transactions to be fetched.
     * @param offset     The starting index to fetch transactions.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * an ArrayList of Transaction belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/transactions")
    public ResponseEntity getTransactions(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                          @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                          @RequestParam(value = "category", defaultValue = "") String category,
                                          @RequestParam(value = "limit", defaultValue = "20") String limit,
                                          @RequestParam(value = "offset", defaultValue = "0") String offset) {
        int limitInt = 20;
        int offsetInt = 0;
        try {
            limitInt = Integer.parseInt(limit);
            if (limitInt < 1 || limitInt > 100) {
                limitInt = 20;
            }
        } catch (NumberFormatException e) {
            // Do nothing
        }
        try {
            offsetInt = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            // Do nothing
        }
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            ArrayList<Transaction> transactions = model.getTransactions(sessionID, category, limitInt, offsetInt);
            return ResponseEntity.status(200).body(transactions);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        }
    }

    /**
     * Method used to create a new Transaction for the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param t          The Transaction object as specified in the json request body.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Transaction created using this method.
     */
    @RequestMapping(method = RequestMethod.POST,
            value = RestControllerConstants.URI_PREFIX + "/transactions")
    public ResponseEntity postTransaction(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                          @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                          @RequestBody Transaction t) {
        if (t == null || t.getDate() == null || t.getAmount() == 0 || t.getExternalIBAN() == null || t.getType() == null) {
            return ResponseEntity.status(405).body("Invalid input given");
        }
        if (!t.getType().equals("deposit") || !t.getType().equals("withdrawal")) {
            return ResponseEntity.status(405).body("Invalid input given (type should be 'deposit' or 'withdrawal')");
        }
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            Transaction transaction;
            if (t.getCategory() != null) {
                transaction = model.postTransaction(sessionID, t.getDate(), t.getAmount(), t.getExternalIBAN(),
                        t.getType(), t.getCategory().getID());
            } else {
                transaction = model.postTransaction(sessionID, t.getDate(), t.getAmount(), t.getExternalIBAN(),
                        t.getType(), 0);
            }
            return ResponseEntity.status(201).body(transaction);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to retrieve a certain Transaction of the user issuing the current request.
     *
     * @param pSessionID    The sessionID specified in the request parameters.
     * @param hSessionID    The sessionID specified in the HTTP header.
     * @param transactionID The transactionID of the Transaction that will be retrieved.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Transaction with transactionID belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}")
    public ResponseEntity getTransaction(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                         @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                         @PathVariable String transactionID) {
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long transactionIDLong = Long.parseLong(transactionID);
            Transaction transaction = model.getTransaction(sessionID, transactionIDLong);
            return ResponseEntity.status(200).body(transaction);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to update a certain transaction of the user issuing the current request.
     *
     * @param pSessionID    The sessionID specified in the request parameters.
     * @param hSessionID    The sessionID specified in the HTTP header.
     * @param transactionID The transactionID of the Transaction that will be updated.
     * @param t             The Transaction object as specified in the json HTTP body.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Transaction updated using this method.
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}")
    public ResponseEntity putTransaction(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                         @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                         @PathVariable String transactionID,
                                         @RequestBody Transaction t) {
        if (t == null) {
            return ResponseEntity.status(405).body("Invalid input given");
        }
        if (!t.getType().equals("deposit") || !t.getType().equals("withdrawal")) {
            return ResponseEntity.status(405).body("Invalid input given (type should be 'deposit' or 'withdrawal')");
        }
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long transactionIDLong = Long.parseLong(transactionID);
            Transaction transaction;
            if (t.getCategory() != null) {
                transaction = model.putTransaction(sessionID, transactionIDLong, t.getDate(), t.getAmount(),
                        t.getExternalIBAN(), t.getType(), t.getCategory().getID());
            } else {
                transaction = model.putTransaction(sessionID, transactionIDLong, t.getDate(), t.getAmount(),
                        t.getExternalIBAN(), t.getType(), 0);
            }
            return ResponseEntity.status(200).body(transaction);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to remove a certain transaction of the user issuing the current request.
     *
     * @param pSessionID    The sessionID specified in the request parameters.
     * @param hSessionID    The sessionID specified in the HTTP header.
     * @param transactionID The transactionID of the Transaction that will be deleted.
     * @return A ResponseEntity containing a HTTP status code and a status message.
     */
    @RequestMapping(method = RequestMethod.DELETE,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}")
    public ResponseEntity deleteTransaction(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                            @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                            @PathVariable String transactionID) {
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long transactionIDLong = Long.parseLong(transactionID);
            model.deleteTransaction(sessionID, transactionIDLong);
            return ResponseEntity.status(204).body("Resource deleted");
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to assign a certain category to a certain transaction of the user issuing the current request.
     *
     * @param pSessionID    The sessionID specified in the request parameters.
     * @param hSessionID    The sessionID specified in the HTTP header.
     * @param transactionID The transactionID of the Transaction to which the Category will be assigned.
     * @param body          The body of the json request.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Transaction to which a Category is assigned.
     */
    @RequestMapping(method = RequestMethod.PATCH,
            value = RestControllerConstants.URI_PREFIX + "/transactions/{transactionID}/category")
    public ResponseEntity assignCategoryToTransaction(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                                      @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                                      @PathVariable String transactionID,
                                                      @RequestBody Map<String, String> body) {
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long transactionIDLong = Long.parseLong(transactionID);
            long categoryIDLong = Long.parseLong(body.get("category_id"));
            Transaction transaction = model.assignCategoryToTransaction(sessionID, transactionIDLong, categoryIDLong);
            return ResponseEntity.status(200).body(transaction);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to retrieve the categories belonging to the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param limit      The maximum amount of categories to be fetched.
     * @param offset     The starting index to fetch categories.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * an ArrayList of Category belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/categories")
    public ResponseEntity getCategories(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                        @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                        @RequestParam(value = "limit", defaultValue = "20") String limit,
                                        @RequestParam(value = "offset", defaultValue = "0") String offset) {
        int limitInt = 20;
        int offsetInt = 0;
        try {
            limitInt = Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            // Do nothing
        }
        try {
            offsetInt = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            // Do nothing
        }
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            ArrayList<Category> categories = model.getCategories(sessionID, limitInt, offsetInt);
            return ResponseEntity.status(200).body(categories);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        }
    }

    /**
     * Method used to create a new Category for the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param c          The Category object as specified in the json HTTP body.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Category created by using this method.
     */
    @RequestMapping(method = RequestMethod.POST,
            value = RestControllerConstants.URI_PREFIX + "/categories")
    public ResponseEntity postCategory(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                       @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                       @RequestBody Category c) {
        if (c == null || c.getName() == null) {
            return ResponseEntity.status(405).body("Invalid input given");
        }
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            Category category = model.postCategory(sessionID, c.getName());
            return ResponseEntity.status(201).body(category);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        }
    }

    /**
     * Method used to retrieve a certain Category belonging to the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param categoryID The categoryID of the Category that will be retrieved.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Category with categoryID belonging to the user issuing the current request.
     */
    @RequestMapping(method = RequestMethod.GET,
            value = RestControllerConstants.URI_PREFIX + "/categories/{categoryID}")
    public ResponseEntity getCategory(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                      @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                      @PathVariable String categoryID) {
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long categoryIDLong = Long.parseLong(categoryID);
            Category category = model.getCategory(sessionID, categoryIDLong);
            return ResponseEntity.status(200).body(category);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to update a certain Category belonging to the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param categoryID The categoryID of the Category that will be updated.
     * @param c          The Category object as specified in the json HTTP body.
     * @return A ResponseEntity containing a HTTP status code and either a status message or
     * the Category updated using this method.
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = RestControllerConstants.URI_PREFIX + "/categories/{categoryID}")
    public ResponseEntity putCategory(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                      @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                      @PathVariable String categoryID,
                                      @RequestBody Category c) {
        if (c == null) {
            return ResponseEntity.status(405).body("Invalid input given");
        }
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long categoryIDLong = Long.parseLong(categoryID);
            Category category = model.putCategory(sessionID, categoryIDLong, c.getName());
            return ResponseEntity.status(200).body(category);
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to remove a certain Category belonging to the user issuing the current request.
     *
     * @param pSessionID The sessionID specified in the request parameters.
     * @param hSessionID The sessionID specified in the HTTP header.
     * @param categoryID The categoryID of the Category that will be deleted.
     * @return A ResponseEntity containing a HTTP status code and a status message.
     */
    @RequestMapping(method = RequestMethod.DELETE,
            value = RestControllerConstants.URI_PREFIX + "/categories/{categoryID}")
    public ResponseEntity deleteCategory(@RequestParam(value = "WWW_Authenticate", defaultValue = "") String pSessionID,
                                         @RequestHeader(value = "WWW_Authenticate", defaultValue = "") String hSessionID,
                                         @PathVariable String categoryID) {
        try {
            String sessionID = this.getSessionID(pSessionID, hSessionID);
            long categoryIDLong = Long.parseLong(categoryID);
            model.deleteCategory(sessionID, categoryIDLong);
            return ResponseEntity.status(204).body("Resource deleted");
        } catch (InvalidSessionIDException e) {
            return ResponseEntity.status(401).body("Session ID is missing or invalid");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(404).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Resource not found");
        }
    }

    /**
     * Method used to generate and retrieve a new sessionID.
     *
     * @return A ResponseEntity containing a HTTP status code the sessionID generated by this method.
     */
    @RequestMapping(method = RequestMethod.POST, value = RestControllerConstants.URI_PREFIX + "/sessions")
    public ResponseEntity getSessionID() {
        return ResponseEntity.status(201).body(model.getSession());
    }

}
