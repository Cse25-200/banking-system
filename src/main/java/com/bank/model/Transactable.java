package com.bank.model;

import java.math.BigDecimal;

/** Anything that can receive deposits and (maybe) allow withdrawals. */
public interface Transactable {
    boolean deposit(BigDecimal amount);

    boolean withdraw(BigDecimal amount);

    BigDecimal getBalance();
}