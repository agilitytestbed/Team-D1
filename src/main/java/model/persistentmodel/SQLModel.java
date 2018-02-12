package model.persistentmodel;

import model.bean.Category;
import model.bean.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLModel {

    private Connection connection;
    private HashMap<String, String> preparedStatements;

    public SQLModel() {
        connection = DatabaseConnection.getDatabaseConnection();
        preparedStatements = new HashMap<>();

        // getTransactions
        preparedStatements.put("getTransactions1",
                "SELECT t.transaction_id, t.name, t.amount\n" +
                "FROM Money_Transaction t, User_Transaction u\n" +
                "WHERE t.transaction_id = u.transaction_id\n" +
                "AND u.session_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;"
        );
        preparedStatements.put("getTransaction2",
                "SELECT t.transaction_id, t.name, t.amount\n" +
                "FROM Money_Transaction t, Transaction_Category tc, User_Transaction u\n" +
                "WHERE t.transaction_id = tc.transaction_id\n" +
                "AND t.transaction_id = u.transaction_id\n" +
                "AND tc.category_id = ?\n" +
                "AND u.session_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;"
        );

        // postTransaction
        preparedStatements.put("postTransaction1",
                "INSERT INTO Money_Transaction (name, amount)\n" +
                "VALUES (?, ?);\n"
        );
        preparedStatements.put("postTransaction2",
                "SELECT MAX(transaction_id)\n" +
                "FROM Money_Transaction;"
        );
        preparedStatements.put("postTransaction3",
                "SELECT transaction_id, name, amount\n" +
                "FROM Money_Transaction\n" +
                "WHERE transaction_id = ?;"
        );

        // getTransaction
        preparedStatements.put("getTransaction",
                "SELECT t.transaction_id, t.name, t.amount\n" +
                "FROM Money_Transaction t, User_Transaction u\n" +
                "WHERE t.transaction_id = u.transaction_id\n" +
                "AND t.transaction_id = ?\n" +
                "AND u.session_id = ?;"
        );

        // putTransaction
        preparedStatements.put("putTransaction1",
                "UPDATE Money_Transaction\n" +
                "SET name = ?\n" +
                "WHERE transaction_id = ?;"
        );
        preparedStatements.put("putTransaction2",
                "UPDATE Money_Transaction\n" +
                "SET amount = ?\n" +
                "WHERE transaction_id = ?;"
        );

        // deleteTransaction
        preparedStatements.put("deleteTransaction",
                "DELETE FROM Money_Transaction\n" +
                "WHERE transaction_id = ?;"
        );

        // assignCategoryToTransaction
        preparedStatements.put("assignCategoryToTransaction",
                "INSERT INTO Transaction_Category (transaction_id, category_id)\n" +
                "VALUES (?, ?);"
        );

        // getCategories
        preparedStatements.put("getCategories",
                "SELECT c.category_id, c.name\n" +
                "FROM Category c, User_Category u\n" +
                "WHERE c.category_id = u.category_id\n" +
                "AND u.session_id = ?\n" +
                "LIMIT ?\n" +
                "OFFSET ?;"
        );

        // postCategory
        preparedStatements.put("postCategory1",
                "INSERT INTO Category (name)\n" +
                "VALUES (?);"
        );
        preparedStatements.put("postCategory2",
                "SELECT MAX(category_id)\n" +
                "FROM Category;"
        );
        preparedStatements.put("postCategory3",
                "SELECT category_id, name\n" +
                "FROM Category\n" +
                "WHERE category_id = ?;"
        );

        // getCategory
        preparedStatements.put("getCategory",
                "SELECT c.category_id, c.name\n" +
                "FROM Category c, User_Category u\n" +
                "WHERE c.category_id = u.category_id\n" +
                "AND c.category_id = ?\n" +
                "AND u.session_id = ?;"
        );

        // putCategory
        preparedStatements.put("putCategory",
                "UPDATE Category\n" +
                "SET name = ?\n" +
                "WHERE category_id = ?;"
        );

        // deleteCategory
        preparedStatements.put("deleteCategory",
                "DELETE FROM Category\n" +
                "WHERE category_id = ?;"
        );

        // Miscellaneous
        preparedStatements.put("assignTransactionToUser",
                "INSERT INTO User_Transaction (session_id, transaction_id)\n" +
                "VALUES (?, ?);"
        );
        preparedStatements.put("assignCategoryToUser",
                "INSERT INTO User_Category (session_id, category_id)\n" +
                "VALUES (?, ?);"
        );
        preparedStatements.put("getCategoryByTransactionID",
                "SELECT c.category_id, c.name\n" +
                "FROM Category c, Transaction_Category tc\n" +
                "WHERE c.category_id = tc.category_id\n" +
                "AND tc.transaction_id = ?;"
        );

    }

    public ArrayList<Transaction> getTransactions(String sessionID, String category, String limit, String offset) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement;
            boolean hasCategory = false;
            if (category.equals("")) {
                statement = connection.prepareStatement(preparedStatements.get("getTransactions1"));
                statement.setString(1, sessionID);
                statement.setInt(2, Integer.parseInt(limit));
                statement.setInt(3, Integer.parseInt(offset));
            } else {
                statement = connection.prepareStatement(preparedStatements.get("getTransactions2"));
                statement.setInt(1, Integer.parseInt(category));
                statement.setString(2, sessionID);
                statement.setInt(3, Integer.parseInt(limit));
                statement.setInt(4, Integer.parseInt(offset));
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = this.createTransactionUsingResultSet(resultSet);

                statement = connection.prepareStatement(preparedStatements.get("getCategoryByTransactionID"));
                statement.setInt(1, transaction.getTransactionID());
                ResultSet resultSet2 = statement.executeQuery();
                if (resultSet2.next()) {
                    transaction.setCategory(this.createCategoryUsingResultSet(resultSet2));
                }

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public Transaction postTransaction(String name, String amount) {
        Transaction transaction = null;
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("postTransaction1"));
            statement.setString(1, name);
            statement.setLong(2, Long.parseLong(amount));
            statement.executeUpdate();

            statement = connection.prepareStatement(preparedStatements.get("postTransaction2"));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int transaction_id = resultSet.getInt(1);
            connection.commit();
            connection.setAutoCommit(true);

            statement = connection.prepareStatement(preparedStatements.get("postTransaction3"));
            statement.setInt(1, transaction_id);
            resultSet = statement.executeQuery();
            resultSet.next();
            transaction = this.createTransactionUsingResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public void assignTransactionToUser(String sessionID, int transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("assignTransactionToUser"));
            statement.setString(1, sessionID);
            statement.setInt(2, transactionID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Transaction getTransaction(String sessionID, String transactionID) {
        Transaction transaction = null;
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getTransaction"));
            statement.setInt(1, Integer.parseInt(transactionID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                transaction = this.createTransactionUsingResultSet(resultSet);
                statement = connection.prepareStatement(preparedStatements.get("getCategoryByTransactionID"));
                statement.setInt(1, transaction.getTransactionID());
                ResultSet resultSet2 = statement.executeQuery();
                if (resultSet2.next()) {
                    transaction.setCategory(this.createCategoryUsingResultSet(resultSet2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public Transaction putTransaction(String sessionID, String transactionID, String name, String amount) {
        Transaction transaction = null;
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getTransaction"));
            statement.setInt(1, Integer.parseInt(transactionID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                transaction = this.createTransactionUsingResultSet(resultSet);
                statement = connection.prepareStatement(preparedStatements.get("getCategoryByTransactionID"));
                statement.setInt(1, Integer.parseInt(transactionID));
                ResultSet resultSet2 = statement.executeQuery();
                if (resultSet2.next()) {
                    transaction.setCategory(this.createCategoryUsingResultSet(resultSet2));
                }
                if (!name.equals("")) {
                    statement = connection.prepareStatement(preparedStatements.get("putTransaction1"));
                    statement.setString(1, name);
                    statement.setInt(2, transaction.getTransactionID());
                    statement.executeUpdate();
                    transaction.setName(name);
                }
                if (!amount.equals("")) {
                    statement = connection.prepareStatement(preparedStatements.get("putTransaction2"));
                    statement.setLong(1, Long.parseLong(amount));
                    statement.setInt(2, transaction.getTransactionID());
                    statement.executeUpdate();
                    transaction.setAmount(Long.parseLong(amount));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public void deleteTransaction(String sessionID, String transactionID) {
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getTransaction"));
            statement.setInt(1, Integer.parseInt(transactionID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                statement = connection.prepareStatement(preparedStatements.get("deleteTransaction"));
                statement.setInt(1, resultSet.getInt(1));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Transaction assignCategoryToTransaction(String sessionID, String transactionID, String categoryID) {
        Transaction transaction = null;
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getTransaction"));
            statement.setInt(1, Integer.parseInt(transactionID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                PreparedStatement statement2 = connection.prepareStatement(preparedStatements.get("getCategory"));
                statement2.setInt(1, Integer.parseInt(categoryID));
                statement2.setString(2, sessionID);
                ResultSet resultSet2 = statement2.executeQuery();
                if (resultSet2.next()) {
                    statement2 = connection.prepareStatement(preparedStatements.get("assignCategoryToTransaction"));
                    statement2.setInt(1, resultSet.getInt(1));
                    statement2.setInt(2, resultSet2.getInt(1));
                    statement2.executeUpdate();
                }
            }
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                transaction = this.createTransactionUsingResultSet(resultSet);
                statement = connection.prepareStatement(preparedStatements.get("getCategoryByTransactionID"));
                statement.setInt(1, transaction.getTransactionID());
                ResultSet resultSet2 = statement.executeQuery();
                if (resultSet2.next()) {
                    transaction.setCategory(this.createCategoryUsingResultSet(resultSet2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public ArrayList<Category> getCategories(String sessionID, String limit, String offset) {
        ArrayList<Category> categories = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getCategories"));
            statement.setString(1, sessionID);
            statement.setInt(2, Integer.parseInt(limit));
            statement.setInt(3, Integer.parseInt(offset));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(this.createCategoryUsingResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Category postCategory(String sessionID, String categoryName) {
        Category category = null;
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("postCategory1"));
            statement.setString(1, categoryName);
            statement.executeUpdate();

            statement = connection.prepareStatement(preparedStatements.get("postCategory2"));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int categoryID = resultSet.getInt(1);
            connection.commit();
            connection.setAutoCommit(true);

            statement = connection.prepareStatement(preparedStatements.get("postCategory3"));
            statement.setInt(1, categoryID);
            resultSet = statement.executeQuery();
            resultSet.next();
            category = this.createCategoryUsingResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public void assignCategoryToUser(String sessionID, int categoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("assignCategoryToUser"));
            statement.setString(1, sessionID);
            statement.setInt(2, categoryID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Category getCategory(String sessionID, String categoryID) {
        Category category = null;
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getCategory"));
            statement.setInt(1, Integer.parseInt(categoryID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                category = this.createCategoryUsingResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public Category putCategory(String sessionID, String categoryID, String categoryName) {
        Category category = null;
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getCategory"));
            statement.setInt(1, Integer.parseInt(categoryID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                category = this.createCategoryUsingResultSet(resultSet);
                if (!categoryName.equals("")) {
                    statement = connection.prepareStatement(preparedStatements.get("putCategory"));
                    statement.setString(1, categoryName);
                    statement.setInt(2, category.getCategoryID());
                    statement.executeUpdate();
                    category.setName(categoryName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public void deleteCategory(String sessionID, String categoryID) {
        Category category = null;
        try {
            PreparedStatement statement = connection.prepareStatement(preparedStatements.get("getCategory"));
            statement.setInt(1, Integer.parseInt(categoryID));
            statement.setString(2, sessionID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                statement = connection.prepareStatement(preparedStatements.get("deleteCategory"));
                statement.setInt(1, resultSet.getInt(1));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Transaction createTransactionUsingResultSet(ResultSet resultSet) throws SQLException {
        int transactionID = resultSet.getInt(1);
        String name = resultSet.getString(2);
        long amount = resultSet.getLong(3);
        return new Transaction(transactionID, name, amount);
    }

    private Category createCategoryUsingResultSet(ResultSet resultSet) throws SQLException {
        int categoryID = resultSet.getInt(1);
        String name = resultSet.getString(2);
        return new Category(categoryID, name);
    }

}
