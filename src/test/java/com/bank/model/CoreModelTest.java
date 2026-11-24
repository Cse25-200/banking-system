package com.bank.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreModelTest {
    private BankAdministrator admin;
    private IndividualCustomer thabo;

    private static final double EPS = 0.001;

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
        admin.openAccount(thabo, new InvestmentAccount("INV1", "Main", thabo, 500));
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
                () -> new InvestmentAccount("INV2", "Main", thabo, 499.99));
        InvestmentAccount ok = new InvestmentAccount("INV3", "Main", thabo, 500.00);
        assertEquals(500.00, ok.getBalance(), EPS);
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
        s.deposit(1000);
        assertFalse(s.withdraw(1));
        assertEquals(1000.00, s.getBalance(), EPS);
    }

    @Test
    void withdrawalUpToBalanceOnlyAndNeverNegative() {
        ChequeAccount c = new ChequeAccount("CHQ4", "Main", thabo, "Acme", "Plot 1");
        c.deposit(300);
        assertFalse(c.withdraw(300.01));
        assertTrue(c.withdraw(300));
        assertEquals(0.00, c.getBalance(), EPS);
        assertFalse(c.withdraw(0.01));
    }

    @Test
    void invalidDepositsAreRejected() {
        SavingsAccount s = new SavingsAccount("SAV3", "Main", thabo);
        assertFalse(s.deposit(0));
        assertFalse(s.deposit(-5));
        assertFalse(s.deposit(Double.NaN));
        assertTrue(s.getTransactionHistory().isEmpty());
    }

    @Test
    void everyTransactionIsRecordedWithResultingBalance() {
        ChequeAccount c = new ChequeAccount("CHQ5", "Main", thabo, "Acme", "Plot 1");
        c.deposit(100);
        c.withdraw(40.50);
        var h = c.getTransactionHistory();
        assertEquals(2, h.size());
        assertEquals(TransactionType.DEPOSIT, h.get(0).getType());
        assertEquals(100.00, h.get(0).getBalanceAfter(), EPS);
        assertEquals(TransactionType.WITHDRAWAL, h.get(1).getType());
        assertEquals(59.50, h.get(1).getBalanceAfter(), EPS);
        assertNotNull(h.get(1).getDateTime());
    }

    @Test
    void overloadedDepositWorks() {
        SavingsAccount s = new SavingsAccount("SAV4", "Main", thabo);
        assertTrue(s.deposit(50, "Birthday money"));
        assertEquals(50.00, s.getBalance(), EPS);
    }

    @Test
    void monthlyInterestRatesAndRecording() {
        SavingsAccount s = new SavingsAccount("SAV5", "Main", thabo);
        s.deposit(1000);
        InvestmentAccount i = new InvestmentAccount("INV4", "Main", thabo, 1000);
        assertEquals(0.50, s.calculateInterest(), EPS);   // 0.05%
        assertEquals(50.00, i.calculateInterest(), EPS);  // 5%

        Transaction t = i.payInterest();
        assertEquals(TransactionType.INTEREST, t.getType());
        assertEquals(1050.00, i.getBalance(), EPS);
        assertEquals(1050.00, t.getBalanceAfter(), EPS);
    }

    @Test
    void interestRoundsToTwoDecimalPlaces() {
        SavingsAccount s = new SavingsAccount("SAV6", "Main", thabo);
        s.deposit(123.45);                           // 0.0617 -> 0.06
        assertEquals(0.06, s.calculateInterest(), EPS);
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
        s.deposit(2000);
        c.deposit(2000);
        admin.runMonthlyInterest();
        assertEquals(2001.00, s.getBalance(), EPS);
        assertEquals(2000.00, c.getBalance(), EPS);
    }

    @Test
    void floatingPointErrorsAreRoundedAway() {
        SavingsAccount s = new SavingsAccount("SAV11", "Main", thabo);
        s.deposit(0.1);
        s.deposit(0.2);
        assertEquals(0.30, s.getBalance(), EPS);
        assertEquals(0.3, s.getBalance(), 0.0);   // exactly 0.3, not 0.30000000000000004
    }

    @Test
    void polymorphicWithdrawBehavesPerAccountType() {
        Account savings = new SavingsAccount("SAV10", "Main", thabo);
        Account cheque = new ChequeAccount("CHQ10", "Main", thabo, "Acme", "Plot 1");
        savings.deposit(100);
        cheque.deposit(100);
        assertFalse(savings.withdraw(10));
        assertTrue(cheque.withdraw(10));
    }

    @Test
    void companyCustomerDisplayName() {
        CompanyCustomer co = new CompanyCustomer(5, "acme", "p", "C010", "Plot 5", "3900000",
                "info@acme.co.bw", "Acme Ltd", "BW000123");
        assertEquals("Acme Ltd", co.getDisplayName());
        assertEquals("Thabo Molefe", thabo.getDisplayName());
    }
}