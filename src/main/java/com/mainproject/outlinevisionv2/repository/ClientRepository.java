package com.mainproject.outlinevisionv2.repository;

import com.mainproject.outlinevisionv2.entity.Authority;
import com.mainproject.outlinevisionv2.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {
    Client findClientByEmail(String email);
    Client findClientByToken(String token);
    Client findClientById(String id);
}
