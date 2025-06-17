package com.bank.DigitalBank.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.util.Set;

@Entity

@Table(name = "UserDetails")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "email",unique = true,nullable = false)
    private String email;

// added as jsonproerty was causing null values
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    @Column(name = "password",nullable = false)
    private String password;


    @Column(name = "roles", columnDefinition = "varchar(255) default 'USER'")
    private String roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public User(Long id, String name, String email, String password, String roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User() {
    }
}
