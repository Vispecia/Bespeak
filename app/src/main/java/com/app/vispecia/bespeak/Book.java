package com.app.vispecia.bespeak;

public class Book {

    String bookName;
    String authorName;
    String imageUrl;
    String key;
    boolean exchangable;
    public Book() {
    }

    public Book(String bookName, String authorName,String imageUrl,String key,boolean exchangable) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
        this.key = key;
        this.exchangable = exchangable;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isExchangable() {
        return exchangable;
    }

    public void setExchangable(boolean exchangable) {
        this.exchangable = exchangable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
