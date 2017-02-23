package com.awchoudhary.bookpocket;

/**
 * Created by awaeschoudhary on 2/21/17.
 */
public class Book {
    private String name;
    private String author;
    private String coverUrl;

    public Book(){}

    public Book(String name, String author, String coverUrl){
        this.name = name;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
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
}
