package com.mainproject.outlinevisionv2.controller;


import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.security.jwtSecuritiy.JWTBuilder;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.EncryptedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@CrossOrigin(origins = "http://localhost:4200",allowCredentials = "true")
@RestController
@RequestMapping(value = "/home", method = RequestMethod.POST)
public class HomePageController {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private JWTBuilder jwtBuilder;

    @Autowired
    public void setJwtBuilder(JWTBuilder jwtBuilder) {
        this.jwtBuilder = jwtBuilder;
    }

    @GetMapping
    public ResponseEntity<?> verifyAccess(HttpServletRequest request) throws ParseException, JOSEException {
        return getResponseEntity(request, jwtBuilder);
    }

    public static ResponseEntity<?> getResponseEntity(HttpServletRequest request, JWTBuilder jwtBuilder) throws ParseException, JOSEException {
        Cookie cookie = WebUtils.getCookie(request, "enc_token");

        if(cookie == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        if(!jwtBuilder.verifyToken(cookie.getValue().substring(7))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }

        return ResponseEntity.ok().body(cookie.getValue().substring(7));
    }
}
