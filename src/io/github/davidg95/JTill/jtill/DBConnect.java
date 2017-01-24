/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import io.github.davidg95.JTill.jtill.Staff.Position;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * Database connection class which handles communication with the database.
 *
 * @author David
 */
public class DBConnect implements DataConnectInterface {

    private Connection con;
    private Driver embedded;

    public String address;
    public String username;
    public String password;

    private boolean connected;

    private final Semaphore productSem;
    private final Semaphore customerSem;
    private final Semaphore staffSem;
    private final Semaphore discountSem;
    private final Semaphore taxSem;
    private final Semaphore categorySem;
    private final Semaphore saleSem;
    private final Semaphore voucherSem;
    private final Semaphore screensSem;

    private static Properties properties;
    public static int PORT = 600;
    public static int MAX_CONNECTIONS = 10;
    public static int MAX_QUEUE = 10;
    public static String hostName;
    public static String DB_ADDRESS = "jdbc:derby:TillEmbedded;";
    public static String DB_USERNAME = "APP";
    public static String DB_PASSWORD = "App";
    public static final String DEFAULT_ADDRESS = "jdbc:derby:TillEmbedded;";
    public static final String DEFAULT_USERNAME = "APP";
    public static final String DEFAULT_PASSWORD = "App";

    private GUIInterface g;

    public DBConnect() {
        productSem = new Semaphore(1);
        customerSem = new Semaphore(1);
        staffSem = new Semaphore(1);
        discountSem = new Semaphore(1);
        taxSem = new Semaphore(1);
        categorySem = new Semaphore(1);
        saleSem = new Semaphore(1);
        voucherSem = new Semaphore(1);
        screensSem = new Semaphore(1);
    }

    /**
     * Method to make a new connection with the database.
     *
     * @param database_address the url of the database.
     * @param username username to log on to the database.
     * @param password password to log on to the database.
     * @throws SQLException if there was a log on error.
     */
    public void connect(String database_address, String username, String password) throws SQLException {
        con = DriverManager.getConnection(database_address, username, password);
        this.address = database_address;
        this.username = username;
        this.password = password;
        connected = true;
    }

    public void create(String address, String username, String password) throws SQLException {
        embedded = new EmbeddedDriver();
        DriverManager.registerDriver(embedded);
        con = DriverManager.getConnection(address, username, password);

        this.address = address;
        this.username = username;
        this.password = password;
        connected = true;
        createTables();
    }

    private void createTables() throws SQLException {
        String categorys = "create table APP.CATEGORYS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "     SELL_START TIME,\n"
                + "     SELL_END TIME,\n"
                + "     TIME_RESTRICT BOOLEAN not null,\n"
                + "     MINIMUM_AGE INT not null\n"
                + ")";
        String discounts = "create table \"APP\".DISCOUNTS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	PERCENTAGE DOUBLE not null\n"
                + ")";
        String tax = "create table \"APP\".TAX\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null,\n"
                + "	VALUE DOUBLE not null\n"
                + ")";
        String configs = "create table APP.CONFIGS\n"
                + "(\n"
                + "	NAME INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	VALUE VARCHAR(20) not null\n"
                + ")";
        String sales = "create table APP.SALES\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "     PRICE DOUBLE,\n"
                + "     CUSTOMER int,\n"
                + "     TIMESTAMP TIME\n"
                + ")";
        String saleItems = "create table APP.SALEITEMS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "     PRODUCT_ID INT not null references PRODUCTS(ID),\n"
                + "     SALE_ID INT not null references SALES(ID)\n"
                + ")";
        String customers = "create table \"APP\".CUSTOMERS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	PHONE VARCHAR(15),\n"
                + "	MOBILE VARCHAR(15),\n"
                + "	EMAIL VARCHAR(50),\n"
                + "	ADDRESS_LINE_1 VARCHAR(50),\n"
                + "	ADDRESS_LINE_2 VARCHAR(50),\n"
                + "	TOWN VARCHAR(50),\n"
                + "	COUNTY VARCHAR(50),\n"
                + "	COUNTRY VARCHAR(50),\n"
                + "	POSTCODE VARCHAR(20),\n"
                + "	NOTES VARCHAR(200),\n"
                + "	DISCOUNT_ID INT references DISCOUNTS(ID),\n"
                + "	LOYALTY_POINTS INTEGER not null\n"
                + ")";
        String products = "create table \"APP\".PRODUCTS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	BARCODE VARCHAR(20),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "     OPEN_PRICE BOOLEAN not null,\n"
                + "	PRICE DOUBLE,\n"
                + "	STOCK INTEGER,\n"
                + "	COMMENTS VARCHAR(200),\n"
                + "	SHORT_NAME VARCHAR(50) not null,\n"
                + "	CATEGORY_ID INT not null references CATEGORYS(ID),\n"
                + "	TAX_ID INT not null references TAX(ID),\n"
                + "	COST_PRICE DOUBLE,\n"
                + "	MIN_PRODUCT_LEVEL INTEGER,\n"
                + "	MAX_PRODUCT_LEVEL INTEGER,\n"
                + "	DISCOUNT_ID INT not null references DISCOUNTS(ID)\n"
                + ")";
        String staff = "create table \"APP\".STAFF\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(50) not null,\n"
                + "	POSITION VARCHAR(20) not null,\n"
                + "	USERNAME VARCHAR(20) not null,\n"
                + "	PASSWORD VARCHAR(20) not null\n"
                + ")";
        String vouchers = "create table \"APP\".VOUCHERS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     NAME VARCHAR(50) not null,\n"
                + "     TYPE VARCHAR(30) not null,\n"
                + "     FIELD1 VARCHAR(50),\n"
                + "     FIELD2 VARCHAR(50),\n"
                + "     FIELD3 VARCHAR(50),\n"
                + "     FIELD4 VARCHAR(50),\n"
                + "     FIELD5 VARCHAR(50),\n"
                + "     FIELD6 VARCHAR(50),\n"
                + "     FIELD7 VARCHAR(50),\n"
                + "     FIELD8 VARCHAR(50)\n"
                + ")";
        String screens = "create table \"APP\".SCREENS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     NAME VARCHAR(50) not null,\n"
                + "     POSITION INTEGER,\n"
                + "     COLOR INT\n"
                + ")";
        String buttons = "create table \"APP\".BUTTONS\n"
                + "(\n"
                + "     ID INT not null primary key\n"
                + "         GENERATED ALWAYS AS IDENTITY\n"
                + "         (START WITH 1, INCREMENT BY 1),\n"
                + "     NAME VARCHAR(50) not null,\n"
                + "     POSITION INTEGER,\n"
                + "     PRODUCT INT not null references PRODUCTS(ID),\n"
                + "     COLOR INT,\n"
                + "     SCREEN_ID INT not null references SCREENS(ID)\n"
                + ")";

        Statement stmt = con.createStatement();
        try {
            stmt.execute(tax);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(categorys);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(discounts);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(configs);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(sales);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(customers);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(products);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(saleItems);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(staff);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(vouchers);
        } catch (SQLException ex) {
        }
        try {
            stmt.execute(screens);
        } catch (SQLException ex) {
            error(ex);
        }
        try {
            stmt.execute(buttons);
        } catch (SQLException ex) {
            error(ex);
        }

        String addCategory = "INSERT INTO CATEGORYS (NAME, TIME_RESTRICT, MINIMUM_AGE) VALUES ('Default','FALSE',0)";
        String addTax = "INSERT INTO TAX (NAME, VALUE) VALUES ('ZERO',0.0)";
        String addDiscount = "INSERT INTO DISCOUNTS (NAME, PERCENTAGE) VALUES ('NONE',0.0)";
        stmt.executeUpdate(addCategory);
        stmt.executeUpdate(addTax);
        stmt.executeUpdate(addDiscount);
    }

    private void error(SQLException ex) {
        JOptionPane.showMessageDialog(null, ex, "Database error", JOptionPane.ERROR_MESSAGE);
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Method to close the database connection. This will close the data sets
     * and close the connection.
     */
    @Override
    public void close() {
        try {
            con.close();
            connected = false;
        } catch (SQLException ex) {

        }
    }

    /**
     * Method to check if the database is currently connected.
     *
     * @return true if it connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        Statement stmt = con.createStatement();
        List<Product> products;
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ResultSet set = stmt.executeQuery(query);
            products = new ArrayList<>();
            while (set.next()) {
                int code = set.getInt("ID");
                String barcode = set.getString("BARCODE");
                String name = set.getString("NAME");
                boolean open = set.getBoolean("OPEN_PRICE");
                double price = set.getDouble("PRICE");
                int stock = set.getInt("STOCK");
                String comments = set.getString("COMMENTS");
                String shortName = set.getString("SHORT_NAME");
                int categoryID = set.getInt("CATEGORY_ID");
                int taxID = set.getInt("TAX_ID");
                double costPrice = set.getDouble("COST_PRICE");
                int minStock = set.getInt("MIN_PRODUCT_LEVEL");
                int maxStock = set.getInt("MAX_PRODUCT_LEVEL");
                int discountID = set.getInt("DISCOUNT_ID");

                Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, open, price, costPrice, stock, minStock, maxStock, barcode, code);

                products.add(p);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }

        return products;
    }

    private List<Product> getProductsFromResultSet(ResultSet set) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (set.next()) {
            int code = set.getInt("ID");
            String barcode = set.getString("BARCODE");
            String name = set.getString("NAME");
            boolean open = set.getBoolean("OPEN_PRICE");
            double price = set.getDouble("PRICE");
            int stock = set.getInt("STOCK");
            String comments = set.getString("COMMENTS");
            String shortName = set.getString("SHORT_NAME");
            int categoryID = set.getInt("CATEGORY_ID");
            int taxID = set.getInt("TAX_ID");
            double costPrice = set.getDouble("COST_PRICE");
            int minStock = set.getInt("MIN_PRODUCT_LEVEL");
            int maxStock = set.getInt("MAX_PRODUCT_LEVEL");
            int discountID = set.getInt("DISCOUNT_ID");

            Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, open, price, costPrice, stock, minStock, maxStock, barcode, code);

            products.add(p);
        }

        return products;
    }

    /**
     * Method to add a new product to the database.
     *
     * @param p the new product to add.
     * @throws SQLException if there was an error adding the product to the
     * database.
     */
    @Override
    public void addProduct(Product p) throws SQLException {
        String query = "INSERT INTO PRODUCTS (BARCODE, NAME, OPEN_PRICE, PRICE, STOCK, COMMENTS, SHORT_NAME, CATEGORY_ID, TAX_ID, COST_PRICE, MIN_PRODUCT_LEVEL, MAX_PRODUCT_LEVEL, DISCOUNT_ID) VALUES (" + p.getSQLInsertString() + ")";
        try (Statement stmt = con.createStatement()) {
            try {
                productSem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                stmt.executeUpdate(query);
            } catch (SQLException ex) {
                throw ex;
            } finally {
                productSem.release();
            }
        }
    }

    @Override
    public Product updateProduct(Product p) throws SQLException, ProductNotFoundException {
        String query = p.getSQlUpdateString();
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        if (value == 0) {
            throw new ProductNotFoundException(p.getProductCode() + "");
        }
        return p;
    }

    /**
     * Method to check if a barcode already exists in the database.
     *
     * @param barcode the barcode to check.
     * @return true or false indicating whether the barcode already exists.
     * @throws SQLException if there was an error checking the barcode.
     */
    @Override
    public boolean checkBarcode(String barcode) throws SQLException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.BARCODE = '" + barcode + "'";
        ResultSet res;
        List<Product> lp;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            res = stmt.executeQuery(query);
            lp = getProductsFromResultSet(res);
            res.close();
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }

        return !lp.isEmpty();
    }

    /**
     * Method to remove a product from the database.
     *
     * @param p the product to remove.
     * @throws SQLException if there was an error removing the product.
     * @throws ProductNotFoundException if the product was not found.
     */
    @Override
    public void removeProduct(Product p) throws SQLException, ProductNotFoundException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + p.getProductCode();
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        if (value == 0) {
            throw new ProductNotFoundException(p.getProductCode() + "");
        }
    }

    /**
     * Method to remove a product from the database.
     *
     * @param id the product to remove.
     * @throws SQLException if there was an error removing the product.
     * @throws ProductNotFoundException if the product code was not found.
     */
    @Override
    public void removeProduct(int id) throws SQLException, ProductNotFoundException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + id + "";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        if (value == 0) {
            throw new ProductNotFoundException(id + "");
        }
    }

    /**
     * Method to purchase a product and reduce its stock level by 1.
     *
     * @param code the code of the product to purchase.
     * @return the new stock level.
     * @throws SQLException if there was an error purchasing the product.
     * @throws OutOfStockException if the product is out of stock.
     * @throws ProductNotFoundException if the product was not found.
     */
    @Override
    public int purchaseProduct(int code) throws SQLException, OutOfStockException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID=" + code;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res = stmt.executeQuery(query);
        try {
            while (res.next()) {
                int stock = res.getInt("STOCK");
                res.close();
                if (stock > 0) {
                    stock--;
                } else {
                    throw new OutOfStockException(code + "");
                }
                String update = "UPDATE PRODUCTS SET STOCK=" + stock + " WHERE PRODUCTS.ID=" + code;
                stmt = con.createStatement();
                stmt.executeUpdate(update);
                return stock;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        throw new ProductNotFoundException(code + "");
    }

    /**
     * Method to get a product by its code.
     *
     * @param code the product to get.
     * @return the Product that matches the code.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    @Override
    public Product getProduct(int code) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID = " + code;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Product> products;

        try {
            ResultSet res = stmt.executeQuery(query);

            products = getProductsFromResultSet(res);
            if (products.isEmpty()) {
                throw new ProductNotFoundException("Product " + code + " could not be found");
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        return products.get(0);
    }

    /**
     * Method to get a product by its barcode.
     *
     * @param barcode the barcode to search.
     * @return the product that matches the barcode.
     * @throws SQLException if there was an error getting the product.
     * @throws ProductNotFoundException if the product could not be found.
     */
    @Override
    public Product getProductByBarcode(String barcode) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM Products WHERE PRODUCTS.BARCODE = '" + barcode + "'";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Product> products;

        try {
            ResultSet res = stmt.executeQuery(query);

            products = getProductsFromResultSet(res);
            if (products.isEmpty()) {
                throw new ProductNotFoundException(barcode + " could not be found");
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        return products.get(0);
    }

    /**
     * Method to set the stock level of a product.
     *
     * @param code the product to set.
     * @param stock the new stock level.
     * @throws SQLException if there was an error setting the stock.
     * @throws ProductNotFoundException if the product could not be found.
     */
    @Override
    public void setStock(int code, int stock) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID=" + code;
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                res.close();
                String update = "UPDATE PRODUCTS SET STOCK=" + stock + " WHERE PRODUCTS.ID='" + code + "'";
                stmt = con.createStatement();
                stmt.executeUpdate(update);
                return;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }
        throw new ProductNotFoundException(code + "");
    }

    @Override
    public List<Discount> getProductsDiscount(Product p) throws SQLException {
        String query = "SELECT * FROM DISCOUNTS, PRODUCTS WHERE PRODUCTS.ID = " + p.getProductCode() + " AND PRODUCTS.DISCOUNT_ID = DISCOUNTS.ID";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Discount> discounts;
        try {
            ResultSet res = stmt.executeQuery(query);
            discounts = getDiscountsFromResultSet(res);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
            discountSem.release();
        }
        return discounts;
    }

    @Override
    public int getProductCount() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        Statement stmt = con.createStatement();
        try {
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            productSem.release();
        }

        List<Product> products = getProductsFromResultSet(res);

        return products.size();
    }

    //Customer Methods
    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Customer> customers;
        try {
            ResultSet set = stmt.executeQuery(query);
            customers = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                String phone = set.getString("PHONE");
                String mobile = set.getString("MOBILE");
                String email = set.getString("EMAIL");
                String address1 = set.getString("ADDRESS_LINE_1");
                String address2 = set.getString("ADDRESS_LINE_2");
                String town = set.getString("TOWN");
                String county = set.getString("COUNTY");
                String country = set.getString("COUNTRY");
                String postcode = set.getString("POSTCODE");
                String notes = set.getString("NOTES");
                int discount = set.getInt("DISCOUNT_ID");
                int loyaltyPoints = set.getInt("LOYALTY_POINTS");

                Customer c = new Customer(name, phone, mobile, email, discount, address1, address2, town, county, country, postcode, notes, loyaltyPoints, id);

                customers.add(c);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        return customers;
    }

    public List<Customer> getCustomersFromResultSet(ResultSet set) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            String phone = set.getString("PHONE");
            String mobile = set.getString("MOBILE");
            String email = set.getString("EMAIL");
            String address1 = set.getString("ADDRESS_LINE_1");
            String address2 = set.getString("ADDRESS_LINE_2");
            String town = set.getString("TOWN");
            String county = set.getString("COUNTY");
            String country = set.getString("COUNTRY");
            String postcode = set.getString("POSTCODE");
            String notes = set.getString("NOTES");
            int discount = set.getInt("DISCOUNT_ID");
            int loyaltyPoints = set.getInt("LOYALTY_POINTS");

            Customer c = new Customer(name, phone, mobile, email, discount, address1, address2, town, county, country, postcode, notes, loyaltyPoints, id);

            customers.add(c);
        }

        return customers;
    }

    /**
     * Method to add a new product to the database.
     *
     * @param c the new customer to add.
     * @throws SQLException if there was an error adding the customer to the
     * database.
     */
    @Override
    public void addCustomer(Customer c) throws SQLException {
        String query = "INSERT INTO CUSTOMERS (NAME, PHONE, MOBILE, EMAIL, ADDRESS_LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, DISCOUNT_ID, LOYALTY_POINTS) VALUES (" + c.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
    }

    @Override
    public Customer updateCustomer(Customer c) throws SQLException, CustomerNotFoundException {
        String query = c.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
        if (value == 0) {
            throw new CustomerNotFoundException(c.getId() + "");
        }
        return c;
    }

    @Override
    public void removeCustomer(Customer c) throws SQLException, CustomerNotFoundException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + c.getId();
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
        if (value == 0) {
            throw new CustomerNotFoundException(c.getId() + "");
        }
    }

    @Override
    public void removeCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }
        if (value == 0) {
            throw new CustomerNotFoundException(id + "");
        }
    }

    @Override
    public Customer getCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "SELECT * FROM CUSTOMERS WHERE CUSTOMERS.ID = '" + id + "'";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Customer> customers;
        try {
            ResultSet res = stmt.executeQuery(query);

            customers = getCustomersFromResultSet(res);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("Customer " + id + " could not be found");
        }
        return customers.get(0);
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws SQLException, CustomerNotFoundException {
        String query = "SELECT * FROM CUSTOMERS WHERE CUSTOMERS.NAME = " + name;
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Customer> customers;
        try {
            ResultSet res = stmt.executeQuery(query);

            customers = getCustomersFromResultSet(res);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("Customer " + name + " could not be found");
        }
        return customers;
    }

    @Override
    public int getCustomerCount() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        try {
            customerSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            customerSem.release();
        }

        List<Customer> customers = getCustomersFromResultSet(res);

        return customers.size();
    }

    //Staff Methods
    @Override
    public List<Staff> getAllStaff() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Staff> staff;
        try {
            ResultSet set = stmt.executeQuery(query);
            staff = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                String position = set.getString("POSITION");
                String uname = set.getString("USERNAME");
                String pword = set.getString("PASSWORD");

                Position enumPosition;

                if (position.equals(Position.ASSISSTANT.toString())) {
                    enumPosition = Position.ASSISSTANT;
                } else if (position.equals(Position.SUPERVISOR.toString())) {
                    enumPosition = Position.SUPERVISOR;
                } else if (position.equals(Position.MANAGER.toString())) {
                    enumPosition = Position.MANAGER;
                } else {
                    enumPosition = Position.AREA_MANAGER;
                }

                Staff s = new Staff(name, enumPosition, uname, pword, id);

                staff.add(s);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        return staff;
    }

    public List<Staff> getStaffFromResultSet(ResultSet set) throws SQLException {
        List<Staff> staff = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            String position = set.getString("POSITION");
            String uname = set.getString("USERNAME");
            String pword = set.getString("PASSWORD");

            Position enumPosition;

            if (position.equals(Position.ASSISSTANT.toString())) {
                enumPosition = Position.ASSISSTANT;
            } else if (position.equals(Position.SUPERVISOR.toString())) {
                enumPosition = Position.SUPERVISOR;
            } else if (position.equals(Position.MANAGER.toString())) {
                enumPosition = Position.MANAGER;
            } else {
                enumPosition = Position.AREA_MANAGER;
            }

            Staff s = new Staff(name, enumPosition, uname, pword, id);

            staff.add(s);
        }

        return staff;
    }

    @Override
    public void addStaff(Staff s) throws SQLException {
        String query = "INSERT INTO STAFF (NAME, POSITION, USERNAME, PASSWORD) VALUES (" + s.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
    }

    @Override
    public Staff updateStaff(Staff s) throws SQLException, StaffNotFoundException {
        String query = s.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
        if (value == 0) {
            throw new StaffNotFoundException(s.getId() + "");
        }
        return s;
    }

    @Override
    public void removeStaff(Staff s) throws SQLException, StaffNotFoundException {
        String query = "DELETE FROM STAFF WHERE STAFF.ID = " + s.getId();
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
        if (value == 0) {
            throw new StaffNotFoundException(s.getId() + "");
        }
    }

    @Override
    public void removeStaff(int id) throws SQLException, StaffNotFoundException {
        String query = "DELETE FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }
        if (value == 0) {
            throw new StaffNotFoundException(id + "");
        }
    }

    @Override
    public Staff getStaff(int id) throws SQLException, StaffNotFoundException {
        String query = "SELECT * FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Staff> staff;
        try {
            ResultSet set = stmt.executeQuery(query);

            staff = getStaffFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        if (staff.isEmpty()) {
            throw new StaffNotFoundException(id + "");
        }

        return staff.get(0);
    }

    @Override
    public Staff login(String username, String password) throws SQLException, LoginException {
        String query = "SELECT * FROM STAFF WHERE STAFF.USERNAME = '" + username.toLowerCase() + "'";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        List<Staff> staff = getStaffFromResultSet(res);

        if (staff.isEmpty()) {
            throw new LoginException(username + " could not be found");
        }

        Staff s = staff.get(0);

        if (s.getPassword().equals(password)) {
            return s;
        }

        throw new LoginException("Incorrect Password");
    }

    @Override
    public int staffCount() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        List<Staff> staff = getStaffFromResultSet(res);

        return staff.size();
    }

    //Discount Methods
    @Override
    public List<Discount> getAllDiscounts() throws SQLException {
        String query = "SELECT * FROM DISCOUNTS";
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Discount> discounts;
        try {
            ResultSet set = stmt.executeQuery(query);
            discounts = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                double percentage = set.getDouble("PERCENTAGE");

                Discount d = new Discount(id, name, percentage);

                discounts.add(d);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }

        return discounts;
    }

    public List<Discount> getDiscountsFromResultSet(ResultSet set) throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            double percentage = set.getDouble("PERCENTAGE");

            Discount d = new Discount(id, name, percentage);

            discounts.add(d);
        }

        return discounts;
    }

    @Override
    public void addDiscount(Discount d) throws SQLException {
        String query = "INSERT INTO DISCOUNTS (NAME, PERCENTAGE) VALUES (" + d.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
    }

    @Override
    public Discount updateDiscount(Discount d) throws SQLException, DiscountNotFoundException {
        String query = d.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
        if (value == 0) {
            throw new DiscountNotFoundException(d.getId() + "");
        }
        return d;
    }

    @Override
    public void removeDiscount(Discount d) throws SQLException, DiscountNotFoundException {
        String query = "DELETE FROM DISCOUNTS WHERE DISCOUNTS.ID = " + d.getId();
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
        if (value == 0) {
            throw new DiscountNotFoundException(d.getId() + "");
        }
    }

    @Override
    public void removeDiscount(int id) throws SQLException, DiscountNotFoundException {
        String query = "DELETE FROM DISCOUNTS WHERE DISCOUNTS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }
        if (value == 0) {
            throw new DiscountNotFoundException(id + "");
        }
    }

    @Override
    public Discount getDiscount(int id) throws SQLException, DiscountNotFoundException {
        String query = "SELECT * FROM DISCOUNTS WHERE DISCOUNTS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            discountSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Discount> discounts;
        try {
            ResultSet set = stmt.executeQuery(query);

            discounts = getDiscountsFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            discountSem.release();
        }

        if (discounts.isEmpty()) {
            throw new DiscountNotFoundException(id + "");
        }

        return discounts.get(0);
    }

    //Tax Methods
    @Override
    public List<Tax> getAllTax() throws SQLException {
        String query = "SELECT * FROM TAX";
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Tax> tax;
        try {
            ResultSet set = stmt.executeQuery(query);
            tax = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                double value = set.getDouble("VALUE");
                Tax t = new Tax(id, name, value);

                tax.add(t);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }

        return tax;
    }

    public List<Tax> getTaxFromResultSet(ResultSet set) throws SQLException {
        List<Tax> tax = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            double value = set.getDouble("VALUE");
            Tax t = new Tax(id, name, value);

            tax.add(t);
        }

        return tax;
    }

    @Override
    public void addTax(Tax t) throws SQLException {
        String query = "INSERT INTO TAX (NAME, VALUE) VALUES (" + t.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
    }

    @Override
    public Tax updateTax(Tax t) throws SQLException, TaxNotFoundException {
        String query = t.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
        if (value == 0) {
            throw new TaxNotFoundException(t.getId() + "");
        }
        return t;
    }

    @Override
    public void removeTax(Tax t) throws SQLException, TaxNotFoundException {
        String query = "DELETE FROM TAX WHERE TAX.ID = " + t.getId();
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
        if (value == 0) {
            throw new TaxNotFoundException(t.getId() + "");
        }
    }

    @Override
    public void removeTax(int id) throws SQLException, TaxNotFoundException {
        String query = "DELETE FROM TAX WHERE TAX.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }
        if (value == 0) {
            throw new TaxNotFoundException(id + "");
        }
    }

    @Override
    public Tax getTax(int id) throws SQLException, TaxNotFoundException {
        String query = "SELECT * FROM TAX WHERE TAX.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            taxSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Tax> tax;
        try {
            ResultSet set = stmt.executeQuery(query);

            tax = getTaxFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            taxSem.release();
        }

        if (tax.isEmpty()) {
            throw new TaxNotFoundException(id + "");
        }

        return tax.get(0);
    }

    //Category Methods
    @Override
    public List<Category> getAllCategorys() throws SQLException {
        String query = "SELECT * FROM CATEGORYS";
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Category> categorys;
        try {
            ResultSet set = stmt.executeQuery(query);
            categorys = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                Time startSell = set.getTime("SELL_START");
                Time endSell = set.getTime("SELL_END");
                boolean timeRestrict = set.getBoolean("TIME_RESTRICT");
                int minAge = set.getInt("MINIMUM_AGE");
                Category c = new Category(id, name, startSell, endSell, timeRestrict, minAge);
                categorys.add(c);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }

        return categorys;
    }

    public List<Category> getCategorysFromResultSet(ResultSet set) throws SQLException {
        List<Category> categorys = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            Time startSell = set.getTime("SELL_START");
            Time endSell = set.getTime("SELL_END");
            boolean timeRestrict = set.getBoolean("TIME_RESTRICT");
            int minAge = set.getInt("MINIMUM_AGE");
            Category c = new Category(id, name, startSell, endSell, timeRestrict, minAge);
            categorys.add(c);
        }
        return categorys;
    }

    @Override
    public void addCategory(Category c) throws SQLException {
        String query = "INSERT INTO CATEGORYS (NAME, SELL_START, SELL_END, TIME_RESTRICT, MINIMUM_AGE) VALUES (" + c.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
    }

    @Override
    public Category updateCategory(Category c) throws SQLException, CategoryNotFoundException {
        String query = c.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
        if (value == 0) {
            throw new CategoryNotFoundException(c.getID() + "");
        }
        return c;
    }

    @Override
    public void removeCategory(Category c) throws SQLException, CategoryNotFoundException {
        String query = "DELETE FROM CATEGORYS WHERE CATEGORYS.ID = " + c.getID();
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
        if (value == 0) {
            throw new CategoryNotFoundException(c.getID() + "");
        }
    }

    @Override
    public void removeCategory(int id) throws SQLException, CategoryNotFoundException {
        String query = "DELETE FROM CATEGORYS WHERE CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }
        if (value == 0) {
            throw new CategoryNotFoundException(id + "");
        }
    }

    @Override
    public Category getCategory(int id) throws SQLException, CategoryNotFoundException {
        String query = "SELECT * FROM CATEGORYS WHERE CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Category> categorys;
        try {
            ResultSet set = stmt.executeQuery(query);

            categorys = getCategorysFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
        }

        if (categorys.isEmpty()) {
            throw new CategoryNotFoundException(id + "");
        }

        return categorys.get(0);
    }

    @Override
    public List<Product> getProductsInCategory(int id) throws SQLException {
        String query = "SELECT * FROM PRODUCTS, CATEGORYS WHERE CATEGORYS.ID = PRODUCTS.CATEGORY_ID AND CATEGORYS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            categorySem.acquire();
            productSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Product> products;
        try {
            ResultSet set = stmt.executeQuery(query);

            products = getProductsFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            categorySem.release();
            productSem.release();
        }

        return products;
    }

    private List<Sale> getAllSalesNoSem() throws SQLException {
        String query = "SELECT * FROM SALES";
        Statement stmt = con.createStatement();
        List<Sale> sales;
        try {
            ResultSet set = stmt.executeQuery(query);
            sales = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                double price = set.getDouble("PRICE");
                int customer = set.getInt("CUSTOMER");
                Time time = set.getTime("TIMESTAMP");
                Sale s = new Sale(id, price, customer, time.getTime());
                sales.add(s);
            }
        } catch (SQLException ex) {
            throw ex;
        }

        return sales;
    }

    public List<Sale> getSalesFromResultSet(ResultSet set) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            double price = set.getDouble("PRICE");
            int customer = set.getInt("CUSTOMER");
            Time time = set.getTime("TIMESTAMP");
            Sale s = new Sale(id, price, customer, time.getTime());
            sales.add(s);
        }
        return sales;
    }

    @Override
    public void addSale(Sale s) throws SQLException {
        String query = "INSERT INTO SALES (PRICE, CUSTOMER, TIMESTAMP) VALUES (" + s.getSQLInsertStatement() + ")";
        Statement stmt = con.createStatement();
        try {
            saleSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
            List<Sale> sales = getAllSalesNoSem();
            Sale lastSale = sales.get(sales.size() - 1);
            for (int p : s.getProducts()) {
                addSaleItem(lastSale, p);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            saleSem.release();
        }
    }

    @Override
    public List<Sale> getAllSales() throws SQLException {
        String query = "SELECT * FROM SALES";
        Statement stmt = con.createStatement();
        try {
            saleSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Sale> sales;
        try {
            ResultSet set = stmt.executeQuery(query);
            sales = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                double price = set.getDouble("PRICE");
                int customer = set.getInt("CUSTOMER");
                Time time = set.getTime("TIMESTAMP");
                Sale s = new Sale(id, price, customer, time.getTime());
                sales.add(s);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            saleSem.release();
        }

        return sales;
    }

    private void addSaleItem(Sale s, int p) throws SQLException {
        String secondQuery = "INSERT INTO SALEITEMS (PRODUCT_ID, SALE_ID) VALUES (" + p + ", " + s.getCode() + ")";
        Statement sstmt = con.createStatement();
        sstmt.executeUpdate(secondQuery);
    }

    @Override
    public Sale getSale(int id) throws SQLException, SaleNotFoundException {
        String query = "SELECT * FROM APP.SALES WHERE SALES.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            saleSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Sale> sales;
        try {
            ResultSet set = stmt.executeQuery(query);
            sales = getSalesFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            saleSem.release();
        }

        if (sales.isEmpty()) {
            throw new SaleNotFoundException(id + "");
        }

        return sales.get(0);
    }

    @Override
    public List<Sale> getSalesInRange(Date start, Date end) throws SQLException, IllegalArgumentException {
        if (start.after(end)) {
            throw new IllegalArgumentException("Start date needs to be before end date");
        }
        List<Sale> s = getAllSales();
        List<Sale> sales = new ArrayList<>();

        s.stream().filter((sale) -> (sale.getTime() >= start.getTime() && sale.getTime() <= start.getTime())).forEachOrdered((sale) -> {
            sales.add(sale);
        });

        return sales;
    }

    //Voucher Methods
    @Override
    public List<Voucher> getAllVouchers() throws SQLException {
        String query = "SELECT * FROM VOUCHERS";
        Statement stmt = con.createStatement();
        try {
            voucherSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Voucher> vouchers;
        try {
            ResultSet set = stmt.executeQuery(query);
            vouchers = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                String type = set.getString("TYPE");
                String field1 = set.getString("FIELD1");
                String field2 = set.getString("FIELD2");
                String field3 = set.getString("FIELD3");
                String field4 = set.getString("FIELD4");
                String field5 = set.getString("FIELD5");
                String field6 = set.getString("FIELD6");
                String field7 = set.getString("FIELD7");
                String field8 = set.getString("FIELD8");
                Voucher.VoucherType voucherType = Voucher.VoucherType.valueOf(type);
                Voucher v = new Voucher(id, name, voucherType, field1, field2, field3, field4, field5, field6, field7, field8);
                vouchers.add(v);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            voucherSem.release();
        }

        return vouchers;
    }

    public List<Voucher> getVouchersFromResultSet(ResultSet set) throws SQLException {
        List<Voucher> vouchers = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            String type = set.getString("TYPE");
            String field1 = set.getString("FIELD1");
            String field2 = set.getString("FIELD2");
            String field3 = set.getString("FIELD3");
            String field4 = set.getString("FIELD4");
            String field5 = set.getString("FIELD5");
            String field6 = set.getString("FIELD6");
            String field7 = set.getString("FIELD7");
            String field8 = set.getString("FIELD8");
            Voucher.VoucherType voucherType = Voucher.VoucherType.valueOf(type);
            Voucher v = new Voucher(id, name, voucherType, field1, field2, field3, field4, field5, field6, field7, field8);
            vouchers.add(v);
        }
        return vouchers;
    }

    @Override
    public void addVoucher(Voucher v) throws SQLException {
        String query = "INSERT INTO VOUCHERS (NAME, TYPE) VALUES (" + v.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            voucherSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            voucherSem.release();
        }
    }

    @Override
    public Voucher updateVoucher(Voucher v) throws SQLException, VoucherNotFoundException {
        String query = v.getSQlUpdateString();
        Statement stmt = con.createStatement();
        try {
            voucherSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            voucherSem.release();
        }
        if (value == 0) {
            throw new VoucherNotFoundException(v.getId() + "");
        }
        return v;
    }

    @Override
    public void removeVoucher(Voucher v) throws SQLException, VoucherNotFoundException {
        String query = "DELETE FROM VOUCHERS WHERE VOUCHERS.ID = " + v.getId();
        Statement stmt = con.createStatement();
        try {
            voucherSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            voucherSem.release();
        }
        if (value == 0) {
            throw new VoucherNotFoundException(v.getId() + "");
        }
    }

    @Override
    public void removeVoucher(int id) throws SQLException, VoucherNotFoundException {
        String query = "DELETE FROM VOUCHERS WHERE VOUCHERS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            voucherSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            voucherSem.release();
        }
        if (value == 0) {
            throw new VoucherNotFoundException(id + "");
        }
    }

    @Override
    public Voucher getVoucher(int id) throws SQLException, VoucherNotFoundException {
        String query = "SELECT * FROM VOUCHERS WHERE VOUCHERS.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            voucherSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Voucher> vouchers;
        try {
            ResultSet set = stmt.executeQuery(query);

            vouchers = getVouchersFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            voucherSem.release();
        }

        if (vouchers.isEmpty()) {
            throw new VoucherNotFoundException(id + "");
        }

        return vouchers.get(0);
    }

    @Override
    public String toString() {
        if (connected) {
            return "Connected to database " + this.address + "\nOn user " + this.username;
        } else {
            return "Not connected to database";
        }
    }

    @Override
    public Staff tillLogin(int id) throws IOException, LoginException, SQLException {
        String query = "SELECT * FROM STAFF WHERE STAFF.ID = " + id;
        Statement stmt = con.createStatement();
        try {
            staffSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet res;
        try {
            res = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            staffSem.release();
        }

        List<Staff> staff = getStaffFromResultSet(res);

        if (staff.isEmpty()) {
            throw new LoginException(id + " could not be found");
        }

        Staff s = staff.get(0);

        return s;
    }

    @Override
    public void logout(int id) throws IOException, StaffNotFoundException {

    }

    @Override
    public void tillLogout(int id) throws IOException, StaffNotFoundException {

    }

    public void loadProperties() {
        properties = new Properties();
        InputStream in;

        try {
            in = new FileInputStream("server.properties");

            properties.load(in);

            hostName = properties.getProperty("host");
            PORT = Integer.parseInt(properties.getProperty("port", Integer.toString(PORT)));
            MAX_CONNECTIONS = Integer.parseInt(properties.getProperty("max_conn", Integer.toString(MAX_CONNECTIONS)));
            MAX_QUEUE = Integer.parseInt(properties.getProperty("max_queue", Integer.toString(MAX_QUEUE)));
            TillInitData.initData.autoLogout = Boolean.parseBoolean(properties.getProperty("autoLogout", "false"));
            TillInitData.initData.logoutTimeout = Integer.parseInt(properties.getProperty("logoutTimeout", "30"));
            TillInitData.initData.logonScreenMessage = properties.getProperty("logonMessage");
            DB_ADDRESS = properties.getProperty("db_address", "jdbc:derby:TillEmbedded;");
            DB_USERNAME = properties.getProperty("db_username", "APP");
            DB_PASSWORD = properties.getProperty("db_password", "App");

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            saveProperties();
        } catch (IOException ex) {
        }
    }

    public void saveProperties() {
        properties = new Properties();
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            hostName = InetAddress.getLocalHost().getHostName();

            properties.setProperty("host", hostName);
            properties.setProperty("port", Integer.toString(PORT));
            properties.setProperty("max_conn", Integer.toString(MAX_CONNECTIONS));
            properties.setProperty("max_queue", Integer.toString(MAX_QUEUE));
            properties.setProperty("autoLogout", Boolean.toString(TillInitData.initData.autoLogout));
            properties.setProperty("logoutTimeout", Integer.toString(TillInitData.initData.logoutTimeout));
            properties.setProperty("logonMessage", TillInitData.initData.logonScreenMessage);
            properties.setProperty("db_address", DB_ADDRESS);
            properties.setProperty("db_username", DB_USERNAME);
            properties.setProperty("db_password", DB_PASSWORD);

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

    private List<Screen> getScreensFromResultSet(ResultSet set) throws SQLException {
        List<Screen> screens = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            int order = set.getInt("POSITION");
            int color = set.getInt("COLOR");
            Screen s = new Screen(name, order, color, id);

            screens.add(s);
        }

        return screens;
    }

    private List<Button> getButtonsFromResultSet(ResultSet set) throws SQLException {
        List<Button> buttons = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            int order = set.getInt("POSITION");
            int product = set.getInt("PRODUCT");
            int screen = set.getInt("SCREEN_ID");
            int color = set.getInt("COLOR");
            Button b = new Button(name, product, order, screen, color, id);

            buttons.add(b);
        }

        return buttons;
    }

    @Override
    public void addScreen(Screen s) throws SQLException {
        String query = "INSERT INTO SCREENS (NAME, POSITION, COLOR) VALUES (" + s.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }
    }

    @Override
    public void addButton(Button b) throws SQLException {
        String query = "INSERT INTO BUTTONS (NAME, POSITION, PRODUCT, COLOR, SCREEN_ID) VALUES (" + b.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }
    }

    @Override
    public void removeScreen(Screen s) throws SQLException, ScreenNotFoundException {
        String query = "DELETE FROM SCREENS WHERE SCREENS.ID = " + s.getId();
        String buttonsQuery = "DELETE FROM BUTTONS WHERE BUTTONS.SCREEN_ID = " + s.getId();
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
            stmt.executeUpdate(buttonsQuery);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }
        if (value == 0) {
            throw new ScreenNotFoundException("Screen " + s + " could not be found");
        }
    }

    @Override
    public void removeButton(Button b) throws SQLException, ButtonNotFoundException {
        String query = "DELETE FROM BUTTONS WHERE BUTTONS.ID = " + b.getId();
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }
        if (value == 0) {
            throw new ButtonNotFoundException("Button " + b + " could not be found");
        }
    }

    @Override
    public Screen getScreen(int s) throws SQLException, ScreenNotFoundException {
        String query = "SELECT * FROM SCREENS WHERE SCREENS.ID = " + s;
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Screen> screens;
        try {
            ResultSet set = stmt.executeQuery(query);

            screens = getScreensFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }

        if (screens.isEmpty()) {
            throw new ScreenNotFoundException("Screen " + s + " could not be found");
        }

        return screens.get(0);
    }

    @Override
    public Button getButton(int b) throws SQLException, ButtonNotFoundException {
        String query = "SELECT * FROM SCREENS WHERE BUTTONS.ID = " + b;
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Button> buttons;
        try {
            ResultSet set = stmt.executeQuery(query);

            buttons = getButtonsFromResultSet(set);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }

        if (buttons.isEmpty()) {
            throw new ButtonNotFoundException("Button " + b + " could not be found");
        }

        return buttons.get(0);
    }

    @Override
    public Screen updateScreen(Screen s) throws SQLException, ScreenNotFoundException {
        String query = s.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }
        if (value == 0) {
            throw new ScreenNotFoundException("Screen " + s + " could not be found");
        }
        return s;
    }

    @Override
    public Button updateButton(Button b) throws SQLException, ButtonNotFoundException {
        String query = b.getSQLUpdateString();
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        int value;
        try {
            value = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }
        if (value == 0) {
            throw new ButtonNotFoundException("Button " + b + " could not be found");
        }
        return b;
    }

    @Override
    public List<Screen> getAllScreens() throws SQLException {
        String query = "SELECT * FROM SCREENS";
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Screen> screens;
        try {
            ResultSet set = stmt.executeQuery(query);
            screens = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                int order = set.getInt("POSITION");
                int color = set.getInt("COLOR");
                Screen s = new Screen(name, order, color, id);

                screens.add(s);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }

        return screens;
    }

    @Override
    public List<Button> getAllButtons() throws SQLException {
        String query = "SELECT * FROM BUTTONS";
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Button> buttons;
        try {
            ResultSet set = stmt.executeQuery(query);
            buttons = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                int order = set.getInt("POSITION");
                int product = set.getInt("PRODUCT");
                int color = set.getInt("COLOR");
                int screen = set.getInt("SCREEN_ID");
                Button b = new Button(name, product, order, screen, color, id);

                buttons.add(b);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }

        return buttons;
    }

    @Override
    public List<Button> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        String query = "SELECT * FROM BUTTONS WHERE BUTTONS.SCREEN_ID=" + s.getId();
        Statement stmt = con.createStatement();
        try {
            screensSem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Button> buttons;
        try {
            ResultSet set = stmt.executeQuery(query);
            buttons = new ArrayList<>();
            while (set.next()) {
                int id = set.getInt("ID");
                String name = set.getString("NAME");
                int order = set.getInt("POSITION");
                int product = set.getInt("PRODUCT");
                int color = set.getInt("COLOR");
                int screen = set.getInt("SCREEN_ID");
                Button b = new Button(name, product, order, screen, color, id);

                buttons.add(b);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            screensSem.release();
        }

        return buttons;
    }

    @Override
    public void deleteAllScreensAndButtons() throws IOException, SQLException {
        String buttons = "DROP TABLE BUTTONS";
        String screens = "DROP TABLE SCREENS";
        Statement stmt = con.createStatement();
        stmt.execute(buttons);
        stmt.execute(screens);
        createTables();
    }

    @Override
    public void setGUI(GUIInterface g) {
        this.g = g;
    }
}