package com.bank.model;

/** Savings account: deposits only, no withdrawals, pays 0.05% monthly interest. */
public class SavingsAccount extends Account implements InterestBearing {
    public static final double INTEREST_RATE = 0.0005;

    public SavingsAccount(String accountNumber, String branch, Customer owner) {
        super(accountNumber, branch, owner);
    }

    /** Savings accounts never allow withdrawals. */
    @Override
    public boolean withdraw(double amount) {
        return false;
    }

    @Override
    public double getInterestRate() { return INTEREST_RATE; }

    @Override
    public double calculateInterest() {
        return money(getBalance() * INTEREST_RATE);
    }

    @Override
    public Transaction payInterest() {
        double interest = calculateInterest();
        if (interest <= 0) return null;
        return credit(interest, TransactionType.INTEREST);
    }

    @Override
    public String getAccountType() { return "Savings"; }
}