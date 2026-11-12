package com.library.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * User — a login account for the system (ADMIN or LIBRARIAN).
 * Passwords are never stored in plain text: only the SHA-256 hash
 * is kept (see hashPassword / verifyPassword).
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ADMIN = "ADMIN";
    public static final String LIBRARIAN = "LIBRARIAN";

    private int id;
    private String name;          // display name (defaults to username)
    private String username;
    private String passwordHash;  // SHA-256 hex, never the plain password
    private String role;          // ADMIN / LIBRARIAN

    public User() {
    }

    /** Stores the SHA-256 hash of the plain password. */
    public User(String username, String plainPassword, String role) {
        this(0, username, username, plainPassword, role);
    }

    /** Full constructor used by the GUI sample data. */
    public User(int id, String name, String username,
                String plainPassword, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = hashPassword(plainPassword);
        this.role = role;
    }

    /** SHA-256 hash as a lower-case hex string. */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(
                    plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed in every JDK — this cannot happen.
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /** True when the plain password matches the stored hash. */
    public boolean verifyPassword(String plainPassword) {
        return Objects.equals(passwordHash, hashPassword(plainPassword));
    }

    public boolean isAdmin() {
        return ADMIN.equalsIgnoreCase(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User other = (User) o;
        return Objects.equals(username, other.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        // Never print the password hash.
        return "User{id=" + id + ", name='" + name + "', username='"
                + username + "', role='" + role + "'}";
    }

    // ----- getters / setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    /** Sets the password from plain text (hashes it). */
    public void setPassword(String plainPassword) {
        this.passwordHash = hashPassword(plainPassword);
    }
    /** Sets an already-hashed value (used when loading from file). */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
