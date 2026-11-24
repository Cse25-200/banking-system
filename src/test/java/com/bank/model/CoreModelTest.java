package com.bank.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreModelTest {
    private BankAdministrator admin;
    private IndividualCustomer thabo;

    private static BigDecimal bd(String s) { return new BigDecimal(s); }

    @BeforeEach
    void setUp() {
        admin = new BankAdministrator(1, "admin", "admin123", "S001");
        thabo = new IndividualCustomer(2, "thabo", "pass123", "C001", "Plot 12, Gaborone",
                "71234567", "thabo@mail.com", "Thabo", "Molefe", "123456789", true);
        admin.registerCustomer(thabo);
    }

    @Test
    void loginNeedsCorrectUsernameAndPassword() {
        assertTrue(thabo.login("thabo", "pass123"));
        assertFalse(thabo.login("thabo", "wrong"));
        assertFalse(thabo.isLoggedIn());
        assertFalse(thabo.login("other", "pass123"));
    }

    @Test
    void usersGetTheRightRole() {
        assertEquals(Role.CUSTOMER, thabo.getRole());
        assertEquals(Role.ADMINISTRATOR, admin.getRole());
    }

    @Test
    void passwordIsStoredHashed() {
        assertNotEquals("pass123", thabo.getPasswordHash());
        assertEquals(64, thabo.getPasswordHash().length());
    }

    @Test
    void accountCannotExistWithoutCustomer() {
        assertThrows(IllegalArgumentException.class, () -> new SavingsAccount("S1", "Main", null));
    }

    @Test
    void customerCanHoldAllThreeAccountTypes() {
        admin.openAccount(thabo, new SavingsAccount("SAV1", "Main", thabo));
        admin.openAccount(thabo, new InvestmentAccount("INV1", "Main", thabo, bd("500")));
        admin.openAccount(thabo, new ChequeAccount("CHQ1", "Main", thabo, "Acme", "Plot 1"));
        assertEquals(3, thabo.getAccounts().size());
    }

    @Test
    void cannotOpenAccountForUnregisteredCustomer() {
        IndividualCustomer other = new IndividualCustomer(3, "x", "p", "C002", "a", "1", "e@x.com",
                "A", "B", "1", false);
        assertThrows(IllegalArgumentException.class,
                () -> admin.openAccount(other, new SavingsAccount("SAV9", "Main", other)));
    }

    @Test
    void investmentNeedsAtLeast500() {
        assertThrows(IllegalArgumentException.class,
                () -> new InvestmentAccount("INV2", "Main", thabo, bd("499.99")));
        InvestmentAccount ok = new InvestmentAccount("INV3", "Main", thabo, bd("500.00"));
        assertEquals(bd("500.00"), ok.getBalance());
        assertEquals(1, ok.getTransactionHistory().size());
    }

    @Test
    void chequeOnlyForEmployedIndividuals() {
        IndividualCustomer jobless = new IndividualCustomer(4, "j", "p", "C003", "a", "1", "e@x.com",
                "J", "K", "2", false);
        assertThrows(IllegalArgumentException.class,
                () -> new ChequeAccount("CHQ2", "Main", jobless, "Acme", "Plot 1"));
        assertThrows(IllegalArgumentException.class,
                () -> new ChequeAccount("CHQ3", "Main", thabo, " ", "Plot 1"));
    }

    @Test
    void savingsRejectsAllWithdrawals() {
        SavingsAccount s = new SavingsAccount("SAV2", "Main", thabo);
        s.deposit(bd("1000"));
        assertFalse(s.withdraw(bd("1")));
        assertEquals(bd("1000.00"), s.getBalance());
    }

    @Test
    void withdrawalUpToBalanceOnlyAndNeverNegative() {
        ChequeAccount c = new ChequeAccount("CHQ4", "Main", thabo, "Acme", "Plot 1");
        c.deposit(bd("300"));
        assertFalse(c.withdraw(bd("300.01")));
        assertTrue(c.withdraw(bd("300")));
        assertEquals(bd("0.00"), c.getBalance());
        assertFalse(c.withdraw(bd("0.01")));
    }

    @Test
    void invalidDepositsAreRejected() {
        SavingsAccount s = new SavingsAccount("SAV3", "Main", thabo);
        assertFalse(s.deposit(bd("0")));
        assertFalse(s.deposit(bd("-5")));
        assertFalse(s.deposit(null));
        assertTrue(s.getTransactionHistory().isEmpty());
    }

    @Test
    void everyTransactionIsRecordedWithResultingBalance() {
        ChequeAccount c = new ChequeAccount("CHQ5", "Main", thabo, "Acme", "Plot 1");
        c.deposit(bd("100"));
        c.withdraw(bd("40.50"));
        var h = c.getTransactionHistory();
        assertEquals(2, h.size());
        assertEquals(TransactionType.DEPOSIT, h.get(0).getType());
        assertEquals(bd("100.00"), h.get(0).getBalanceAfter());
        assertEquals(TransactionType.WITHDRAWAL, h.get(1).getType());
        assertEquals(bd("59.50"), h.get(1).getBalanceAfter());
        assertNotNull(h.get(1).getDateTime());
    }

    @Test
    void overloadedDepositWorks() {
        SavingsAccount s = new SavingsAccount("SAV4", "Main", thabo);
        assertTrue(s.deposit(bd("50"), "Birthday money"));
        assertEquals(bd("50.00"), s.getBalance());
    }

    @Test
    void monthlyInterestRatesAndRecording() {
        SavingsAccount s = new SavingsAccount("SAV5", "Main", thabo);
        s.deposit(bd("1000"));
        InvestmentAccount i = new InvestmentAccount("INV4", "Main", thabo, bd("1000"));
        assertEquals(bd("0.50"), s.calculateInterest());   // 0.05%
        assertEquals(bd("50.00"), i.calculateInterest());  // 5%

        Transaction t = i.payInterest();
        assertEquals(TransactionType.INTEREST, t.getType());
        assertEquals(bd("1050.00"), i.getBalance());
        assertEquals(bd("1050.00"), t.getBalanceAfter());
    }

    @Test
    void interestRoundsToTwoDecimalPlaces() {
        SavingsAccount s = new SavingsAccount("SAV6", "Main", thabo);
        s.deposit(bd("123.45"));                           // 0.0617 -> 0.06
        assertEquals(bd("0.06"), s.calculateInterest());
    }

    @Test
    void emptySavingsPaysNoInterest() {
        SavingsAccount s = new SavingsAccount("SAV7", "Main", thabo);
        assertNull(s.payInterest());
        assertTrue(s.getTransactionHistory().isEmpty());
    }

    @Test
    void adminInterestRunSkipsChequeAccounts() {
        SavingsAccount s = new SavingsAccount("SAV8", "Main", thabo);
        ChequeAccount c = new ChequeAccount("CHQ6", "Main", thabo, "Acme", "Plot 1");
        admin.openAccount(thabo, s);
        admin.openAccount(thabo, c);
        s.deposit(bd("2000"));
        c.deposit(bd("2000"));
        admin.runMonthlyInterest();
        assertEquals(bd("2001.00"), s.getBalance());
        assertEquals(bd("2000.00"), c.getBalance());
    }

    @Test
    void polymorphicWithdrawBehavesPerAccountType() {
        Account savings = new SavingsAccount("SAV10", "Main", thabo);
        Account cheque = new ChequeAccount("CHQ10", "Main", thabo, "Acme", "Plot 1");
        savings.deposit(bd("100"));
        cheque.deposit(bd("100"));
        assertFalse(savings.withdraw(bd("10")));
        assertTrue(cheque.withdraw(bd("10")));
    }

    @Test
    void companyCustomerDisplayName() {
        CompanyCustomer co = new CompanyCustomer(5, "acme", "p", "C010", "Plot 5", "3900000",
                "info@acme.co.bw", "Acme Ltd", "BW000123");
        assertEquals("Acme Ltd", co.getDisplayName());
        assertEquals("Thabo Molefe", thabo.getDisplayName());
    }
}