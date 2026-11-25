package com.bank;

import com.bank.model.Account;
import com.bank.model.BankAdministrator;
import com.bank.model.ChequeAccount;
import com.bank.model.CompanyCustomer;
import com.bank.model.Customer;
import com.bank.model.IndividualCustomer;
import com.bank.model.InvestmentAccount;
import com.bank.model.SavingsAccount;
import com.bank.model.User;
import com.bank.view.AccountView;
import com.bank.view.AdminView;
import com.bank.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Entry point of the JavaFX application. */
public class BankApp extends Application {

    // TEMPORARY sample data (replaced by database + controllers in Weeks 6-7)
    private final BankAdministrator admin =
            new BankAdministrator(1, "admin", "admin123", "S001");
    private final IndividualCustomer thabo = new IndividualCustomer(2, "thabo", "pass123", "C001",
            "Plot 12, Gaborone", "71234567", "thabo@mail.com",
            "Thabo", "Molefe", "123456789", true);
    private int nextUserId = 10;

    @Override
    public void start(Stage stage) {
        admin.registerCustomer(thabo);
        SavingsAccount savings = new SavingsAccount("SAV001", "Main", thabo);
        admin.openAccount(thabo, savings);
        admin.openAccount(thabo, new InvestmentAccount("INV001", "Main", thabo, 1000));
        admin.openAccount(thabo, new ChequeAccount("CHQ001", "Main", thabo,
                "Acme Ltd", "Plot 5, Gaborone"));
        savings.deposit(2000);

        LoginView loginView = new LoginView();
        loginView.setOnLogin(() -> {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            User user = null;
            if (admin.login(username, password)) {
                user = admin;
            } else {
                for (Customer c : admin.getCustomers()) {
                    if (c.login(username, password)) {
                        user = c;
                        break;
                    }
                }
            }

            if (user == null) {
                loginView.showError("Wrong username or password");
            } else if (user instanceof Customer customer) {
                showAccountScreen(stage, loginView, customer);
            } else {
                showAdminScreen(stage, loginView);
            }
        });

        stage.setScene(new Scene(loginView.getRoot(), 400, 350));
        stage.setTitle("Banking System");
        stage.show();
    }

    // ---------------- customer screen ----------------

    private void showAccountScreen(Stage stage, LoginView loginView, Customer customer) {
        AccountView view = new AccountView();
        view.setWelcome("Welcome, " + customer.getDisplayName());
        view.setAccounts(customer.getAccounts());
        view.showAccount(view.getSelectedAccount());

        view.setOnAccountSelected(() -> {
            view.showAccount(view.getSelectedAccount());
            view.showMessage("", false);
        });
        view.setOnDeposit(() -> handleMoney(view, true));
        view.setOnWithdraw(() -> handleMoney(view, false));
        view.setOnLogout(() -> {
            customer.logout();
            backToLogin(stage, loginView);
        });

        stage.getScene().setRoot(view.getRoot());
        resize(stage, 820, 560);
    }

    private void handleMoney(AccountView view, boolean isDeposit) {
        Account account = view.getSelectedAccount();
        if (account == null) {
            view.showMessage("Choose an account first", true);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(view.getAmountText().trim());
        } catch (NumberFormatException e) {
            view.showMessage("Enter a valid amount, e.g. 250.50", true);
            return;
        }

        boolean ok = isDeposit ? account.deposit(amount) : account.withdraw(amount);
        if (ok) {
            view.showMessage((isDeposit ? "Deposit" : "Withdrawal") + " successful", false);
            view.clearAmount();
        } else if (isDeposit) {
            view.showMessage("Deposit must be greater than zero", true);
        } else if (account instanceof SavingsAccount) {
            view.showMessage("Savings accounts do not allow withdrawals", true);
        } else {
            view.showMessage("Withdrawal failed: invalid amount or insufficient funds", true);
        }
        view.showAccount(account);
    }

    // ---------------- administrator screen ----------------

    private void showAdminScreen(Stage stage, LoginView loginView) {
        AdminView view = new AdminView();
        view.setWelcome("Administrator " + admin.getUsername());
        view.setCustomers(admin.getCustomers());

        view.setOnRegister(() -> handleRegister(view));
        view.setOnOpenAccount(() -> handleOpenAccount(view));
        view.setOnRunInterest(() -> {
            admin.runMonthlyInterest();
            view.showMessage("Monthly interest paid on all Savings and Investment accounts", false);
        });
        view.setOnLogout(() -> {
            admin.logout();
            backToLogin(stage, loginView);
        });

        stage.getScene().setRoot(view.getRoot());
        resize(stage, 900, 620);
    }

    private void handleRegister(AdminView view) {
        try {
            Customer customer;
            if ("Individual".equals(view.getCustomerType())) {
                customer = new IndividualCustomer(nextUserId, view.getUsername(),
                        view.getPassword(), view.getCustomerId(), view.getAddress(),
                        view.getPhone(), view.getEmail(), view.getFirstName(),
                        view.getSurname(), view.getIdNumber(), view.isEmployed());
            } else {
                customer = new CompanyCustomer(nextUserId, view.getUsername(),
                        view.getPassword(), view.getCustomerId(), view.getAddress(),
                        view.getPhone(), view.getEmail(), view.getCompanyName(),
                        view.getRegistrationNumber());
            }
            admin.registerCustomer(customer);
            nextUserId++;
            view.setCustomers(admin.getCustomers());
            view.clearRegisterForm();
            view.showMessage("Customer registered: " + customer.getDisplayName(), false);
        } catch (IllegalArgumentException e) {
            view.showMessage(e.getMessage(), true);
        }
    }

    private void handleOpenAccount(AdminView view) {
        Customer customer = view.getSelectedCustomer();
        if (customer == null) {
            view.showMessage("Choose a customer first", true);
            return;
        }

        String type = view.getAccountType();
        String number = view.getAccountNumber();
        String branch = view.getBranch();
        try {
            Account account;
            if ("Savings".equals(type)) {
                account = new SavingsAccount(number, branch, customer);
            } else if ("Investment".equals(type)) {
                double deposit = Double.parseDouble(view.getOpeningDeposit().trim());
                account = new InvestmentAccount(number, branch, customer, deposit);
            } else {
                if (!(customer instanceof IndividualCustomer individual)) {
                    throw new IllegalArgumentException(
                            "Cheque accounts are only for individual customers");
                }
                account = new ChequeAccount(number, branch, individual,
                        view.getEmployerName(), view.getEmployerAddress());
            }
            admin.openAccount(customer, account);
            view.setCustomers(admin.getCustomers());
            view.clearAccountForm();
            view.showMessage(type + " account " + account.getAccountNumber()
                    + " opened for " + customer.getDisplayName(), false);
        } catch (NumberFormatException e) {
            view.showMessage("Enter a valid opening deposit, e.g. 500.00", true);
        } catch (IllegalArgumentException e) {
            view.showMessage(e.getMessage(), true);
        }
    }

    // ---------------- shared ----------------

    private void backToLogin(Stage stage, LoginView loginView) {
        loginView.clear();
        stage.getScene().setRoot(loginView.getRoot());
        resize(stage, 400, 350);
    }

    private void resize(Stage stage, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}