package com.scvr_tech.travelmantics;

public class TravelDeal {

    private String id;
    private String title;
    private String description;
    private String price;
    private String imgUrl;

    public TravelDeal() {
        // Required empty constructor
    }

    public TravelDeal(String title, String description, String price, String imgUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}