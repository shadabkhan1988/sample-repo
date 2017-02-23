package com.rahulhooda.sampleweatherapp.Model;

/**
 * Created by rahul.hooda on 2/17/17.
 */

public class NewsDetail {

    String newsTitle;
    String newDescription;
    String newsDateAndTime;
    String newsImage;

    public String getNewsReporter() {
        return newsReporter;
    }

    public void setNewsReporter(String newsReporter) {
        this.newsReporter = newsReporter;
    }

    String newsReporter;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public String getNewsDateAndTime() {
        return newsDateAndTime;
    }

    public void setNewsDateAndTime(String newsDateAndTime) {
        this.newsDateAndTime = newsDateAndTime;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }
}
