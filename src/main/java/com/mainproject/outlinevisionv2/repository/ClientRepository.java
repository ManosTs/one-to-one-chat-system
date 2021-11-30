package com.mainproject.outlinevisionv2.repository;

import com.mainproject.outlinevisionv2.entity.Client;
import com.nimbusds.jwt.EncryptedJWT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client,String> {
    Client findClientByEmail(String email);

    Client findClientByToken(String token);

    Client findClientById(String id);

    @Query("SELECT firstName, lastName FROM Client")
    List<Object[]> findAllGroupByFirstName();
}
