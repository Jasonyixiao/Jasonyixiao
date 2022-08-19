package com.newproject.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class ValidateJwt {
    @Autowired
    JwtValidator jwtValidator;
    public ValidateJwt() {
    }
    public String validate(String authorizationHeader) {
         JwtValidator validator = new JwtValidator();

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            try{
                String token = validator.validate(jwt);
                return token;
            } catch (Exception e) {
                return null;
            }

        } else{
            return null;
        }
    }
}
