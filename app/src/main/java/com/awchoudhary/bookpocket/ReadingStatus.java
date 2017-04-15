package com.awchoudhary.bookpocket;

/**
 * Created by awaeschoudhary on 4/7/17.
 * Class that contains constants used throughout the project
 */

public enum ReadingStatus {
    WANT_TO_READ("Want to read"),
    READING("Currently reading"),
    COMPLETED("Completed book"),
    NO_STATUS("No status available");

    private final String text;

    private ReadingStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}


