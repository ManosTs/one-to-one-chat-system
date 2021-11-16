package com.mainproject.outlinevisionv2.controller;


import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://192.168.1.2:4200", exposedHeaders = "Authorization")
@RestController
@RequestMapping(value = "/home", method = RequestMethod.POST)
public class HomePageController {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public String getHome(){
        return "home";
    }

    @GetMapping("/settings/{id}")
    public ResponseEntity getClientSettings(@PathVariable("id") String id){
        Client clientFound = clientRepository.findClientById(id);
        return ResponseEntity.ok().body(clientFound.getId());
    }
}
