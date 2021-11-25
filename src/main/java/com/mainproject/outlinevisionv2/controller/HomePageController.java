package com.mainproject.outlinevisionv2.controller;


import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://192.168.1.2:4200")
@RestController
@RequestMapping(value = "/home", method = RequestMethod.POST)
public class HomePageController {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ResponseEntity<?> accessHome(@RequestParam("token") String token){
        if(token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have the permissions");
        }
        Client clientFound = this.clientRepository.findClientByToken(token);

        return ResponseEntity.ok().body(clientFound);

    }

}
