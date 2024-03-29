/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author David
 */
public class Till implements Serializable, Cloneable, JTillObject {

    private int id;
    private UUID uuid;
    private final String name;
    private BigDecimal uncashedTakings;
    private boolean connected;
    private Date lastContact;

    public Till(String name) {
        this.name = name;
        this.uncashedTakings = new BigDecimal("0");
        uncashedTakings = uncashedTakings.setScale(2);
        this.uuid = UUID.randomUUID();
    }

    public Till(String name, BigDecimal uncashedTakings, int id, UUID uuid) {
        this(name);
        this.uncashedTakings = uncashedTakings;
        this.id = id;
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getUncashedTakings() {
        return uncashedTakings;
    }

    public void setUncashedTakings(BigDecimal uncashedTakings) {
        this.uncashedTakings = uncashedTakings;
    }

    public void addTakings(BigDecimal val) {
        this.uncashedTakings = uncashedTakings.add(val);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Date getLastContact() {
        return lastContact;
    }

    public void setLastContact(Date lastContact) {
        this.lastContact = lastContact;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.uuid.toString()
                + "'," + this.uncashedTakings;
    }

    public String getSQLUpdateString() {
        return "UPDATE TILLS"
                + " SET NAME='" + this.name
                + "' UUID='" + this.uuid.toString()
                + "' UNCASHED=" + this.uncashedTakings
                + " WHERE TILLS.ID=" + this.id;
    }

    @Override
    public Till clone() {
        try {
            final Till result = (Till) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Till) {
            if (this.getId() == ((Till) o).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name + " - " + (this.connected ? "Connected" : "Not Connected");
    }
}
