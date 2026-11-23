package com.bank.model;

import java.math.BigDecimal;

/** Implemented by accounts that earn monthly interest (Savings and Investment). */
public interface InterestBearing {
    /** Monthly interest rate as a fraction, e.g. 0.05 = 5%. */
    BigDecimal getInterestRate();

    /** Interest due on the current balance, rounded to 2 decimal places. */
    BigDecimal calculateInterest();

    /**
     * Credits the interest to the account and records it as an INTEREST transaction.
     * Returns the transaction, or null if there was no interest to pay.
     */
    Transaction payInterest();
}