package com.mainproject.outlinevisionv2.repository;

import com.mainproject.outlinevisionv2.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends JpaRepository<Authority,Long> {
    Authority findAuthorityByName(String name);
}
