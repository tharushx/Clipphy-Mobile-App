package com.app.clipphy.Domain;

import java.util.List;

public class AllProducts {
    private String title;
    private String status;
    private String count;
    private String price;
    private String priceSale;
    private String picAddress;
    private float ratingBar;
    private String description;
    private String clipphyDetails;
    private String duration;
    private String current_count;
    private String clipphyStatus;
    private String numberincart;
    private String product_Id;
    private String started_date;
    private List<String> members;

    public AllProducts(String title, String status, String count, String price, String priceSale, String started_date, String picAddress, float ratingBar, String description, String clipphyDetails, String duration, String current_count, String clipphyStatus, String numberincart, String product_Id, List<String> members) {
        this.title = title;
        this.status = status;
        this.count = count;
        this.priceSale = priceSale;
        this.price = price;
        this.picAddress = picAddress;
        this.ratingBar = ratingBar;
        this.description = description;
        this.clipphyDetails = clipphyDetails;
        this.duration = duration;
        this.current_count = current_count;
        this.clipphyStatus = clipphyStatus;
        this.numberincart = numberincart;
        this.product_Id = product_Id;
        this.members = members;
        this.started_date = started_date;
    }

    public AllProducts() {}

    public AllProducts(String title, String status, String count, String price, String picAddress, int ratingBar, String number) {
    }

    public String getPriceSale() {
        return priceSale;
    }

    public void setPriceSale(String priceSale) {
        this.priceSale = priceSale;
    }

    public String getStarted_date() {
        return started_date;
    }

    public void setStarted_date(String started_date) {
        this.started_date = started_date;
    }

    public String getProduct_Id() {
        return product_Id;
    }

    public void setProduct_Id(String product_Id) {
        this.product_Id = product_Id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public float getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(float ratingBar) {
        this.ratingBar = ratingBar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClipphyDetails() {
        return clipphyDetails;
    }

    public void setClipphyDetails(String clipphyDetails) {
        this.clipphyDetails = clipphyDetails;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCurrent_count() {
        return current_count;
    }

    public void setCurrent_count(String current_count) {
        this.current_count = current_count;
    }

    public String getClipphyStatus() {
        return clipphyStatus;
    }

    public void setClipphyStatus(String clipphyStatus) {
        this.clipphyStatus = clipphyStatus;
    }

    public String getNumberincart() {
        return numberincart;
    }

    public void setNumberincart(String numberincart) {
        this.numberincart = numberincart;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public int getId() {
        return 0;
    }
}
