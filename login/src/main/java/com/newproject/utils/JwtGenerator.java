package com.newproject.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
public class JwtGenerator {
//    public static void main(String[] args) {
//        JwtGenerator generator = new JwtGenerator();
//        System.out.println(generator.generateJwt(new HashMap<>()));
//    }

    public JwtGenerator(){}


    public PublicKey readPublicKey(){
        File publicKeyFile = new File("public.key");
        byte[] publicKeyBytes = new byte[0];
        try {
            publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        try {
            return keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private PrivateKey readPrivateKey(){
        File privateKeyFile = new File("private.key");
        byte[] privateKeyBytes = new byte[0];
        try {
            privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try {
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


    public String generateJwt(Map<String, String> payload, Date expireDate) {
        log.info("passed");
        JWTCreator.Builder tokenBuilder = JWT.create()
                .withIssuer("https://keycloak.quadmeup.com/auth/realms/Realm")
                .withClaim("jti", UUID.randomUUID().toString())
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date(System.currentTimeMillis()));

        payload.entrySet().forEach(action -> tokenBuilder.withClaim(action.getKey(), action.getValue()));
        return  tokenBuilder.sign(Algorithm.RSA256(((RSAPublicKey) readPublicKey()), ((RSAPrivateKey) readPrivateKey())));
    }

    //    private void createKey(){
//        KeyPairGenerator keyPairGenerator;
//        try {
//            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        keyPairGenerator.initialize(2048);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        try (FileOutputStream fos = new FileOutputStream("public.key")) {
//            fos.write(keyPair.getPublic().getEncoded());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        try (FileOutputStream fos2 = new FileOutputStream("private.key")) {
//            fos2.write(keyPair.getPrivate().getEncoded());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
