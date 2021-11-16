package com.mainproject.outlinevisionv2.security.jwtSecuritiy;

import com.mainproject.outlinevisionv2.entity.Authority;
import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import io.jsonwebtoken.*;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;


@Component
public class JWTBuilder {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private final Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60000).toInstant());

    private final String SECRET_KEY;



    public JWTBuilder(@Value("${jwt.secret}") String key){
        this.SECRET_KEY = key;

    }

    //build and generate token
    private String buildToken(Client client) {
        return Jwts
                .builder()
                .setIssuer("outline-vision")
                .setSubject(client.getEmail())
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .claim("client_id",client.getId())
                .claim("first_name", client.getFirstName())
                .claim("last_name", client.getLastName())
                .claim("profile_picture",client.getFile().getId())
                .claim("authority", client.getAuthorities())
                .compact();
    }

    public String generateToken(Client client) {
        return buildToken(client);
    }
    //--------------------------------------------------------------//

    //check if token has expired
    private boolean isTokenExpired(String token) {
        //get the right claims from Jws body
        Jws<Claims> claimsJws = Jwts
                .parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
        //return true if Jwt token is expired
        return claimsJws
                .getBody()
                .getExpiration()
                .before(new Date());
    }
    //----------------------------------------------------------//

    //return true if client email equals to jwt's subject and token is not expired yet
    public boolean verifyToken(String token) {
        Client client = clientRepository.findClientByToken(token);

        //get the right claims from Jws body
        Jws<Claims> claimsJws = Jwts
                .parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);

        return (
                (
                        client.getEmail()
                                .equals(claimsJws.getBody().getSubject())
                )&&
                        !isTokenExpired(token)
        );
    }
    //-----------------------------------------------------------------------------------------//
    public  String getEmailFromToken(String token){
        Jws<Claims> claimsJws = Jwts
                .parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }
}
