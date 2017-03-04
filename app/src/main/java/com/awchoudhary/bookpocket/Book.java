package com.awchoudhary.bookpocket;

/**
 * Created by awaeschoudhary on 2/21/17.
 */
public class Book {
    private String id;
    private String name;
    private String subtitle;
    private String author;
    private String coverUrl;
    private int numPages;
    private String description;
    private int ratings;

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
}
