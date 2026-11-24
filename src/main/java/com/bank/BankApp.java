package com.bank;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Entry point of the JavaFX application. */
public class BankApp extends Application {

    @Override
    public void start(Stage stage) {
        Label hello = new Label("Banking System - JavaFX is working");
        stage.setScene(new Scene(new StackPane(hello), 400, 200));
        stage.setTitle("Banking System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
