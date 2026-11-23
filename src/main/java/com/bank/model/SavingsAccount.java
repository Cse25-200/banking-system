package com.bank.model;

import java.math.BigDecimal;

/** Savings account: deposits only, no withdrawals, pays 0.05% monthly interest. */
public class SavingsAccount extends Account implements InterestBearing {
    public static final BigDecimal INTEREST_RATE = new BigDecimal("0.0005");

    public SavingsAccount(String accountNumber, String branch, Customer owner) {
        super(accountNumber, branch, owner);
    }

    /** Savings accounts never allow withdrawals. */
    @Override
    public boolean withdraw(BigDecimal amount) {
        return false;
    }

    @Override
    public BigDecimal getInterestRate() { return INTEREST_RATE; }

    @Override
    public BigDecimal calculateInterest() {
        return money(getBalance().multiply(INTEREST_RATE));
    }

    @Override
    public Transaction payInterest() {
        BigDecimal interest = calculateInterest();
        if (interest.signum() <= 0) return null;
        return credit(interest, TransactionType.INTEREST);
    }

    @Override
    public String getAccountType() { return "Savings"; }
}