package com.bank.model;

/** A customer who is a person. May be employed (needed for a cheque account). */
public class IndividualCustomer extends Customer {
    private final String firstName;
    private final String surname;
    private final String idNumber;
    private boolean employed;

    public IndividualCustomer(int userId, String username, String password, String customerId,
                              String address, String phoneNumber, String email,
                              String firstName, String surname, String idNumber, boolean employed) {
        super(userId, username, password, customerId, address, phoneNumber, email);
        this.firstName = requireText(firstName, "First name");
        this.surname = requireText(surname, "Surname");
        this.idNumber = requireText(idNumber, "ID number");
        this.employed = employed;
    }

    public IndividualCustomer(int userId, String username, String passwordHash, boolean alreadyHashed,
                              String customerId, String address, String phoneNumber, String email,
                              String firstName, String surname, String idNumber, boolean employed) {
        super(userId, username, passwordHash, alreadyHashed, customerId, address, phoneNumber, email);
        this.firstName = firstName;
        this.surname = surname;
        this.idNumber = idNumber;
        this.employed = employed;
    }

    public String getFirstName() { 
        return firstName; }

    public String getSurname() { 
        return surname; }

    public String getIdNumber() { 
        return idNumber; }

    public boolean isEmployed() { 
        return employed; }

    public void setEmployed(boolean employed) {
         this.employed = employed; }

    @Override
    public String getDisplayName() { 
        return firstName + " " + surname; }
}