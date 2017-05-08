package com.awchoudhary.bookpocket.ui.mybooksscreen;

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
    private String dateStarted;
    private String dateCompleted;
    private String dateToReadBy; //date book should be read by when status is TO_READ
    private String datePublished;
    private String dateAdded;
    private String readingStatus;
    private String rowColor; //Hex color of book row
    private String publisher;
    private String isbn;
    private boolean favorite;
    private int seqNo;
    private int currentPage;
    private String userShelfId; //concatenation of userId and shelfId

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

    public String getDateStarted(){
        return dateStarted;
    }
    public void setDateStarted(String dateStarted){
        this.dateStarted = dateStarted;
    }

    public String getDateCompleted(){
        return dateCompleted;
    }
    public void setDateCompleted(String dateCompleted){
        this.dateCompleted = dateCompleted;
    }

    public String getDatePublished() {
        return datePublished;
    }
    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDateToReadBy() {
        return dateToReadBy;
    }
    public void setDateToReadBy(String dateToReadBy) {
        this.dateToReadBy = dateToReadBy;
    }

    public String getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getRowColor() {
        return rowColor;
    }
    public void setRowColor(String rowColor) {
        this.rowColor = rowColor;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getReadingStatus() {
        return readingStatus;
    }
    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public int getSeqNo() {
        return seqNo;
    }
    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getUserShelfId() {
        return userShelfId;
    }
    public void setUserShelfId(String userShelfId) {
        this.userShelfId = userShelfId;
    }
}
