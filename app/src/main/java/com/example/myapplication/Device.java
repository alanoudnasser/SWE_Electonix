package com.example.myapplication;

public class Device {
    private int id;
    private String name;
    private String company;
    private int price;
    private int userId;
    private String username;
    private byte[] image;
    private int currentRentalId = -1;



    public Device(int id, String name, String company, int price, int userId, byte[] image) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.price = price;
        this.userId = userId;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getRentalId() {
        return currentRentalId;
    }

    public void setRentalId(int rentalId) {
        this.currentRentalId = rentalId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
