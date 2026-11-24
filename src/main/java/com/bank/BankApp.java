package com.bank;

import com.bank.model.Account;
import com.bank.model.BankAdministrator;
import com.bank.model.ChequeAccount;
import com.bank.model.Customer;
import com.bank.model.IndividualCustomer;
import com.bank.model.InvestmentAccount;
import com.bank.model.SavingsAccount;
import com.bank.model.User;
import com.bank.view.AccountView;
import com.bank.view.LoginView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Entry point of the JavaFX application. */
public class BankApp extends Application {

    // TEMPORARY sample data (replaced by database + controllers in Weeks 6-7)
    private final BankAdministrator admin =
            new BankAdministrator(1, "admin", "admin123", "S001");
    private final IndividualCustomer thabo = new IndividualCustomer(2, "thabo", "pass123", "C001",
            "Plot 12, Gaborone", "71234567", "thabo@mail.com",
            "Thabo", "Molefe", "123456789", true);

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
            if (admin.login(username, password)) user = admin;
            else if (thabo.login(username, password)) user = thabo;

            if (user == null) {
                loginView.showError("Wrong username or password");
            } else if (user instanceof Customer customer) {
                showAccountScreen(stage, loginView, customer);
            } else {
                // Administrator screen comes later
                stage.getScene().setRoot(new StackPane(new Label("Administrator screen coming soon")));
            }
        });

        stage.setScene(new Scene(loginView.getRoot(), 400, 350));
        stage.setTitle("Banking System");
        stage.show();
    }

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
            loginView.clear();
            stage.getScene().setRoot(loginView.getRoot());
            stage.setWidth(400);
            stage.setHeight(350);
            stage.centerOnScreen();
        });

        stage.getScene().setRoot(view.getRoot());
        stage.setWidth(820);
        stage.setHeight(560);
        stage.centerOnScreen();
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

    public static void main(String[] args) {
        launch(args);
    }
}