package com.lennart.model.headlinesFE;

import java.util.List;

/**
 * Created by LennartMac on 02/10/2017.
 */
public class Topic {

    private int entry;
    private String dateTime;
    private List<String> headlines;
    private List<String> links;
    private List<String> sites;
    private String imageLink;

    public Topic() {

    }

    public Topic(int entry, String dateTime, List<String> headlines, List<String> links, List<String> sites, String imageLink) {
        this.entry = entry;
        this.dateTime = dateTime;
        this.headlines = headlines;
        this.links = links;
        this.sites = sites;
        this.imageLink = imageLink;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
