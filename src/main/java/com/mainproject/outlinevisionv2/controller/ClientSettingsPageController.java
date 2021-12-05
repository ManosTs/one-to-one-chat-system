package com.mainproject.outlinevisionv2.controller;

import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.security.jwtSecuritiy.JWTBuilder;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@CrossOrigin(origins = "http://localhost:4200",allowCredentials = "true")
@RestController
@RequestMapping(value = "/settings", method = RequestMethod.POST)
public class ClientSettingsPageController {

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
        Cookie cookie1 = WebUtils.getCookie(request, "enc_token");

        if(cookie1 == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        if(!jwtBuilder.verifyToken(cookie1.getValue().substring(7))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }

        return ResponseEntity.ok().body(cookie1.getValue().substring(7));
    }
}
