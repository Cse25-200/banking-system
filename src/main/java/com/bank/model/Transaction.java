package com.bank.model;

import java.time.LocalDateTime;

/**
 * Transaction
 */
public class Transaction {

    private final int transactionId;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime dateTime;
    private final double balanceAfter;

    public Transaction(int transactionId, TransactionType type,
                       double amount, double balanceAfter) {

        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        return transactionId + " | " + type
                + " | BWP " + String.format("%.2f", amount)
                + " | " + dateTime
                + " | Balance: BWP "
                + String.format("%.2f", balanceAfter);
    }
}