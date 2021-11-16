package com.mainproject.outlinevisionv2.controller;

import com.mainproject.outlinevisionv2.entity.Authority;
import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.entity.File;
import com.mainproject.outlinevisionv2.repository.AuthorityRepository;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.repository.FileRepository;
import com.mainproject.outlinevisionv2.security.EncodingPassword;
import com.mainproject.outlinevisionv2.security.jwtSecuritiy.JWTBuilder;
import com.mainproject.outlinevisionv2.service.FileService;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "http://192.168.1.2:4200",exposedHeaders ="Authorization")
@RestController
@RequestMapping(value = "/clients", method = RequestMethod.POST)
public class ClientController {

    private AuthorityRepository authorityRepository;

    @Autowired
    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    private FileRepository fileRepository;

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping()
    public List<Client> getClients(){
        return clientRepository.findAll();
    }

    private JWTBuilder jwtBuilder;

    @Autowired
    public void setJwtBuilder(JWTBuilder jwtBuilder) {
        this.jwtBuilder = jwtBuilder;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerClient(@RequestBody Client client){
        //get client if exists
        Client existsClient = clientRepository.findClientByEmail(client.getEmail());

        //if client already exists send not modified status
        if(existsClient != null){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(existsClient);
        }

        File file = fileRepository.findFileByName(client.getFileName());
        client.addFile(file);

        //set encrypted password to client
        String hashedPass = EncodingPassword.passwordEncoder(client.getPassword());
        client.setPassword(hashedPass);

        //add authority(each registered client has user authority as default)
        Authority userAuthority = authorityRepository.findAuthorityByName("user");
        client.addAuthority(userAuthority);

        //give token to client
        String clientToken = jwtBuilder.generateToken(client);
        client.setToken(clientToken);

        //save client to repository
        Client savedClient = clientRepository.save(client);

        return ResponseEntity.ok().body(savedClient);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> loginClient(@RequestBody Client client,HttpServletResponse response){
        Client clientFound = clientRepository.findClientByEmail(client.getEmail());

        //if client is not found return not found status
        if(clientFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        //if passwords do not match return forbidden access
        if(!EncodingPassword.checkIfPwsMatch(client.getPassword(),clientFound.getPassword())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Passwords do not match");
        }
        //else return 200 OK status and add custom header "Authorization" to retrieve token for client side
        String access_token = clientFound.getToken();
        return ResponseEntity.ok().header("Authorization",access_token).body(clientFound);
    }



}
