package com.newproject.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import com.newproject.entity.User;
import com.newproject.utils.JwtGenerator;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JwtValidator {
    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    UpdateDB updateDB;

//    public static void main(String[] args) {
//        JwtValidator jwtValidator = new JwtValidator();
//        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJqYXNvbjIwMDIiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLnF1YWRtZXVwLmNvbS9hdXRoL3JlYWxtcy9SZWFsbSIsImV4cCI6MTY2MDYyOTA5OCwiaWF0IjoxNjYwNjI4NDk4LCJqdGkiOiJlNTY1OGU4ZC1lYzQyLTQyYWUtYWJkYS1iYTM2OGJlMWU4ZGIifQ.sOad9GbLutAiJ3rQRgW3nohccLX-uOKWASujEFqfrISyF4S6ywyOycdGiQCrV54zelkrhgLnVEKOJH-IRMJLFnAt5Kf7dkPhG4xE9KEhWhDwm30fMKn9Yw2InaXVjc7GhYOAz_iNqc6cQwzoqLJD59vjzrFgooKWzysPYvc9YSxJxLBrF1Fdq96VBILeI0fhZmuR-w5jCP8rMbWLq1Sq0Umo1Lcz8mRMwE_7TOCvXrvlD3Ghwzhfykyp4D0D4ukJhCcJmAIUol8h3558-VEME15BQ1w5g5cnqOR0BPu14gHmWJp2yQeFclEdVGh7Cb_hfxqJrVFXEEfel5lRWaQIpA";
//        System.out.println(jwtValidator.getPayload(jwt));
//    }

    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);
    private static final List<String> allowedIsses = Collections.singletonList("https://keycloak.quadmeup.com/auth/realms/Realm");

    private String getKeycloakCertificateUrl(DecodedJWT token) {
        return token.getIssuer() + "/protocol/openid-connect/certs";
    }

//    private RSAPublicKey loadPublicKey(DecodedJWT token) throws JwkException, MalformedURLException {
//
//        final String url = getKeycloakCertificateUrl(token);
//        JwkProvider provider = new UrlJwkProvider(new URL(url));
//
//        return (RSAPublicKey) provider.get(token.getKeyId()).getPublicKey();
//    }

    /**
     * Validate a JWT token
     *
     * @param token
     * @return decoded token
     */
    public String validate(String token) {
        //let's check the user has given the currect token
        //get the username first:
        log.info("check2");
        try {
            //Decode the token
            final DecodedJWT jwt = JWT.decode(token);
            //checks issuer
            if (!allowedIsses.contains(jwt.getIssuer())) {
                throw new InvalidParameterException(String.format("Unknown Issuer %s", jwt.getIssuer()));
            }
            JwtGenerator generator = new JwtGenerator();
            RSAPublicKey publicKey = (RSAPublicKey) generator.readPublicKey();
            Algorithm algorithm = Algorithm.RSA256(publicKey, (RSAPrivateKey) readPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwt.getIssuer())
                    .build();

            verifier.verify(token);
            Date expiresAt = jwt.getExpiresAt();
            Date newExpiresAt = extendExpireDate(expiresAt);
            Map<String, String> newPayload = getPayload(token);
            log.info("check4");
            //generate a new token
            String jwt2 = generator.generateJwt(newPayload, newExpiresAt);
            System.out.println(jwt2);
            return jwt2; // we might want to return a new jwt

            } catch (Exception e) {
                logger.error("Failed to validate JWT", e);
                throw new InvalidParameterException("JWT validation failed: " + e.getMessage());
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
    private Date extendExpireDate(Date expireDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(expireDate);
        cal.add(Calendar.MINUTE, 10);
        return cal.getTime();
    }


    public String expireToken(String jwt) {
        HashMap<String, String> payload = getPayload(jwt);
        String tokenNew = jwtGenerator.generateJwt(payload, new Date(System.currentTimeMillis()));
        updateDB.updateJwt(getPayload(jwt).get("sub"), tokenNew);
        return tokenNew;
    }

    private HashMap<String, String> getPayload(String jwt) {
        // we create a map
        HashMap<String, String> map = new HashMap<>();
        //convert the string into
        String payload = getPayloadInString(jwt);
        String value = payload.substring(1, payload.length() - 1);
        String[] keyValuePairs = value.split(",");
        for (String pair : keyValuePairs){
            String[] entry = pair.split(":"); // iterate over pairs
            map.put(entry[0].substring(1, entry[0].length()-1), entry[1].trim());
        }
        // create a new payload
        HashMap<String, String> newPayload = new HashMap<>();
        newPayload.put("sub", map.get("sub"));
        return newPayload;
    }

    public String getPayloadInString(String token){
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        return new String(decoder.decode(chunks[1]));
    }

    private boolean validateToken(String username, String jwt){
        User user = updateDB.getUserById(username);
        return Objects.equals(user.getJwt(), jwt);
    }



}

