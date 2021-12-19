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
import lombok.extern.flogger.Flogger;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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

    @GetMapping(value = "/findClientByName")
    public ResponseEntity<?> getClientByKeyword(@RequestParam("name") String name) {
        List<Object[]> clients = clientRepository.findByKeyword(name);
        return ResponseEntity.ok().body(clients);
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveClients() {
        List<Object[]> list = clientRepository.findAllActiveClients();

        return ResponseEntity.ok().body(list);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerClient(@RequestBody Client client) throws ParseException, NoSuchAlgorithmException, JOSEException, IOException, InvalidKeySpecException {
        //get client if exists
        Client existsClient = clientRepository.findClientByEmail(client.getEmail());

        //if client already exists send not modified status
        if (existsClient != null) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(existsClient);
        }

        //set encrypted confirmed password
        String hashedConfirmPass = EncodingPassword.passwordEncoder(client.getConfirmPassword());
        client.setConfirmPassword(hashedConfirmPass);

        //set encrypted password to client
        String hashedPass = EncodingPassword.passwordEncoder(client.getPassword());
        client.setPassword(hashedPass);

        //add authority(each registered client has user authority as default)
        Authority userAuthority = authorityRepository.findAuthorityByName("user");
        client.addAuthority(userAuthority);

        //give token to client
        String clientToken = jwtBuilder.generateToken(client);
        client.setToken(clientToken);

        //default profile pic for user
        byte[] byte1 = "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".getBytes(StandardCharsets.UTF_8);

        File file = new File("blank-profile_picture", "image/png", Base64.getDecoder().decode(byte1));
        fileRepository.save(file);
        client.addFile(file);
        client.setFileID(file.getId());
        //----------------------------------------------------------------------//

        //save client to repository
        Client savedClient = clientRepository.save(client);

        return ResponseEntity.ok().body(savedClient);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> loginClient(@RequestBody Client client) throws ParseException, JOSEException {
        Client clientFound = clientRepository.findClientByEmail(client.getEmail());

        //if client is not found return not found status
        if (clientFound == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        //if passwords do not match return forbidden access
        if (!EncodingPassword.checkIfPwsMatch(client.getPassword(), clientFound.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Passwords do not match");
        }

        clientFound.setActive(true);
        clientFound.setLast_logon(new Date());
        Client savedClient = clientRepository.save(clientFound);

        HttpCookie httpCookie = ResponseCookie
                .from("enc_token", "Bearer+" + savedClient.getToken())
                .path("/")
                .httpOnly(true)
                .secure(false)//use SSL (https:// at the beginning of the URL) if you've got the Secure flag set.
                .domain("localhost")
                .maxAge(jwtBuilder.getExpirationDate(savedClient.getToken()))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, httpCookie.toString());

        return ResponseEntity.ok().headers(headers).body(httpCookie.getValue());
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<?> logoutClient(HttpServletResponse response, @RequestParam("id") String id) {
        Client clientFound = clientRepository.findClientById(id);

        clientFound.setActive(false);

        clientFound.setLast_logout(new Date());

        Client savedClient = clientRepository.save(clientFound);

        Cookie httpCookie = new Cookie("enc_token", "Bearer+");

        httpCookie.setPath("/");
        httpCookie.setHttpOnly(true);
        httpCookie.setSecure(false);//use SSL (https:// at the beginning of the URL) if you've got the Secure flag set.
        httpCookie.setDomain("localhost");
        httpCookie.setMaxAge(0);

        response.addCookie(httpCookie);

        return ResponseEntity.ok().body(savedClient.getActive());
    }

    @GetMapping(value = "/isActive/{id}")
    public ResponseEntity<?> isClientActive(@PathVariable String id) {
        Client clientFound = clientRepository.findClientById(id);

        return ResponseEntity.ok().body(clientFound.getActive());
    }

    @GetMapping(value = "/{id}/status={status}")
    public ResponseEntity<?> changeStatus(@PathVariable String id, @PathVariable("status") Boolean status) {
        Client clientFound = clientRepository.findClientById(id);

        if (!status) {
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

        if (date1 == null) {
            return ResponseEntity.ok().body(0);
        }

        long diff = date2.getTime() - date1.getTime();

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

        if (!jwtBuilder.verifyToken(access_token)) {
            String message = "ACCESS DENIED";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }

        return ResponseEntity.ok().body(claims);
    }

}
