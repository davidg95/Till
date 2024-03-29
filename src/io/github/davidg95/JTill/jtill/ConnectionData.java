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
public class ConnectionData implements Serializable, Cloneable {

    private final String flag;
    private final Object data;
    private final Object data2;

    public ConnectionData(String flag, Object data) {
        this(flag, data, null);
    }

    public ConnectionData(String flag) {
        this(flag, null);
    }

    public ConnectionData(String flag, Object data1, Object data2) {
        this.flag = flag;
        this.data = data1;
        this.data2 = data2;
    }

    public static ConnectionData create(String flag) {
        return new ConnectionData(flag);
    }

    public static ConnectionData create(String flag, Object data) {
        return new ConnectionData(flag, data);
    }

    public static ConnectionData create(String flag, Object data1, Object data2) {
        return new ConnectionData(flag, data1, data2);
    }

    public String getFlag() {
        return flag;
    }

    public Object getData() {
        return data;
    }

    public Object getData2() {
        return data2;
    }

    @Override
    public ConnectionData clone() {
        try {
            final ConnectionData result = (ConnectionData) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Flag- " + this.flag
                + "\n Data- " + this.data
                + "\n Data 2- " + this.data2;
    }
}
