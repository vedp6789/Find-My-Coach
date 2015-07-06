package com.findmycoach.app.beans.mentor;

/**
 * Created by abhi7 on 06/07/15.
 */
public class Currency {
    private String message;

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String currencySymbol;

}
