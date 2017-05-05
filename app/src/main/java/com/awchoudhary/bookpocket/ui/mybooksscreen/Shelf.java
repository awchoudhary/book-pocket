package com.awchoudhary.bookpocket.ui.mybooksscreen;

/**
 * Created by awaeschoudhary on 5/5/17.
 */

public class Shelf {
    private int shelfId;
    private String shelfName;
    private String shelfColor;
    private int bookCount;
    private boolean isPublic;

    public int getShelfId() {
        return shelfId;
    }
    public void setShelfId(int shelfId) {
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
}
