package com.autoparts.sitepecascarro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity 
public class Usuario {
    
    @Id
    private String cpf;

    private String email;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String username;

    public Usuario(){

    }

    public Usuario(String cpf, String email, String senha, Role role, String username) {
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.username = username;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
