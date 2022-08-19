package com.newproject.controller;


import com.mysql.cj.log.Log;
import com.newproject.entity.AuthenticationRequest;
import com.newproject.entity.AuthenticationResponse;
import com.newproject.entity.Song;
import com.newproject.entity.User;
import com.newproject.service.*;
import com.newproject.utils.JwtGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;


// 接口： http://localhost:8081/user/login
@RestController
@RequestMapping("/user/login")
@Slf4j
public class helloController {

    @Autowired
    ValidateJwt validateJwt;
    @Autowired
    Login login;
    @Autowired
    JwtGenerator generator;

    @Autowired
    JwtValidator jwtValidator;

    //@RequestHeader(value = "Authorization") String authorizationHeader

    @GetMapping()
    public ResponseEntity<HashMap<String, String>> getUser(@RequestParam("username") String username,
                                                           @RequestHeader(value = "Authorization") String authorizationHeader){
        System.out.println(authorizationHeader);
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            log.info("tokennotnull");
            UpdateDB updateDB = new UpdateDB();
            User user = updateDB.getUserById(username);
            HashMap<String, String> info = new HashMap<>();
            info.put("username", username);
            info.put("DOB", user.getDOB().toString());
            info.put("firstname", user.getFirstname());
            info.put("lastname", user.getLastname());
            info.put("email", user.getEmail());
            info.put("jwt", token);
            return ResponseEntity.ok(info);
        } else {
            log.info("check");
            HashMap<String, String> map = new HashMap<>();
            map.put("error", "bad Request");
            return ResponseEntity.badRequest().body(map);
        }
    }


    //@RequestHeader(value = "Accept") String acceptHeader,
    //@RequestHeader(value = "Authorization") String authorizationHeader
    @PostMapping
    public ResponseEntity<String> createUser(@RequestParam("username") String username,
                             @RequestHeader(value = "Authorization") String authorizationHeader,
                             @RequestParam("email") String email,
                             @RequestParam("DOB") String DOB,
                             @RequestParam("firstname") String firstname,
                             @RequestParam("password") String password,
                             @RequestParam("lastname") String lastname) {
        // the http accept header will be read and injected as a parameter
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            UpdateDB updateDB = new UpdateDB();
            updateDB.addUser(username, email, DOB, firstname, password, lastname);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Login first");
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteUser(@RequestHeader(value = "Authorization") String authorizationHeader,
                             @RequestParam("username") String username){
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            UpdateDB updateDB = new UpdateDB();
            updateDB.deleteUser(username);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Please Login");
        }
    }

    @RequestMapping("/updateEmail")
    @PutMapping()
    public ResponseEntity<String> updateEmail(@RequestHeader(value = "Authorization") String authorizationHeader,
                              @RequestParam("username") String username,
                              @RequestParam("email") String email){
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            UpdateDB updateDB = new UpdateDB();
            updateDB.updateEmail(username, email);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Please Login");
        }

    }
    @RequestMapping("/updatename")
    @PutMapping()
    public ResponseEntity<String> updateName(@RequestHeader(value = "Authorization") String authorizationHeader,
                             @RequestParam("username") String username,
                             @RequestParam("firstname") String firstname,
                             @RequestParam("lastname") String lastname){
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            UpdateDB updateDB = new UpdateDB();
            updateDB.updateUserName(username, firstname, lastname);
            return ResponseEntity.ok(token);
        } else{
            return ResponseEntity.badRequest().body("Please login First");
        }

    }

    @RequestMapping("/updateDOB")
    @PutMapping()
    public ResponseEntity<String> updateDOB(@RequestHeader(value = "Authorization") String authorizationHeader,
                            @RequestParam("username") String username,
                              @RequestParam("DOB") String DOB){
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            UpdateDB updateDB = new UpdateDB();
            updateDB.updateDOB(username, DOB);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("username password don't match");
        }
    }

    @RequestMapping("/updatePassword")
    @PutMapping()
    public ResponseEntity<String> updatePassword(@RequestHeader(value = "Authorization") String authorizationHeader,// for now they can change their password if they enter their old U&pwd
                                 @RequestParam("username") String username,
                                 @RequestParam("password") String password){
        String token = validateJwt.validate(authorizationHeader);
        UpdateDB updateDB = new UpdateDB();
        if (token != null) {
            updateDB.updatePassword(username, password);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("invalid token");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login( // user will get a JWT is login was successful
            @RequestBody AuthenticationRequest authenticationRequest) {
        log.info("已进入请求");
        if (login.login(authenticationRequest.getUsername(), authenticationRequest.getPassword())) {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("sub", authenticationRequest.getUsername());
            String jwt = generator.generateJwt(payload, new Date(System.currentTimeMillis() + 1000*60*60));
            return ResponseEntity.ok(new AuthenticationResponse(jwt)); // return a new jwt with updated expiration date
        } else {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("username password don't match"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout( // user will get a JWT is login was successful
                                                          @RequestHeader(value = "Authorization") String authorizationHeader) {
        String token = validateJwt.validate(authorizationHeader);
        if (token != null) {
            return ResponseEntity.ok(new AuthenticationResponse(jwtValidator.expireToken(token))); // return a new jwt with updated expiration date
        } else {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("username password don't match"));
        }
    } // suppose the JWT is stored locally, what happens when


}
