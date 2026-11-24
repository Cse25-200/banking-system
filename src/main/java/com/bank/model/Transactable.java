package com.bank.model;

/** Anything that can receive deposits and (maybe) allow withdrawals. */
public interface Transactable {
    boolean deposit(double amount);

    boolean withdraw(double amount);

    double getBalance();
}