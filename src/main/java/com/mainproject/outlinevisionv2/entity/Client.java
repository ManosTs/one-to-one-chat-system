package com.mainproject.outlinevisionv2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.type.BlobType;

import javax.persistence.*;
import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {

    //properties
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "token", nullable = false,length = 1000)
    private String token;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(
            name = "clients_authority",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities = new HashSet<>();

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(
            name = "clients_profile_pictures",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private File file = new File();

    //---------------------------------------------------------------------//

    //setters
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void addAuthority(Authority authority){
        authorities.add(authority);
    }

    public void addFile(File file){
        this.file = file;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    //-----------------------------------------//

    //getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }
}
