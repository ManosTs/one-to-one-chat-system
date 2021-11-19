package com.mainproject.outlinevisionv2.entity;

import org.springframework.web.bind.annotation.CrossOrigin;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "http://192.168.1.2:4200")
@Entity
@Table(name = "files")
public class File{
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "data")
    @Lob
    private byte[] data;

    @OneToMany(mappedBy = "file")
    private Set<Client> clients = new HashSet<>();

    public File(){
    }

       
    public File(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }
}
