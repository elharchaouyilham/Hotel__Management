package model;

import model.enums.UserRole;

import java.util.UUID;

public class User {

    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String ville;
    private UserRole title;

    public User() {
    }

    public User(
            UUID id,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            String ville,
            UserRole title
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.ville = ville;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public UserRole getTitle() {
        return title;
    }

    public void setTitle(UserRole title) {
        this.title = title;
    }
}