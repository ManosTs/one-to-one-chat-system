package com.mainproject.outlinevisionv2.controller;


import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.security.jwtSecuritiy.JWTBuilder;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.EncryptedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@CrossOrigin(origins = "http://192.168.1.5:4200",exposedHeaders ="Authorization")
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
    public ResponseEntity<?> accessHome(@RequestParam("access_token") String access_token)
            throws ParseException, JOSEException {

        if(access_token == null || !jwtBuilder.verifyToken(access_token)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You do not have the permissions");
        }

        Client clientFound = clientRepository.findClientByToken(access_token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", access_token);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(clientFound);
    }

}
