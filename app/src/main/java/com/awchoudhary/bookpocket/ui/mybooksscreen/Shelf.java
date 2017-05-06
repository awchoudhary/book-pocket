package com.awchoudhary.bookpocket.ui.mybooksscreen;

/**
 * Created by awaeschoudhary on 5/5/17.
 */

public class Shelf {
    private String shelfId;
    private String shelfName;
    private String shelfColor;
    private int bookCount;
    private boolean isPublic;
    private String userId;

    //public no arg constructor
    public Shelf(){}

    public Shelf(String shelfId, String shelfName, String userId){
        this.shelfId = shelfId;
        this.shelfName = shelfName;
        this.userId = userId;
    }

    public String getShelfId() {
        return shelfId;
    }
    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public String getShelfName() {
        return shelfName;
    }
    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public String getShelfColor() {
        return shelfColor;
    }
    public void setShelfColor(String shelfColor) {
        this.shelfColor = shelfColor;
    }

    public int getBookCount() {
        return bookCount;
    }
    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean aPublic) {
        this.isPublic = aPublic;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
