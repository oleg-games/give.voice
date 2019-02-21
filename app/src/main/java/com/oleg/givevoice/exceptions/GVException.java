package com.oleg.givevoice.exceptions;

public class GVException extends Exception {
    private String title = "GVException";

    GVException() {
    }

    public GVException(String msg) {
        super(msg);
    }

    public GVException(String msg, String title) {
        super(msg);
        this.title = "GVException: " + title;
    }

    public String getTitle() {
        return title;
    }
}
