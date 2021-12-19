package com.mainproject.outlinevisionv2.controller;

import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.security.jwtSecuritiy.JWTBuilder;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    public ResponseEntity<?> verifyAccess(HttpServletRequest request) throws ParseException, JOSEException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return HomePageController.getResponseEntity(request, jwtBuilder);
    }
}
