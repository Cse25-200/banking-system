package com.bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Bank staff member: registers customers, opens accounts and runs monthly interest. */
public class BankAdministrator extends User {
    private final String staffId;
    private final List<Customer> customers = new ArrayList<>();

    public BankAdministrator(int userId, String username, String password, String staffId) {
        super(userId, username, password, Role.ADMINISTRATOR);
        if (staffId == null || staffId.isBlank()) {
            throw new IllegalArgumentException("Staff ID is required");
        }
        this.staffId = staffId.trim();
    }

    public void registerCustomer(Customer c) {
        if (c == null) throw new IllegalArgumentException("Customer is required");
        for (Customer existing : customers) {
            if (existing.getCustomerId().equals(c.getCustomerId())
                    || existing.getUsername().equals(c.getUsername())) {
                throw new IllegalArgumentException("Customer already registered: " + c.getCustomerId());
            }
        }
        customers.add(c);
    }

    /** Opens an account for a registered customer. */
    public void openAccount(Customer c, Account a) {
        if (c == null || !customers.contains(c)) {
            throw new IllegalArgumentException("Customer must be registered first");
        }
        c.addAccount(a);
    }

    /** Pays interest on every interest-bearing account of every registered customer. */
    public void runMonthlyInterest() {
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a instanceof InterestBearing ib) {
                    ib.payInterest();
                }
            }
        }
    }

    public String getStaffId() { return staffId; }

    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }
}