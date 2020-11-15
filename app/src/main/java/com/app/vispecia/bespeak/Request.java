package com.app.vispecia.bespeak;

public class Request {

    String profileImg, bookImg, bookName,hisUID,hisName;

    public Request() {
    }

    public Request(String profileImg, String bookImg, String bookName, String hisUID,String hisName) {
        this.profileImg = profileImg;
        this.bookImg = bookImg;
        this.bookName = bookName;
        this.hisUID = hisUID;
        this.hisName = hisName;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getBookImg() {
        return bookImg;
    }

    public void setBookImg(String bookImg) {
        this.bookImg = bookImg;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getHisUID() {
        return hisUID;
    }

    public void setHisUID(String hisUID) {
        this.hisUID = hisUID;
    }

    public String getHisName() {
        return hisName;
    }

    public void setHisName(String hisName) {
        this.hisName = hisName;
    }
}
