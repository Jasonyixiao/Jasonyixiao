package com.newproject.entity;

import lombok.*;

import java.util.Date;
@Data
@AllArgsConstructor
@Setter
@Getter
public class User {
    private final String username;
    private String email;
    private Date DOB;
    private String firstname;
    private String lastname;
    private String salt;
    private String hash;
    private String jwt;


    @Override
    public String toString() {
        return "User{" +
                "useranme='" + username + '\'' +
                ", email='" + email + '\'' +
                ", DOB=" + DOB +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", salt='" + salt + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }

    public String getSalt() {
        return salt;
    }

    public String gethash() {
        return hash;
    }

    public void sethash(String hash) {
        this.hash = hash;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}

