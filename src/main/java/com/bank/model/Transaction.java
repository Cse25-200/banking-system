package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/** One recorded money movement on an account. Immutable once created. */
public class Transaction {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final int transactionId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime dateTime;
    private final BigDecimal balanceAfter;

    /** Creates a new transaction stamped with the current date and time. */
    public Transaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
        this(NEXT_ID.getAndIncrement(), type, amount, LocalDateTime.now(), balanceAfter);
    }

    /** Used when rebuilding a transaction that was stored earlier (database). */
    public Transaction(int transactionId, TransactionType type, BigDecimal amount,
                       LocalDateTime dateTime, BigDecimal balanceAfter) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.dateTime = dateTime;
        this.balanceAfter = balanceAfter;
        NEXT_ID.accumulateAndGet(transactionId + 1, Math::max);
    }

    public int getTransactionId() { return transactionId; }

    public TransactionType getType() { return type; }

    public BigDecimal getAmount() { return amount; }

    public LocalDateTime getDateTime() { return dateTime; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }

    @Override
    public String toString() {
        return String.format("#%d %s BWP %s on %s (balance BWP %s)",
                transactionId, type, amount, dateTime, balanceAfter);
    }
}