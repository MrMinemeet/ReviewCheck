package net.projectwhitespace.reviewcheck;

import android.graphics.Bitmap;

public class itemResult {
    private String name;
    private double rating;
    private String link;
    private Byte overall;
    private String ASIN;
    private String amazonType;
    private String pictureUrl;
    private Bitmap picture;


    public itemResult(String name, double rating, Byte overall, String link, String ASIN, String amazonType){
        this.name = name;
        this.rating = rating;
        this.overall = overall;
        this.link = link;
        this.ASIN = ASIN;
        this.amazonType = amazonType;
    }

    public itemResult() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Byte getOverall() {
        return overall;
    }

    public void setOverall(Byte overall) {
        this.overall = overall;
    }

    public String getASIN() {
        return ASIN;
    }

    public void setASIN(String ASIN) {
        this.ASIN = ASIN;
    }

    public String getAmazonType() {
        return amazonType;
    }

    public void setAmazonType(String amazonType) {
        this.amazonType = amazonType;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap pictureBmp) {
        this.picture = pictureBmp;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl){
        this.pictureUrl = pictureUrl;
    }
}
