package com.mainproject.outlinevisionv2.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authorities")
public class Authority implements GrantedAuthority  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Set<Client> userRoles = new HashSet<>();

    //constructor
    public Authority(String name) {
        this.name = name;
    }

    public Authority() {

    }
    //setters
    public void setId(Long id) {
        this.id = id;
    }

    //------------------------------------//

    //getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getAuthority() {
        return this.getName();
    }
    //---------------------------.//
}
