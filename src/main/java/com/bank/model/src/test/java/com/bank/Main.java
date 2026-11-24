package com.bank;

import com.bank.model.Account;
import com.bank.model.BankAdministrator;
import com.bank.model.ChequeAccount;
import com.bank.model.IndividualCustomer;
import com.bank.model.InvestmentAccount;
import com.bank.model.SavingsAccount;
import com.bank.model.Transaction;

/** Console demo of the core model. Replaced by the JavaFX application in Week 5. */
public class Main {
    public static void main(String[] args) {
        // 1. Set up people
        BankAdministrator admin = new BankAdministrator(1, "admin", "admin123", "S001");
        IndividualCustomer thabo = new IndividualCustomer(2, "thabo", "pass123", "C001",
                "Plot 12, Gaborone", "71234567", "thabo@mail.com",
                "Thabo", "Molefe", "123456789", true);

        // 2. Register the customer and log in
        admin.registerCustomer(thabo);
        System.out.println("Login with wrong password: " + thabo.login("thabo", "wrong"));
        System.out.println("Login with right password: " + thabo.login("thabo", "pass123"));

        // 3. Open one account of each type
        SavingsAccount savings = new SavingsAccount("SAV001", "Main", thabo);
        InvestmentAccount investment = new InvestmentAccount("INV001", "Main", thabo, 1000);
        ChequeAccount cheque = new ChequeAccount("CHQ001", "Main", thabo,
                "Acme Ltd", "Plot 5, Gaborone");
        admin.openAccount(thabo, savings);
        admin.openAccount(thabo, investment);
        admin.openAccount(thabo, cheque);

        // 4. Deposits and withdrawals
        savings.deposit(2000);
        cheque.deposit(5000);
        System.out.println("Withdraw 100 from savings allowed? " + savings.withdraw(100));
        System.out.println("Withdraw 1200.50 from cheque allowed? " + cheque.withdraw(1200.50));
        System.out.println("Withdraw 99999 from cheque allowed? " + cheque.withdraw(99999));

        // 5. Monthly interest
        admin.runMonthlyInterest();

        // 6. Show everything
        System.out.println();
        System.out.println(thabo.getDisplayName() + "'s accounts:");
        for (Account a : thabo.getAccounts()) {
            System.out.println("  " + a);
            for (Transaction t : a.getTransactionHistory()) {
                System.out.println("      " + t);
            }
        }
    }
}