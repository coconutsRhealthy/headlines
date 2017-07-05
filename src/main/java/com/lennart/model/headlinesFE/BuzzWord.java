package com.lennart.model.headlinesFE;

import java.util.List;

/**
 * Created by LennartMac on 25/06/17.
 */
public class BuzzWord {

    private String dateTime;
    private String word;
    private List<String> headlines;
    private List<String> links;

    public BuzzWord(String dateTime, String word, List<String> headlines, List<String> links) {
        this.dateTime = dateTime;
        this.word = word;
        this.headlines = headlines;
        this.links = links;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getHeadlines() {
        return headlines;
    }

    public void setHeadlines(List<String> headlines) {
        this.headlines = headlines;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
}
