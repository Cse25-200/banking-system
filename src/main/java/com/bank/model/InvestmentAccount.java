package com.bank.model;

import java.math.BigDecimal;

/**
 * Investment account: opened with at least BWP 500.00, allows deposits and
 * withdrawals up to the available balance, pays 5% monthly interest.
 */
public class InvestmentAccount extends Account implements InterestBearing {
    public static final BigDecimal INTEREST_RATE = new BigDecimal("0.05");
    public static final BigDecimal MIN_OPENING_DEPOSIT = new BigDecimal("500.00");

    public InvestmentAccount(String accountNumber, String branch, Customer owner,
                             BigDecimal openingDeposit) {
        super(accountNumber, branch, owner);
        if (openingDeposit == null || openingDeposit.compareTo(MIN_OPENING_DEPOSIT) < 0) {
            throw new IllegalArgumentException(
                    "An investment account needs an opening deposit of at least BWP 500.00");
        }
        deposit(openingDeposit);
    }

    /** Allowed up to the available balance; the balance can never go below zero. */
    @Override
    public boolean withdraw(BigDecimal amount) {
        if (!isValidAmount(amount) || amount.compareTo(getBalance()) > 0) return false;
        debit(amount, TransactionType.WITHDRAWAL);
        return true;
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
    public String getAccountType() { return "Investment"; }
}