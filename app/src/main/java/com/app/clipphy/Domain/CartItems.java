package com.app.clipphy.Domain;

public class CartItems {

    private String title;
    private String pic;
    private String feeEachItem;

    public CartItems(String title, String pic, String feeEachItem, String totalEachItem, String numberInCart) {
        this.title = title;
        this.pic = pic;
        this.feeEachItem = feeEachItem;
        this.totalEachItem = totalEachItem;
        NumberInCart = numberInCart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getFeeEachItem() {
        return feeEachItem;
    }

    public void setFeeEachItem(String feeEachItem) {
        this.feeEachItem = feeEachItem;
    }

    public String getTotalEachItem() {
        return totalEachItem;
    }

    public void setTotalEachItem(String totalEachItem) {
        this.totalEachItem = totalEachItem;
    }

    public String getNumberInCart() {
        return NumberInCart;
    }

    public void setNumberInCart(String numberInCart) {
        NumberInCart = numberInCart;
    }

    private String totalEachItem;
    private String NumberInCart;

}
