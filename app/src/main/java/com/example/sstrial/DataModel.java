package com.example.sstrial;

public class DataModel {
    private String imageUrl;
    private String result;
    private String dateTime;

    public DataModel() {
        // default constructor
    }

    public DataModel(String imageUrl, String result, String dateTime) {
        this.imageUrl = imageUrl;
        this.result = result;
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getResult() {
        return result;
    }

    public String getDateTime() {
        return dateTime;
    }
}
