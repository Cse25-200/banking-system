package com.bank.model;

/** A customer that is a company or organisation. */
public class CompanyCustomer extends Customer {
    private final String companyName;
    private final String registrationNumber;

    public CompanyCustomer(int userId, String username, String password, String customerId,
                           String address, String phoneNumber, String email,
                           String companyName, String registrationNumber) {
        super(userId, username, password, customerId, address, phoneNumber, email);
        this.companyName = requireText(companyName, "Company name");
        this.registrationNumber = requireText(registrationNumber, "Registration number");
    }

    public CompanyCustomer(int userId, String username, String passwordHash, boolean alreadyHashed,
                           String customerId, String address, String phoneNumber, String email,
                           String companyName, String registrationNumber) {
        super(userId, username, passwordHash, alreadyHashed, customerId, address, phoneNumber, email);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
    }

    public String getCompanyName() { return companyName; }

    public String getRegistrationNumber() { return registrationNumber; }

    @Override
    public String getDisplayName() { return companyName; }
}