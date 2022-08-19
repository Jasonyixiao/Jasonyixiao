//package com.newproject.controller;
//
//import com.newproject.entity.AuthenticationRequest;
//import com.newproject.entity.AuthenticationResponse;
//import com.newproject.service.Login;
//import com.newproject.utils.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RequestMapping("/authen")
//public class JwtSender {
//
//    private final Login login = new Login();
//
//    private final JwtUtil jwtUtil = new JwtUtil();
//
//    @PostMapping()
//    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
//        if (login.login(authenticationRequest.getUsername(), authenticationRequest.getPassword())) {
//            String jwt = jwtUtil.generateToken(authenticationRequest.getUsername());
//            return ResponseEntity.ok(new AuthenticationResponse(jwt));
//        } else {
//            return ResponseEntity.badRequest().body(new AuthenticationResponse("username password don't match"));
//        }
//    }
////,
////
//}
