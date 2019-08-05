package net.projectwhitespace.reviewcheck;

public class itemResult {
    public String name;
    public double rating;
    public String link;
    public Byte overall;
    public String ASIN;
    public String amazonType;


    public itemResult(String name, double rating, Byte overall, String link, String ASIN, String amazonType){
        this.name = name;
        this.rating = rating;
        this.overall = overall;
        this.link = link;
        this.ASIN = ASIN;
        this.amazonType = amazonType;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getLink() {
        return link;
    }

    public Byte getOverall() {
        return overall;
    }

    public String getASIN() {
        return ASIN;
    }

    public String getAmazonVersion() {
        return amazonType;
    }
}
