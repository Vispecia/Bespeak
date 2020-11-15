package com.app.vispecia.bespeak;

class Users {

    String username,id,place,profileImage,onlineStatus;

    public Users(){}

    public Users(String username, String id,String place,String profileImage, String onlineStatus) {
        this.username = username;
        this.id = id;
        this.place = place;
        this.profileImage = profileImage;
        this.onlineStatus = onlineStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
