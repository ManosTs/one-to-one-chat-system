package com.mainproject.outlinevisionv2.controller;


import com.mainproject.outlinevisionv2.entity.Authority;
import com.mainproject.outlinevisionv2.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/authorities", method = RequestMethod.POST)
public class AuthorityController {

    private AuthorityRepository authorityRepository;
    @Autowired
    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @GetMapping
    public List<Authority> getAllAuthorities(){
        return authorityRepository.findAll();
    }
}
