package com.awchoudhary.bookpocket.util;

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

    public static int getDaysSince(DateTime date){
        return Days.daysBetween(date, new DateTime()).getDays();
    }

    public static int getDaysBetween(DateTime dateStarted, DateTime dateCompleted){
        return Days.daysBetween(dateStarted, dateCompleted).getDays();
    }

    public static int getDaysTill(DateTime date){
        return Days.daysBetween(new DateTime(), date).getDays();
    }

    //Convert date string from MM/dd/YYYY to Month, day Year format
    public static String formatDate(String dateString){
        if(dateString == null || dateString.isEmpty()){
            return null;
        }

        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");

        DateTime dateTimeString = dtf.parseDateTime(dateString);

        return dateTimeString.toString(DateTimeFormat.mediumDate());
    }
}
