package com.mainproject.outlinevisionv2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Column(name = "file_id", nullable = true)
    private String fileID;

    @Column(name = "active_status", nullable = false)
    private Boolean isActive = false;

    @Column(name= "last_logout", nullable = true)
    private Date last_logout;

    @Column(name= "last_logon", nullable = true)
    private Date last_logon ;


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

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setLast_logout(Date last_logout) {
        this.last_logout = last_logout;
    }

    public void setLast_logon(Date last_logon) {
        this.last_logon = last_logon;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
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

    public Boolean getActive() {
        return isActive;
    }

    public Date getLast_logout() {
        return last_logout;
    }

    public Date getLast_logon() {
        return last_logon;
    }

    public String getFileID() {
        return fileID;
    }
}
