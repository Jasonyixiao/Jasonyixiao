package com.newproject.controller;

import com.newproject.entity.Song;
import com.newproject.entity.User;
import com.newproject.service.SongManager;
import com.newproject.service.UpdateDB;
import com.newproject.service.ValidateJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    SongManager songManager;

    @Autowired
    ValidateJwt validateJwt;

    //@RequestHeader(value = "Authorization") String authorizationHeader
    @RequestMapping("/getsong")
    public ResponseEntity<Song> getSong(@RequestParam("Songname") String songName,
                                        @RequestHeader(value = "Authorization") String authorizationHeader
                                        ){
        System.out.println(authorizationHeader);
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
        return ResponseEntity.ok(songManager.getSong(songName));
        } else {
            return ResponseEntity.badRequest().body(new Song());
        }
    }

    @RequestMapping("/addSong")
    public ResponseEntity<String> addSong(@RequestParam("Songname") String songName,
                                          @RequestParam("url") String url,
                                          @RequestParam("artist") String artist,
                                          @RequestHeader(value = "Authorization") String authorizationHeader
    ){
        System.out.println(authorizationHeader);
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {

            if (songManager.addSong(songName, url, artist) == 1) {
                return ResponseEntity.ok("Success!");
            } else {
                return ResponseEntity.badRequest().body("rating unsuccessful");
            }
        } else {
            return ResponseEntity.badRequest().body("invalid token");
        }
    }

    @RequestMapping("/rateSong")
    public ResponseEntity<String> rateSong(@RequestParam("Songname") String songName,
                                        @RequestParam("rating") int rating,
                                        @RequestHeader(value = "Authorization") String authorizationHeader
    ){
        System.out.println(authorizationHeader);
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            Song song = songManager.getSong(songName);
            if (songManager.rateSong(song, rating) == 1) {
                return ResponseEntity.ok("Success!");
            } else {
                return ResponseEntity.badRequest().body("rating unsuccessful");
            }
        } else {
            return ResponseEntity.badRequest().body("invalid token");
        }
    }
}
