package com.bank.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Anyone who can log in to the system (customers and bank administrators). */
public abstract class User {
    private int userId;
    private final String username;
    private final String passwordHash;
    private final Role role;
    private boolean loggedIn;

    protected User(int userId, String username, String password, Role role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        this.userId = userId;
        this.role = role;
        this.username = username.trim();
        this.passwordHash = hash(password);
    }

    /** Used when loading a user whose password is already stored as a hash. */
    protected User(int userId, String username, String passwordHash, boolean alreadyHashed, Role role) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /** True (and the user is marked logged in) only if both details match. */
    public boolean login(String username, String password) {
        loggedIn = username != null && password != null
                && this.username.equals(username.trim())
                && this.passwordHash.equals(hash(password));
        return loggedIn;
    }

    public void logout() {
         loggedIn = false; }

    public boolean isLoggedIn() { 
        return loggedIn; }

    public int getUserId() {
         return userId; }

    public void setUserId(int userId) {
         this.userId = userId; }

    public String getUsername() { 
        return username; }

    public Role getRole() {
         return role; }

    public String getPasswordHash() { 
        return passwordHash; }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}