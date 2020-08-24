package com.example.cashy.model;

public class User {

    public String name,email,userId,profileImg,referCode;
    public int coins,gems;
    public float cash;

    public User() {
    }

    public User(String name, String email, String userId, String profileImg, String referCode, int coins, int gems, float cash) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.profileImg = profileImg;
        this.referCode = referCode;
        this.coins = coins;
        this.gems = gems;
        this.cash = cash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }
}


