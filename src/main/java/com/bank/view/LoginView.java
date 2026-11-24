package com.bank.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Login screen. Only builds the screen; it makes no decisions about who may log in. */
public class LoginView {
    private final VBox root = new VBox(12);
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button loginButton = new Button("Log in");
    private final Label messageLabel = new Label();

    public LoginView() {
        Label title = new Label("Banking System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        loginButton.setDefaultButton(true);          // Enter key presses the button
        loginButton.setMaxWidth(Double.MAX_VALUE);

        messageLabel.setStyle("-fx-text-fill: #c0392b;");

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getChildren().addAll(title, usernameField, passwordField, loginButton, messageLabel);
    }

    public Parent getRoot() { return root; }

    public String getUsername() { return usernameField.getText(); }

    public String getPassword() { return passwordField.getText(); }

    /** The code that runs when the user presses "Log in" or Enter. */
    public void setOnLogin(Runnable action) {
        loginButton.setOnAction(e -> action.run());
    }

    public void showError(String message) {
        messageLabel.setText(message);
        passwordField.clear();
    }
}