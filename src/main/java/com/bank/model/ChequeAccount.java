package com.bank.model;

/**
 * Cheque account: only for employed individual customers. Deposits and
 * withdrawals of any amount up to the available balance. Pays no interest.
 */
public class ChequeAccount extends Account {
    private final String employerName;
    private final String employerAddress;

    public ChequeAccount(String accountNumber, String branch, IndividualCustomer owner,
                         String employerName, String employerAddress) {
        super(accountNumber, branch, owner);
        if (!owner.isEmployed()) {
            throw new IllegalArgumentException("A cheque account can only be opened for an employed customer");
        }
        if (employerName == null || employerName.isBlank()
                || employerAddress == null || employerAddress.isBlank()) {
            throw new IllegalArgumentException("Employer name and address are required");
        }
        this.employerName = employerName.trim();
        this.employerAddress = employerAddress.trim();
    }

    @Override
    public boolean withdraw(double amount) {
        if (!isValidAmount(amount)) return false;
        double rounded = money(amount);
        if (rounded > getBalance()) return false;
        debit(rounded, TransactionType.WITHDRAWAL);
        return true;
    }

    public String getEmployerName() { return employerName; }

    public String getEmployerAddress() { return employerAddress; }

    @Override
    public String getAccountType() { return "Cheque"; }
}