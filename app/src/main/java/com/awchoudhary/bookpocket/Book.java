package com.awchoudhary.bookpocket;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.Serializable;

/**
 * Created by awaeschoudhary on 2/21/17.
 */

public class Book implements Serializable {
    private String id;
    private String name; //TODO: Change "name" to "title"
    private String subtitle;
    private String author;
    private String coverUrl;
    private int numPages;
    private String description;
    private int ratings;
    private String notes;
    private boolean completed;
    private DateTime dateStarted;
    private DateTime dateCompleted;

    public Book(){}

    public Book(String name, String author, String coverUrl){
        this.name = name;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getSubtitle(){
        return subtitle;
    }
    public void setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author = author;
    }

    public String getCoverUrl(){
        return coverUrl;
    }
    public void setCoverUrl(String coverUrl){
        this.coverUrl = coverUrl;
    }

    public int getNumPages(){
        return numPages;
    }
    public void setNumPages(int numPages){
        this.numPages = numPages;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public int getRatings(){
        return ratings;
    }
    public void setRatings(int ratings){
        this.ratings = ratings;
    }

    public String getNotes(){
        return notes;
    }
    public void setNotes(String notes){
        this.notes = notes;
    }

    public boolean getCompleted(){
        return completed;
    }
    public void setCompleted(boolean completed){
        this.completed = completed;
    }

    public DateTime getDateStarted(){
        return dateStarted;
    }
    public void setDateStarted(DateTime dateStarted){
        this.dateStarted = dateStarted;
    }

    public DateTime getDateCompleted(){
        return dateCompleted;
    }
    public void setDateCompleted(DateTime dateCompleted){
        this.dateCompleted = dateCompleted;
    }

    public int getDaysSinceStarted(){
        return Days.daysBetween(dateStarted, new DateTime()).getDays();
    }

    public int getDaysTakenToComplete(){
        return Days.daysBetween(dateStarted, dateCompleted).getDays();
    }

    public int getDaysSinceCompleted(){
        return Days.daysBetween(dateCompleted, new DateTime()).getDays();
    }
}
