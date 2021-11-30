package com.mainproject.outlinevisionv2.security.jwtSecuritiy;

import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.*;

import java.security.interfaces.*;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;


@Component
public class JWTBuilder {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private final Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60000).toInstant());

    private final String SECRET_KEY;

    public JWTBuilder(@Value("${jwt.secret}") String key) throws NoSuchAlgorithmException {
        this.SECRET_KEY = key;
    }

    //public and private key generators
    private final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    private final KeyPair pair = keyGen.generateKeyPair();

    private final RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
    private final RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
    //---------------------------------------------------------------------------//

    // Request JWT encrypted with RSA-OAEP-256 and 128-bit AES/GCM
    private final JWEHeader header = new JWEHeader(
            JWEAlgorithm.RSA_OAEP_256,
            EncryptionMethod.A128GCM
    );

    private String buildEncryptedToken(Client client) throws JOSEException, ParseException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        JWTClaimsSet jwtClaims = new JWTClaimsSet
                .Builder()
                .issuer(".outline-vision.com")
                .jwtID(UUID.randomUUID().toString())
                //----------------claims------------------------//
                .claim("client_id", client.getId())
                .claim("first_name", client.getFirstName())
                .claim("last_name", client.getLastName())
                .claim("authority", client.getAuthorities())
                //------------------------------------------------//
                .subject(client.getEmail())
                .expirationTime(expirationDate)
                .build();

        // Create the encrypted JWT object
        EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

        // Create a rsaEncrypter with the specified public RSA key
        RSAEncrypter rsaEncrypter = new RSAEncrypter(publicKey);

        // Do the actual encryption
        jwt.encrypt(rsaEncrypter);

        String jwtString = jwt.serialize();

        //-------------------------------------------------------------------------------//

        //save client's public key to file
        try (FileOutputStream fos = new FileOutputStream(client.getId()+ "-public.key")) {
            fos.write(publicKey.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //------------------------------------------------------------------//

        return jwtString; //returns the encrypted token
    }

    public JWTClaimsSet decodeToken (String jwtString) throws ParseException, JOSEException {
        EncryptedJWT jwt = EncryptedJWT.parse(jwtString);
        // Create a rsaDecrypter with the specified private RSA key
        RSADecrypter rsaDecrypter = new RSADecrypter(privateKey);

        // Decrypt
        jwt.decrypt(rsaDecrypter);

        return jwt.getJWTClaimsSet();
    }

    public String generateToken(Client client) throws ParseException, NoSuchAlgorithmException, JOSEException, IOException, InvalidKeySpecException {
        return buildEncryptedToken(client);
    }
    //--------------------------------------------------------------//

    //check if token has expired
    private boolean isTokenExpired(String token) throws ParseException, JOSEException {
        //get the right claims from Jws body
        JWTClaimsSet claim = decodeToken(token);
        //return true if Jwt token is expired
        return claim.getExpirationTime()
                .before(new Date());
    }
    //----------------------------------------------------------//

    //return true if client email equals to jwt's subject and token is not expired yet
    public boolean verifyToken(String token) throws ParseException, JOSEException {
        Client client = clientRepository.findClientByToken(token);
        //get the right claims from Jws body
        JWTClaimsSet claimsJws = decodeToken(token);

        return (
                (
                        client.getEmail()
                                .equals(claimsJws.getSubject())
                ) &&
                        !isTokenExpired(token)
        );
    }

    //-----------------------------------------------------------------------------------------//
    public String getEmailFromToken(String token) throws ParseException, JOSEException {
        JWTClaimsSet claimsJws = decodeToken(token);
        return claimsJws.getSubject();
    }
}

