package com.bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A bank customer: an individual or a company. Owns (holds) accounts. */
public abstract class Customer extends User {
    private final String customerId;
    private String address;
    private String phoneNumber;
    private String email;
    private final List<Account> accounts = new ArrayList<>();

    protected Customer(int userId, String username, String password, String customerId,
                       String address, String phoneNumber, String email) {
        super(userId, username, password, Role.CUSTOMER);
        this.customerId = requireText(customerId, "Customer ID");
        this.address = requireText(address, "Address");
        this.phoneNumber = requireText(phoneNumber, "Phone number");
        this.email = requireText(email, "Email");
    }

    protected Customer(int userId, String username, String passwordHash, boolean alreadyHashed,
                       String customerId, String address, String phoneNumber, String email) {
        super(userId, username, passwordHash, alreadyHashed, Role.CUSTOMER);
        this.customerId = customerId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /** Adds an account this customer owns. The account must belong to this customer. */
    public void addAccount(Account a) {
        if (a == null) throw new IllegalArgumentException("Account is required");
        if (a.getOwner() != this) {
            throw new IllegalArgumentException("Account belongs to a different customer");
        }
        if (getAccount(a.getAccountNumber()) != null) {
            throw new IllegalArgumentException("Account already added: " + a.getAccountNumber());
        }
        accounts.add(a);
    }

    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }

    /** Returns the account with this number, or null if this customer does not hold it. */
    public Account getAccount(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(accountNumber)) return a;
        }
        return null;
    }

    /** Name to show on screens: full name or company name. */
    public abstract String getDisplayName();

    public String getCustomerId() { return customerId; }

    public String getAddress() { return address; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getEmail() { return email; }

    public void setAddress(String address) { this.address = requireText(address, "Address"); }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = requireText(phoneNumber, "Phone number"); }

    public void setEmail(String email) { this.email = requireText(email, "Email"); }

    protected static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}