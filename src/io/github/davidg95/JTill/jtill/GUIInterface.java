/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

/**
 *
 * @author David
 */
public interface GUIInterface {

    public void log(Object o);
    
    public void logWarning(Object o);

    public void setClientLabel(String text);

    public void showMessage(String title, String message);

    public boolean showYesNoMessage(String title, String message);

    public void showModalMessage(String title, String message);

    public void hideModalMessage();

    public void addTill(Till t);

    public void allow();

    public void disallow();
    
    public void updateTills();
}
