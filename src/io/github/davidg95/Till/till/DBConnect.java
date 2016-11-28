/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import io.github.davidg95.Till.till.Staff.Position;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * Database connection class which handles communication with the database.
 *
 * @author David
 */
public class DBConnect {

    private Connection con;
    private Driver embedded;

    private String address;
    private String username;
    private String password;

    private boolean connected;

    private ResultSet productSet;
    private ResultSet customerSet;
    private ResultSet staffSet;
    private ResultSet discountSet;
    private ResultSet configSet;
    private ResultSet taxSet;
    private ResultSet categorySet;

    private final String all_products = "SELECT * FROM PRODUCTS";
    private final String all_customers = "SELECT * FROM CUSTOMERS";
    private final String all_staff = "SELECT * FROM STAFF";
    private final String all_discounts = "SELECT * FROM DISCOUNTS";
    private final String all_configs = "SELECT * FROM CONFIGS";
    private final String all_tax = "SELECT * FROM TAX";
    private final String all_categorys = "SELECT * FROM CATEGORYS";

    private Statement products_stmt;
    private Statement customers_stmt;
    private Statement staff_stmt;
    private Statement discounts_stmt;
    private Statement configs_stmt;
    private Statement tax_stmt;
    private Statement category_stmt;

    private static int productCounter;
    private static int customerCounter;
    public static int staffCounter;
    private static int discountCounter;
    private static int taxCounter;
    private static int categoryCounter;

    private Statement create_tables_stmt;

    public DBConnect() {

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
        configs_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        configSet = configs_stmt.executeQuery(all_configs);
        openConfigs();
        staff_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        staffSet = staff_stmt.executeQuery(all_staff);
    }

    public void create(String username, String password) throws SQLException {
        embedded = new EmbeddedDriver();
        DriverManager.registerDriver(embedded);
        con = DriverManager.getConnection("jdbc:derby:TillEmbedded;create=true", "App", "App");

        this.address = "jdbc:derby:TillEmbedded;create=true";
        this.username = "App";
        this.password = "App";
        connected = true;
        createTables();
    }

    private void createTables() throws SQLException {
        String categorys = "create table APP.CATEGORYS\n"
                + "(\n"
                + "	ID INT not null primary key\n"
                + "        GENERATED ALWAYS AS IDENTITY\n"
                + "        (START WITH 1, INCREMENT BY 1),\n"
                + "	NAME VARCHAR(20) not null\n"
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

        Statement stmt = con.createStatement();
        stmt.execute(tax);
        stmt.execute(categorys);
        stmt.execute(discounts);
        stmt.execute(configs);
        stmt.execute(customers);
        stmt.execute(products);
        stmt.execute(staff);

        String addCategory = "INSERT INTO CATEGORYS VALUES ('Default')";
        String addTax = "INSERT INTO TAX VALUES ('ZERO',0.0)";
        String addDiscount = "INSERT INTO DISCOUNTS VALUES ('NONE',0.0)";
        stmt.executeUpdate(addCategory);
        stmt.executeUpdate(addTax);
        stmt.executeUpdate(addDiscount);
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
     * Method to initialise the database connection. This method will set up the
     * SQL statements and load the data sets.
     *
     * @throws SQLException if there was an SQL error.
     */
    public void initDatabase() throws SQLException {

    }

    /**
     * Method to close the database connection. This will close the data sets
     * and close the connection.
     */
    public void close() {
        try {
            productSet.close();
            customerSet.close();
            staffSet.close();
            discountSet.close();
            configSet.close();
            taxSet.close();
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
    public boolean isConnected() {
        return connected;
    }

    public List<Product> getAllProducts() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery(query);
        List<Product> products = new ArrayList<>();
        while (set.next()) {
            int code = set.getInt("ID");
            String barcode = set.getString("BARCODE");
            String name = set.getString("NAME");
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

            Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, price, costPrice, stock, minStock, maxStock, barcode, code);

            products.add(p);
        }

        return products;
    }

    private List<Product> getProductsFromResultSet(ResultSet set) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (set.next()) {
            int code = set.getInt("ID");
            String barcode = set.getString("BARCODE");
            String name = set.getString("NAME");
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

            Product p = new Product(name, shortName, categoryID, comments, taxID, discountID, price, costPrice, stock, minStock, maxStock, barcode, code);

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
    public void addProduct(Product p) throws SQLException {
        String query = "INSERT INTO PRODUCTS (BARCODE, NAME, PRICE, STOCK, COMMENTS, SHORT_NAME, CATEGORY_ID, TAX_ID, COST_PRICE, MIN_PRODUCT_LEVEL, MAX_PRODUCT_LEVEL, DISCOUNT_ID) VALUES (" + p.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
        saveConfigs();
    }

    /**
     * Method to check if a barcode already exists in the database.
     *
     * @param barcode the barcode to check.
     * @return true or false indicating whether the barcode already exists.
     * @throws SQLException if there was an error checking the barcode.
     */
    public boolean checkBarcode(String barcode) throws SQLException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.BARCODE = '" + barcode + "'";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        List<Product> lp = getProductsFromResultSet(res);

        stmt.close();
        res.close();

        return !lp.isEmpty();
    }

    /**
     * Method to remove a product from the database.
     *
     * @param p the product to remove.
     * @throws SQLException if there was an error removing the product.
     */
    public void removeProduct(Product p) throws SQLException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + p.getProductCode();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }

    /**
     * Method to remove a product from the database.
     *
     * @param id the product to remove.
     * @throws SQLException if there was an error removing the product.
     */
    public void removeProduct(int id) throws SQLException {
        String query = "DELETE FROM PRODUCTS WHERE PRODUCTS.ID = " + id + "";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
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
    public int purchaseProduct(int code) throws SQLException, OutOfStockException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID=" + code;
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);
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
    public Product getProduct(String code) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM Products WHERE PRODUCTS.ID = '" + code + "'";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        List<Product> products = getProductsFromResultSet(res);
        if (products.isEmpty()) {
            throw new ProductNotFoundException(code);
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
    public Product getProductByBarcode(String barcode) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM Products WHERE PRODUCTS.BARCODE = '" + barcode + "'";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        List<Product> products = getProductsFromResultSet(res);
        if (products.isEmpty()) {
            throw new ProductNotFoundException(barcode);
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
    public void setStock(String code, int stock) throws SQLException, ProductNotFoundException {
        String query = "SELECT * FROM PRODUCTS WHERE PRODUCTS.ID='" + code + "'";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            res.close();
            String update = "UPDATE PRODUCTS SET STOCK=" + stock + " WHERE PRODUCTS.ID='" + code + "'";
            stmt = con.createStatement();
            stmt.executeUpdate(update);
            return;
        }
        throw new ProductNotFoundException(code);
    }

    public void getProductsDiscount(Product p) throws SQLException {
        String query = "SELECT * FROM DISCOUNTS, PRODUCTS WHERE PRODUCTS.ID = '" + p.getProductCode() + "' AND PRODUCTS.DISCOUNT_ID = DISCOUNTS.ID";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        while (res.next()) {
            System.out.println(res.getString(1) + "\n" + res.getString(2));
        }
    }

    public int getProductCount() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        List<Product> products = getProductsFromResultSet(res);

        return products.size();
    }

//    public String generateProductCode() {
//        String no;
//        String zeros;
//        no = Integer.toString(productCounter);
//        zeros = "";
//        for (int i = no.length(); i < 5; i++) {
//            zeros += "0";
//        }
//        productCounter++;
//
//        return "P" + zeros + no;
//    }

    public List<Customer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery(query);
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
            String discount = set.getString("DISCOUNT_ID");
            int loyaltyPoints = set.getInt("LOYALTY_POINTS");

            Customer c = new Customer(name, phone, mobile, email, discount, address1, address2, town, county, country, postcode, notes, loyaltyPoints, id);

            customers.add(c);
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
            String discount = set.getString("DISCOUNT_ID");
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
    public void addCustomer(Customer c) throws SQLException {
        String query = "INSERT INTO CUSTOMERS (NAME, PHONE, MOBILE, EMAIL, ADDRESS_LINE_1, ADDRESS_LINE_2, TOWN, COUNTY, COUNTRY, POSTCODE, NOTES, DISCOUNT_ID, LOYALTY_POINTS) VALUES (" + c.getSQLInsertString() + ")";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }

    public void removeCustomer(Customer c) throws SQLException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + c.getId();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    public void removeCustomer(int id) throws SQLException {
        String query = "DELETE FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    public Customer getCustomer(int id) throws SQLException, CustomerNotFoundException {
        String query = "SELECT * FROM CUSTOMERS WHERE CUSTOMERS.ID = " + id;
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        List<Customer> customers = getCustomersFromResultSet(res);

        if (customers.isEmpty()) {
            throw new CustomerNotFoundException(id + "");
        }
        return customers.get(0);
    }

    public int getCustomerCount() throws SQLException {
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(query);

        List<Customer> customers = getCustomersFromResultSet(res);

        return customers.size();
    }

    public String generateCustomerCode() {
        String no;
        String zeros;
        no = Integer.toString(customerCounter);
        zeros = "";
        for (int i = no.length(); i < 5; i++) {
            zeros += "0";
        }
        customerCounter++;

        return "C" + zeros + no;
    }

    public List<Staff> getAllStaff() throws SQLException {
        String query = "SELECT * FROM STAFF";
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery(query);
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

    public List<Discount> getAllDiscounts() throws SQLException {
        String query = "SELECT * FROM DISCOUNTS";
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery(query);
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

    public HashMap<String, String> getAllConfigs() throws SQLException {
        HashMap<String, String> configs = new HashMap<>();

        while (configSet.next()) {
            String name = configSet.getString("NAME");
            String value = configSet.getString("VALUE");
            configs.put(name, value);
        }

        if (configs.isEmpty()) {
            configs.put("products", 0 + "");
            configs.put("customers", 0 + "");
            configs.put("staff", 0 + "");
            configs.put("discounts", 0 + "");
            configs.put("tax", 0 + "");
            configs.put("categorys", 0 + "");
        }

        return configs;
    }

    public List<Tax> getAllTax() throws SQLException {
        String query = "SELECT * FROM TAX";
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery(query);
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

    public List<Category> getAllCategorys() throws SQLException {
        String query = "SELECT * FROM CATEGORYS";
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery(query);
        List<Category> categorys = new ArrayList<>();
        while (set.next()) {
            int id = set.getInt("ID");
            String name = set.getString("NAME");
            Category c = new Category(id, name);
            categorys.add(c);
        }

        return categorys;
    }

//    public void updateWholeProducts(List<Product> products) throws SQLException {
//        productSet.beforeFirst();
//
//        while (productSet.next()) {
//            productSet.deleteRow();
//        }
//
//        for (Product p : products) {
//            productSet.moveToInsertRow();
//            productSet.updateString("ID", p.getProductCode());
//            productSet.updateString("BARCODE", p.getBarcode());
//            productSet.updateString("NAME", p.getName());
//            productSet.updateDouble("PRICE", p.getPrice());
//            productSet.updateInt("STOCK", p.getStock());
//            productSet.updateString("COMMENTS", p.getComments());
//            productSet.updateString("SHORT_NAME", p.getShortName());
//            productSet.updateString("CATEGORY_ID", p.getCategoryID());
//            productSet.updateString("TAX_ID", p.getTaxID());
//            productSet.updateDouble("COST_PRICE", p.getCostPrice());
//            productSet.updateInt("MIN_PRODUCT_LEVEL", p.getMinStockLevel());
//            productSet.updateInt("MAX_PRODUCT_LEVEL", p.getMaxStockLevel());
//            productSet.insertRow();
//        }
//
//        products_stmt.close();
//        productSet.close();
//
//        products_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//
//        productSet = products_stmt.executeQuery(all_products);
//    }

//    public void updateWholeCustomers(List<Customer> customers) throws SQLException {
//        customerSet.beforeFirst();
//
//        while (customerSet.next()) {
//            customerSet.deleteRow();
//        }
//
//        for (Customer c : customers) {
//            customerSet.moveToInsertRow();
//            customerSet.updateString("ID", c.getId());
//            customerSet.updateString("NAME", c.getName());
//            customerSet.updateString("PHONE", c.getPhone());
//            customerSet.updateString("MOBILE", c.getMobile());
//            customerSet.updateString("EMAIL", c.getEmail());
//            customerSet.updateString("ADDRESS_LINE_1", c.getAddressLine1());
//            customerSet.updateString("ADDRESS_LINE_2", c.getAddressLine2());
//            customerSet.updateString("TOWN", c.getTown());
//            customerSet.updateString("COUNTY", c.getCounty());
//            customerSet.updateString("COUNTRY", c.getCountry());
//            customerSet.updateString("POSTCODE", c.getPostcode());
//            customerSet.updateString("NOTES", c.getNotes());
//            customerSet.updateString("DISCOUNT_ID", c.getDiscountID());
//            customerSet.updateInt("LOYALTY_POINTS", c.getLoyaltyPoints());
//            customerSet.insertRow();
//        }
//
//        customers_stmt.close();
//        customerSet.close();
//
//        customers_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//
//        customerSet = customers_stmt.executeQuery(all_customers);
//    }

    public void updateWholeStaff(List<Staff> staff) throws SQLException {
        staffSet.beforeFirst();

        while (staffSet.next()) {
            staffSet.deleteRow();
        }

        for (Staff s : staff) {
            staffSet.moveToInsertRow();
            staffSet.updateInt(1, s.getId());
            staffSet.updateString(2, s.getName());
            staffSet.updateString(3, s.getPosition().toString());
            staffSet.updateString(4, s.getUsername());
            staffSet.updateString(5, s.getPassword());
            staffSet.insertRow();
        }

        staff_stmt.close();
        staffSet.close();

        staff_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        staffSet = staff_stmt.executeQuery(all_staff);
    }

    public void updateWholeDiscounts(List<Discount> discounts) throws SQLException {
        discountSet.beforeFirst();

        while (discountSet.next()) {
            discountSet.deleteRow();
        }

        for (Discount d : discounts) {
            discountSet.moveToInsertRow();
            discountSet.updateInt(1, d.getId());
            discountSet.updateString(2, d.getName());
            discountSet.updateDouble(3, d.getPercentage());
            discountSet.insertRow();
        }

        discounts_stmt.close();
        discountSet.close();

        discounts_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        discountSet = discounts_stmt.executeQuery(all_discounts);
    }

    public void updateWholeConfigs(HashMap<String, String> configs) throws SQLException {
        configSet.beforeFirst();

        while (configSet.next()) {
            configSet.deleteRow();
        }

        for (Map.Entry pair : configs.entrySet()) {
            configSet.moveToInsertRow();
            configSet.updateString("NAME", "" + pair.getKey());
            configSet.updateString("VALUE", "" + pair.getValue());
            configSet.insertRow();
        }

        configs_stmt.close();
        configSet.close();

        configs_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        configSet = configs_stmt.executeQuery(all_configs);
    }

    public void updateWholeTax(List<Tax> tax) throws SQLException {
        taxSet.beforeFirst();

        while (taxSet.next()) {
            taxSet.deleteRow();
        }

        for (Tax t : tax) {
            taxSet.moveToInsertRow();
            taxSet.updateInt("ID", t.getId());
            taxSet.updateString("NAME", t.getName());
            taxSet.updateDouble("VALUE", t.getValue());
            taxSet.insertRow();
        }

        tax_stmt.close();
        taxSet.close();

        tax_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        taxSet = tax_stmt.executeQuery(all_tax);
    }

    public void updateWholeCategorys(List<Category> categorys) throws SQLException {
        categorySet.beforeFirst();

        while (categorySet.next()) {
            categorySet.deleteRow();
        }

        for (Category c : categorys) {
            categorySet.moveToInsertRow();
            categorySet.updateInt("ID", c.getID());
            categorySet.updateString("NAME", c.getName());
            categorySet.insertRow();
        }

        category_stmt.close();
        categorySet.close();

        category_stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        categorySet = category_stmt.executeQuery(all_categorys);
    }

    /**
     * Method to save the configs.
     */
    public void saveConfigs() {
        HashMap<String, String> configs = new HashMap<>();

        configs.put("products", "" + productCounter);
        configs.put("customers", "" + customerCounter);
        configs.put("staff", "" + staffCounter);
        configs.put("discounts", "" + discountCounter);
        configs.put("tax", "" + taxCounter);
        configs.put("categorys", "" + categoryCounter);

        try {
            updateWholeConfigs(configs);
        } catch (SQLException ex) {
        }
    }

    /**
     * Method to load the configs.
     */
    public final void openConfigs() {
        try {
            HashMap<String, String> configs = getAllConfigs();

            productCounter = Integer.parseInt(configs.get("products"));
            customerCounter = Integer.parseInt(configs.get("customers"));
            staffCounter = Integer.parseInt(configs.get("staff"));
            discountCounter = Integer.parseInt(configs.get("discounts"));
            taxCounter = Integer.parseInt(configs.get("tax"));
            categoryCounter = Integer.parseInt(configs.get("categorys"));
        } catch (SQLException ex) {
        }
    }

    @Override
    public String toString() {
        return "Connected to database " + this.address + "\nOn user " + this.username;
    }
}
