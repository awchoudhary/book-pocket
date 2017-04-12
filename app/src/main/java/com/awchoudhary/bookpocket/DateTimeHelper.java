package com.awchoudhary.bookpocket;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by awaeschoudhary on 3/26/17.
 * Helper class that perform date opperations.
 */

public class DateTimeHelper {
    //date formatter used to parse date strings
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("MM/dd/yyyy");

    //parses DateTime object to string
    public static String toString(DateTime date){
        if(date == null){
            return "";
        }
        return DATE_FORMATTER.print(date);
    }

    //parses string to DateTime object.
    public static DateTime toDateTime(String dateTimeString){
        if(dateTimeString == null || dateTimeString.equals("")){
            return null;
        }
        return DATE_FORMATTER.parseDateTime(dateTimeString);
    }

    public int getDaysSinceStarted(DateTime dateStarted){
        return Days.daysBetween(dateStarted, new DateTime()).getDays();
    }

    public int getDaysTakenToComplete(DateTime dateStarted, DateTime dateCompleted){
        return Days.daysBetween(dateStarted, dateCompleted).getDays();
    }

    public int getDaysSinceCompleted(DateTime dateCompleted){
        return Days.daysBetween(dateCompleted, new DateTime()).getDays();
    }
}
