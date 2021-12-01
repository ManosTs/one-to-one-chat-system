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
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "http://192.168.1.5:4200", exposedHeaders = "Authorization")
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

    private JWTBuilder jwtBuilder;

    @Autowired
    public void setJwtBuilder(JWTBuilder jwtBuilder) {
        this.jwtBuilder = jwtBuilder;
    }
    //-----------------------------------------------------------------------------------------------------------------//

    @GetMapping(value = "/all")
    public ResponseEntity<?> getClientsByFirstName(){
        List<Object[]> clientsList = clientRepository.findAllGroupByFirstName();
        return ResponseEntity.ok().body(clientsList);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerClient(@RequestBody Client client) throws ParseException, NoSuchAlgorithmException, JOSEException, IOException, InvalidKeySpecException {
        //get client if exists
        Client existsClient = clientRepository.findClientByEmail(client.getEmail());

        //if client already exists send not modified status
        if(existsClient != null){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(existsClient);
        }

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
    public ResponseEntity<?> loginClient(@RequestBody Client client, HttpServletResponse response) throws ParseException, JOSEException {
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
//       String access_token = clientFound.getToken();

        clientFound.setActive(true);
        clientFound.setLast_logon(new Date());
        Client savedClient = clientRepository.save(clientFound);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", savedClient.getToken());

        return ResponseEntity.ok().headers(headers).body(savedClient);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<?> logoutClient(@RequestParam("id") String id){
        Client clientFound = clientRepository.findClientById(id);

        clientFound.setActive(false);

        clientFound.setLast_logout(new Date());

        return ResponseEntity.ok().body("You have disconnected!");
    }

    @GetMapping(value = "/isActive/{id}")
    public ResponseEntity<?> isClientActive(@PathVariable String id){
        Client clientFound = clientRepository.findClientById(id);

        return ResponseEntity.ok().body(clientFound.getActive());
    }

    @GetMapping(value = "/{id}/status={status}")
    public ResponseEntity<?> changeStatus(@PathVariable String id, @PathVariable("status") Boolean status){
        Client clientFound = clientRepository.findClientById(id);

        if(!status){
            clientFound.setActive(false);
            clientFound.setLast_logout(new Date());
            Client savedClient = clientRepository.save(clientFound);
            return ResponseEntity.ok().body(savedClient.getActive());
        }

        clientFound.setLast_logout(null);

        clientFound.setActive(true);

        Client savedClient = clientRepository.save(clientFound);

        return ResponseEntity.ok().body(savedClient.getActive());
    }

    @GetMapping(value = "/lastSeen")
    public ResponseEntity<?> lastSeen(@RequestParam("id") String id) {
        Client clientFound = clientRepository.findClientById(id);

        Date date1 = clientFound.getLast_logout();
        Date date2 = new Date();

        long diff = date2.getTime() - date1.getTime();

        if(diff == date2.getTime()){
            return ResponseEntity.ok().body(0);
        }

        long diffInMinutes = diff / (60 * 1000) % 60;



        return ResponseEntity.ok().body(diffInMinutes);
    }

    @GetMapping(value = "/{id}/lastLogon")
    public ResponseEntity<?> lastLogon(@PathVariable String id) {
        Client clientFound = clientRepository.findClientById(id);

        return ResponseEntity.ok().body(clientFound.getLast_logon());

    }

    @GetMapping(value = "/encrypted-token")
    public ResponseEntity<?> getClaimsFromToken(@RequestParam("access_token") String access_token) throws ParseException, JOSEException {

        JWTClaimsSet claims = jwtBuilder.decodeToken(access_token);

        return ResponseEntity.ok().body(claims);

    }
}
