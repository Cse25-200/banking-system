package com.bank;

import com.bank.model.BankAdministrator;
import com.bank.model.IndividualCustomer;
import com.bank.model.User;
import com.bank.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Entry point of the JavaFX application. */
public class BankApp extends Application {

    // TEMPORARY sample users (replaced by database + controllers in Weeks 6-7)
    private final BankAdministrator admin =
            new BankAdministrator(1, "admin", "admin123", "S001");
    private final IndividualCustomer thabo = new IndividualCustomer(2, "thabo", "pass123", "C001",
            "Plot 12, Gaborone", "71234567", "thabo@mail.com",
            "Thabo", "Molefe", "123456789", true);

    @Override
    public void start(Stage stage) {
        admin.registerCustomer(thabo);

        LoginView loginView = new LoginView();
        loginView.setOnLogin(() -> {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            User user = null;
            if (admin.login(username, password)) user = admin;
            else if (thabo.login(username, password)) user = thabo;

            if (user == null) {
                loginView.showError("Wrong username or password");
            } else {
                // Placeholder until the real screens exist
                stage.getScene().setRoot(new StackPane(
                        new Label("Logged in as " + user.getUsername()
                                + " (" + user.getRole() + ")")));
            }
        });

        stage.setScene(new Scene(loginView.getRoot(), 400, 350));
        stage.setTitle("Banking System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}