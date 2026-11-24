package com.bank.model;

public class InvestmentAccount extends Account
        implements InterestBearing {

    public static final double INTEREST_RATE = 0.05;
    public static final double MIN_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, String branch,
                             Customer owner, double openingDeposit) {

        super(accountNumber, branch, owner);

        if (!Double.isFinite(openingDeposit)
                || openingDeposit < MIN_OPENING_DEPOSIT) {

            throw new IllegalArgumentException(
                    "An investment account needs an opening deposit "
                    + "of at least BWP 500.00");
        }

        deposit(openingDeposit);
    }

    @Override
    public boolean withdraw(double amount) {

        if (!isValidAmount(amount)) {
            return false;
        }

        double rounded = money(amount);

        if (rounded > getBalance()) {
            return false;
        }

        debit(rounded, TransactionType.WITHDRAWAL);

        return true;
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }

    @Override
    public double calculateInterest() {
        return money(getBalance() * INTEREST_RATE);
    }

    @Override
    public Transaction payInterest() {

        double interest = calculateInterest();

        if (interest <= 0) {
            return null;
        }

        return credit(interest, TransactionType.INTEREST);
    }

    @Override
    public String getAccountType() {
        return "Investment";
    }
}