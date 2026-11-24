package com.bank.view;

import com.bank.model.Account;
import com.bank.model.Transaction;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/** Customer screen: choose an account, see balance and history, deposit or withdraw. */
public class AccountView {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BorderPane root = new BorderPane();
    private final Label welcomeLabel = new Label();
    private final Button logoutButton = new Button("Log out");
    private final ComboBox<Account> accountBox = new ComboBox<>();
    private final Label balanceLabel = new Label("BWP 0.00");
    private final TextField amountField = new TextField();
    private final Button depositButton = new Button("Deposit");
    private final Button withdrawButton = new Button("Withdraw");
    private final Label messageLabel = new Label();
    private final TableView<Transaction> historyTable = new TableView<>();

    public AccountView() {
        // ----- top bar -----
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, welcomeLabel, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15));
        root.setTop(topBar);

        // ----- account chooser + balance -----
        accountBox.setPromptText("Choose an account");
        accountBox.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account a) {
                return a == null ? "" : a.getAccountType() + " - " + a.getAccountNumber();
            }

            @Override
            public Account fromString(String s) {
                return null;
            }
        });
        balanceLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        HBox accountRow = new HBox(15, new Label("Account:"), accountBox,
                new Label("Balance:"), balanceLabel);
        accountRow.setAlignment(Pos.CENTER_LEFT);

        // ----- amount + buttons -----
        amountField.setPromptText("Amount (BWP)");
        HBox actionRow = new HBox(10, amountField, depositButton, withdrawButton);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        // ----- history table -----
        TableColumn<Transaction, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getTransactionId())));
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getType().toString()));
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount (BWP)");
        amountCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getAmount())));
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date / time");
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDateTime().format(TIME_FORMAT)));
        TableColumn<Transaction, String> balanceCol = new TableColumn<>("Balance after (BWP)");
        balanceCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getBalanceAfter())));
        historyTable.getColumns().addAll(idCol, typeCol, amountCol, dateCol, balanceCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPlaceholder(new Label("No transactions yet"));
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        VBox centre = new VBox(12, accountRow, actionRow, messageLabel,
                new Label("Transaction history"), historyTable);
        centre.setPadding(new Insets(0, 15, 15, 15));
        root.setCenter(centre);
    }

    public Parent getRoot() { return root; }

    // ----- what the controller can set -----

    public void setWelcome(String text) {
         welcomeLabel.setText(text); }

    public void setAccounts(List<Account> accounts) {
        accountBox.getItems().setAll(accounts);
        if (!accounts.isEmpty()) accountBox.getSelectionModel().selectFirst();
    }

    /** Shows the balance and transaction history of the given account. */
    public void showAccount(Account account) {
        if (account == null) {
            balanceLabel.setText("BWP 0.00");
            historyTable.getItems().clear();
        } else {
            balanceLabel.setText(String.format("BWP %.2f", account.getBalance()));
            historyTable.getItems().setAll(account.getTransactionHistory());
        }
    }

    public void showMessage(String text, boolean isError) {
        messageLabel.setStyle("-fx-text-fill: " + (isError ? "#c0392b" : "#27ae60") + ";");
        messageLabel.setText(text);
    }

    public void clearAmount() {
         amountField.clear(); }

    // ----- what the controller can read -----

    public Account getSelectedAccount() {
         return accountBox.getValue(); }

    public String getAmountText() { 
        return amountField.getText(); }

    // ----- hooks for the controller -----

    public void setOnAccountSelected(Runnable action) {
         accountBox.setOnAction(e -> action.run()); }

    public void setOnDeposit(Runnable action) {
         depositButton.setOnAction(e -> action.run()); }

    public void setOnWithdraw(Runnable action) {
         withdrawButton.setOnAction(e -> action.run()); }

    public void setOnLogout(Runnable action) {
         logoutButton.setOnAction(e -> action.run()); }
}