package com.newproject.entity;

import com.newproject.mapper.SongMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Getter
@Setter
public class Song {
    private String url;
    private String author;
    private String name;
    private int rating;

    private int totalNumRate;


    public Song() {
    }

    public Song(String url, String author, String name) {
        this.url = url;
        this.author = author;
        this.name = name;
        this.rating = 0;
        this.totalNumRate = 0;
    }

    public int getTotalNumRate() {
        return totalNumRate;
    }

    public int setTotalNumRate() {
        this.totalNumRate += 1;
        return totalNumRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
