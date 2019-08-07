package net.projectwhitespace.reviewcheck;

public class itemResult {
    private String name;
    private double rating;
    private String link;
    private Byte overall;
    private String ASIN;
    private String amazonType;
    private String pictureURL;


    public itemResult(String name, double rating, Byte overall, String link, String ASIN, String amazonType, String pictureURL){
        this.name = name;
        this.rating = rating;
        this.overall = overall;
        this.link = link;
        this.ASIN = ASIN;
        this.amazonType = amazonType;
        this.pictureURL = pictureURL;
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

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}
