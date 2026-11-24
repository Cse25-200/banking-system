package com.bank.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/** One recorded money movement on an account. Immutable once created. */
public class Transaction {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final int transactionId;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime dateTime;
    private final double balanceAfter;

    /** Creates a new transaction stamped with the current date and time. */
    public Transaction(TransactionType type, double amount, double balanceAfter) {
        this(NEXT_ID.getAndIncrement(), type, amount, LocalDateTime.now(), balanceAfter);
    }

    /** Used when rebuilding a transaction that was stored earlier (database). */
    public Transaction(int transactionId, TransactionType type, double amount,
                       LocalDateTime dateTime, double balanceAfter) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.dateTime = dateTime;
        this.balanceAfter = balanceAfter;
        // keep the id counter ahead of any id loaded from storage
        NEXT_ID.accumulateAndGet(transactionId + 1, Math::max);
    }

    public int getTransactionId() { return transactionId; }

    public TransactionType getType() { return type; }

    public double getAmount() { return amount; }

    public LocalDateTime getDateTime() { return dateTime; }

    public double getBalanceAfter() { return balanceAfter; }

    @Override
    public String toString() {
        return String.format("#%d %s BWP %.2f on %s (balance BWP %.2f)",
                transactionId, type, amount, dateTime, balanceAfter);
    }
}