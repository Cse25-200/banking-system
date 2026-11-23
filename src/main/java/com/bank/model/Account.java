package com.bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal balance = money(BigDecimal.ZERO);
    private final String branch;
    private final LocalDate dateOpened;
    private final Customer owner;
    private final List<Transaction> transactions = new ArrayList<>();

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
    public boolean deposit(BigDecimal amount) {
        if (!isValidAmount(amount)) return false;
        credit(amount, TransactionType.DEPOSIT);
        return true;
    }

    /** Overloaded version that also takes a note/reference for the deposit. */
    public boolean deposit(BigDecimal amount, String description) {
        // The description is only a reference for the caller; the stored record
        // is the same as for a normal deposit.
        return deposit(amount);
    }

    /** Each account type decides its own withdrawal rule (overridden in subclasses). */
    @Override
    public abstract boolean withdraw(BigDecimal amount);

    /** Human-readable account type, e.g. "Savings". */
    public abstract String getAccountType();

    @Override
    public BigDecimal getBalance() { return balance; }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }

    public String getAccountNumber() { return accountNumber; }

    public String getBranch() { return branch; }

    public LocalDate getDateOpened() { return dateOpened; }

    public Customer getOwner() { return owner; }

    // ---- protected helpers: the only places the balance can change ----

    protected void setBalance(BigDecimal newBalance) {
        if (newBalance.signum() < 0) {
            throw new IllegalStateException("Balance can never go below zero");
        }
        this.balance = money(newBalance);
    }

    protected void recordTransaction(TransactionType type, BigDecimal amount) {
        transactions.add(new Transaction(type, money(amount), balance));
    }

    /** Adds money, updates the balance immediately and records the transaction. */
    protected Transaction credit(BigDecimal amount, TransactionType type) {
        setBalance(balance.add(amount));
        recordTransaction(type, amount);
        return transactions.get(transactions.size() - 1);
    }

    /** Removes money (never below zero), updates the balance and records the transaction. */
    protected Transaction debit(BigDecimal amount, TransactionType type) {
        setBalance(balance.subtract(amount));
        recordTransaction(type, amount);
        return transactions.get(transactions.size() - 1);
    }

    /** Used when rebuilding an account from stored data (database). */
    protected void restore(BigDecimal storedBalance, List<Transaction> storedTransactions) {
        this.balance = money(storedBalance);
        this.transactions.clear();
        this.transactions.addAll(storedTransactions);
    }

    protected static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.signum() > 0;
    }

    /** Money is always exact and rounded to 2 decimal places (BWP). */
    protected static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return getAccountType() + " account " + accountNumber + " (BWP " + balance + ")";
    }
}