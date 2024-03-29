/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;

/**
 *
 * @author David
 */
public class Tax implements Serializable, JTillObject {

    private int id;
    private String name;
    private double value;

    public Tax(int id, String name, double value) {
        this(name, value);
        this.id = id;
    }

    public Tax(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "'," + this.value;
    }

    public String getSQLUpdateString() {
        return "UPDATE TAX"
                + " SET NAME='" + this.getName()
                + "', VALUE=" + this.getValue()
                + " WHERE TAX.ID=" + this.getId();
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }

}
