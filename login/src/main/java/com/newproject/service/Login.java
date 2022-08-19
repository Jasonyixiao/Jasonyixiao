package com.newproject.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Service
@Component
public class Login {
    private final UpdateDB updateDB;

    public Login() {
        this.updateDB = new UpdateDB();
    }

    public boolean login(String username, String password){
        String saltString = updateDB.getsalt(username);
        byte[] salt = Base64.getDecoder().decode(saltString);
        String hash = updateDB.getHash(username);
        try {
            return Objects.equals(generateHash(password, salt), hash);
        } catch (Exception e) {
            return false;
        }
    }

    private static String generateHash(String password, byte[] salt) {

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


}
