/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class of type product which implements Serializable. This class models a
 * product and will be used in the till system.
 *
 * @author 1301480
 */
public class Product implements Serializable, Cloneable, Item, JTillObject {

    private int productCode;
    private int order_code;
    private String name;
    private String shortName;
    private int category;
    private int department;
    private int tax;
    private boolean open;
    private BigDecimal price;
    private BigDecimal costPrice;
    private int stock;
    private int minStockLevel;
    private int maxStockLevel;
    private String comments;

    /**
     * Constructor which takes in only a name and comments. This can be used for
     * open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param comments the comments.
     * @param tax the tax class for this product.
     * @param open if the price is open.
     */
    public Product(String name, String shortName, int order_code, int category, int department, String comments, int tax, boolean open) {
        this.name = name;
        this.shortName = shortName;
        this.order_code = order_code;
        this.category = category;
        this.department = department;
        this.comments = comments;
        this.tax = tax;
        this.open = open;
    }

    /**
     * Constructor which takes in only a name, comments and product code. This
     * can be used for open products.
     *
     * @param name the name of the product.
     * @param shortName the shortened name of the product.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param comments the comments.
     * @param tax the tax class for this product.
     * @param open if the price is open.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, int order_code, int category, int department, String comments, int tax, boolean open, int productCode) {
        this(name, shortName, order_code, category, department, comments, tax, open);
        this.productCode = productCode;
    }

    /**
     * Constructor which taken in values for all fields apart from product code.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param price the price for the product.
     * @param tax the tax class for this product.
     * @param open if the price is open.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     */
    public Product(String name, String shortName, int order_code, int category, int department, String comments, int tax, boolean open, BigDecimal price, BigDecimal costPrice, int stock, int minStock, int maxStock) {
        this(name, shortName, order_code, category, department, comments, tax, open);
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.minStockLevel = minStock;
        this.maxStockLevel = maxStock;
    }

    /**
     * Constructor which taken in values for all fields as parameters.
     *
     * @param name the name for the product.
     * @param shortName the shortened name of the product.
     * @param order_code the re order code for the product.
     * @param category the category this will belong to.
     * @param department the department the product belongs to.
     * @param price the price for the product.
     * @param tax the tax class for this product.
     * @param open if the price is open.
     * @param stock the initial stock level for the product.
     * @param costPrice the cost price of the product.
     * @param minStock the minimum stock level.
     * @param comments any comments about the product.
     * @param maxStock the maximum stock level.
     * @param productCode the product code.
     */
    public Product(String name, String shortName, int order_code, int category, int department, String comments, int tax, boolean open, BigDecimal price, BigDecimal costPrice, int stock, int minStock, int maxStock, int productCode) {
        this(name, shortName, order_code, category, department, comments, tax, open, price, costPrice, stock, minStock, maxStock);
        this.productCode = productCode;
    }

    /**
     * Method to increase the stock level of the product
     *
     * @param stock the stock to add.
     */
    public void addStock(int stock) {
        this.stock += stock;
    }

    /**
     * Method to decrease the stock level of the product
     *
     * @param stock the stock to remove.
     */
    public void removeStock(int stock) {
        this.stock -= stock;
    }

    /**
     * Method to purchase the product and reduce its stock level by 1. this
     * method throws an OutOfStockExceptin if the stock level is 0.
     *
     * @throws OutOfStockException if the stock level is 0.
     */
    public void purchace() throws OutOfStockException {
        if (this.stock > 0) {
            stock--;
        } else {
            throw new OutOfStockException(this.productCode + "");
        }
    }

    @Override
    public int getId() {
        return productCode;
    }

    @Override
    public void setId(int productCode) {
        this.productCode = productCode;
    }

    public String getLongName() {
        return name;
    }

    public void setLongName(String name) {
        this.name = name;
    }

    public int getOrder_code() {
        return order_code;
    }

    public void setOrder_code(int order_code) {
        this.order_code = order_code;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String getName() {
        return shortName;
    }

    @Override
    public void setName(String shortName) {
        this.shortName = shortName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public int getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(int maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getSQLInsertString() {
        return + this.order_code
                + ",'" + this.name
                + "'," + this.open
                + "," + this.price
                + "," + this.stock
                + ",'" + this.comments
                + "','" + this.shortName
                + "'," + this.category
                + "," + this.department
                + "," + this.tax
                + "," + this.costPrice
                + "," + this.minStockLevel
                + "," + this.maxStockLevel;
    }

    public String getSQlUpdateString() {
        return "UPDATE PRODUCTS"
                + " SET PRODUCTS.ORDER_CODE=" + this.getOrder_code()
                + ", PRODUCTS.NAME='" + this.getLongName()
                + "', PRODUCTS.OPEN_PRICE=" + this.isOpen()
                + ", PRODUCTS.PRICE=" + this.getPrice()
                + ", PRODUCTS.STOCK=" + this.getStock()
                + ", PRODUCTS.COMMENTS='" + this.getComments()
                + "', PRODUCTS.SHORT_NAME='" + this.getName()
                + "', PRODUCTS.CATEGORY_ID=" + this.getCategory()
                + ", PRODUCTS.DEPARTMENT_ID=" + this.getDepartment()
                + ", PRODUCTS.TAX_ID=" + this.getTax()
                + ", PRODUCTS.COST_PRICE=" + this.getCostPrice()
                + ", PRODUCTS.MIN_PRODUCT_LEVEL=" + this.getMinStockLevel()
                + ", PRODUCTS.MAX_PRODUCT_LEVEL=" + this.getMaxStockLevel()
                + " WHERE PRODUCTS.ID=" + this.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product) {
            return this.productCode == ((Product) o).getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.productCode;
        return hash;
    }

    @Override
    public Item clone() {
        try {
            final Product result = (Product) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.shortName;
    }
}
