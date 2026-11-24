package com.bank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all accounts. An account cannot exist without an owning customer.
 * Fields are private (encapsulation); subclasses change the balance only through
 * the protected helper methods so every change is recorded as a transaction.
 */
public abstract class Account implements Transactable {
    private final String accountNumber;
    private double balance = 0.00;
    private final String branch;
    private final LocalDate dateOpened;
    private final Customer owner;
    private final List<Transaction> transactions = new ArrayList<>();
    private int nextTransactionId = 1;

    protected Account(String accountNumber, String branch, Customer owner) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (branch == null || branch.isBlank()) {
            throw new IllegalArgumentException("Branch is required");
        }
        if (owner == null) {
            throw new IllegalArgumentException("An account must have an owning customer");
        }
        this.accountNumber = accountNumber.trim();
        this.branch = branch.trim();
        this.owner = owner;
        this.dateOpened = LocalDate.now();
    }

    /** Deposits a positive amount. Returns false (and changes nothing) if the amount is invalid. */
    @Override
    public boolean deposit(double amount) {
        if (!isValidAmount(amount)) return false;
        credit(money(amount), TransactionType.DEPOSIT);
        return true;
    }

    /** Overloaded version that also takes a note/reference for the deposit. */
    public boolean deposit(double amount, String description) {
        // The description is only a reference for the caller; the stored record
        // is the same as for a normal deposit.
        return deposit(amount);
    }

    /** Each account type decides its own withdrawal rule (overridden in subclasses). */
    @Override
    public abstract boolean withdraw(double amount);

    /** Human-readable account type, e.g. "Savings". */
    public abstract String getAccountType();

    @Override
    public double getBalance() { return balance; }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }

    public String getAccountNumber() { return accountNumber; }

    public String getBranch() { return branch; }

    public LocalDate getDateOpened() { return dateOpened; }

    public Customer getOwner() { return owner; }

    // ---- protected helpers: the only places the balance can change ----

    protected void setBalance(double newBalance) {
        double rounded = money(newBalance);
        if (rounded < 0) {
            throw new IllegalStateException("Balance can never go below zero");
        }
        this.balance = rounded;
    }

    protected void recordTransaction(TransactionType type, double amount) {
        transactions.add(new Transaction(nextTransactionId++, type, money(amount), balance));
    }

    /** Adds money, updates the balance immediately and records the transaction. */
    protected Transaction credit(double amount, TransactionType type) {
        setBalance(balance + amount);
        recordTransaction(type, amount);
        return transactions.get(transactions.size() - 1);
    }

    /** Removes money (never below zero), updates the balance and records the transaction. */
    protected Transaction debit(double amount, TransactionType type) {
        setBalance(balance - amount);
        recordTransaction(type, amount);
        return transactions.get(transactions.size() - 1);
    }

    /** Used when rebuilding an account from stored data (database). */
    protected void restore(double storedBalance, List<Transaction> storedTransactions) {
        this.balance = money(storedBalance);
        this.transactions.clear();
        this.transactions.addAll(storedTransactions);
        this.nextTransactionId = storedTransactions.size() + 1;
    }

    /** A valid amount is a real number that is still above zero after rounding to 2 decimals. */
    protected static boolean isValidAmount(double amount) {
        return !Double.isNaN(amount) && !Double.isInfinite(amount) && money(amount) > 0;
    }

    /** Money is always rounded to 2 decimal places (BWP). */
    protected static double money(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s account %s (BWP %.2f)", getAccountType(), accountNumber, balance);
    }
}