package com.bank.view;

import com.bank.model.Customer;
import com.bank.model.IndividualCustomer;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/** Administrator screen: register customers, open accounts, run monthly interest. */
public class AdminView {
    private final BorderPane root = new BorderPane();
    private final Label welcomeLabel = new Label();
    private final Button logoutButton = new Button("Log out");
    private final Label messageLabel = new Label();

    // ---- Register customer tab ----
    private final ComboBox<String> customerTypeBox = new ComboBox<>();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField customerIdField = new TextField();
    private final TextField addressField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField surnameField = new TextField();
    private final TextField idNumberField = new TextField();
    private final CheckBox employedBox = new CheckBox("Employed");
    private final TextField companyNameField = new TextField();
    private final TextField regNumberField = new TextField();
    private final Button registerButton = new Button("Register customer");

    // ---- Open account tab ----
    private final ComboBox<Customer> customerBox = new ComboBox<>();
    private final ComboBox<String> accountTypeBox = new ComboBox<>();
    private final TextField accountNumberField = new TextField();
    private final TextField branchField = new TextField();
    private final TextField openingDepositField = new TextField();
    private final TextField employerNameField = new TextField();
    private final TextField employerAddressField = new TextField();
    private final Button openAccountButton = new Button("Open account");

    // ---- Customers tab ----
    private final TableView<Customer> customerTable = new TableView<>();
    private final Button interestButton = new Button("Run monthly interest");

    public AdminView() {
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, welcomeLabel, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15));
        root.setTop(topBar);

        TabPane tabs = new TabPane(buildRegisterTab(), buildAccountTab(), buildCustomersTab());
        root.setCenter(tabs);

        HBox bottom = new HBox(messageLabel);
        bottom.setPadding(new Insets(10, 15, 10, 15));
        root.setBottom(bottom);
    }

    // ================= building the tabs =================

    private Tab buildRegisterTab() {
        customerTypeBox.getItems().addAll("Individual", "Company");
        customerTypeBox.getSelectionModel().selectFirst();

        GridPane common = grid();
        addRow(common, 0, "Customer type", customerTypeBox);
        addRow(common, 1, "Username", usernameField);
        addRow(common, 2, "Password", passwordField);
        addRow(common, 3, "Customer ID", customerIdField);
        addRow(common, 4, "Address", addressField);
        addRow(common, 5, "Phone number", phoneField);
        addRow(common, 6, "Email", emailField);

        GridPane individual = grid();
        addRow(individual, 0, "First name", firstNameField);
        addRow(individual, 1, "Surname", surnameField);
        addRow(individual, 2, "ID number", idNumberField);
        individual.add(employedBox, 1, 3);

        GridPane company = grid();
        addRow(company, 0, "Company name", companyNameField);
        addRow(company, 1, "Registration no.", regNumberField);
        show(company, false);

        // Show the fields that match the chosen customer type
        customerTypeBox.setOnAction(e -> {
            boolean isIndividual = "Individual".equals(customerTypeBox.getValue());
            show(individual, isIndividual);
            show(company, !isIndividual);
        });

        VBox form = new VBox(15, common, individual, company, registerButton);
        form.setPadding(new Insets(15));
        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        return tab("Register customer", scroll);
    }

    private Tab buildAccountTab() {
        customerBox.setPromptText("Choose a customer");
        customerBox.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer c) {
                return c == null ? "" : c.getCustomerId() + " - " + c.getDisplayName();
            }

            @Override
            public Customer fromString(String s) {
                return null;
            }
        });

        accountTypeBox.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeBox.getSelectionModel().selectFirst();
        accountNumberField.setPromptText("e.g. SAV002");
        branchField.setPromptText("e.g. Main");
        openingDepositField.setPromptText("At least 500.00");

        GridPane common = grid();
        addRow(common, 0, "Customer", customerBox);
        addRow(common, 1, "Account type", accountTypeBox);
        addRow(common, 2, "Account number", accountNumberField);
        addRow(common, 3, "Branch", branchField);

        GridPane investment = grid();
        addRow(investment, 0, "Opening deposit (BWP)", openingDepositField);
        show(investment, false);

        GridPane cheque = grid();
        addRow(cheque, 0, "Employer name", employerNameField);
        addRow(cheque, 1, "Employer address", employerAddressField);
        show(cheque, false);

        // Show only the extra fields the chosen account type needs
        accountTypeBox.setOnAction(e -> {
            String type = accountTypeBox.getValue();
            show(investment, "Investment".equals(type));
            show(cheque, "Cheque".equals(type));
        });

        VBox form = new VBox(15, common, investment, cheque, openAccountButton);
        form.setPadding(new Insets(15));
        return tab("Open account", form);
    }

    private Tab buildCustomersTab() {
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerId()));
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDisplayName()));
        TableColumn<Customer, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue() instanceof IndividualCustomer ? "Individual" : "Company"));
        TableColumn<Customer, String> countCol = new TableColumn<>("Accounts");
        countCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getAccounts().size())));
        customerTable.getColumns().addAll(idCol, nameCol, typeCol, countCol);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerTable.setPlaceholder(new Label("No customers registered"));
        VBox.setVgrow(customerTable, Priority.ALWAYS);

        VBox box = new VBox(12, interestButton, customerTable);
        box.setPadding(new Insets(15));
        return tab("Customers", box);
    }

    // ================= small helpers =================

    private static Tab tab(String title, Node content) {
        Tab t = new Tab(title, content);
        t.setClosable(false);
        return t;
    }

    private static GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(8);
        return g;
    }

    private static void addRow(GridPane g, int row, String label, Node field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
    }

    private static void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    // ================= what the controller can set =================

    public Parent getRoot() { 
        return root; }

    public void setWelcome(String text) {
         welcomeLabel.setText(text); }

    /** Refreshes the customer drop-down and the customers table. */
    public void setCustomers(List<Customer> customers) {
        Customer selected = customerBox.getValue();
        customerBox.getItems().setAll(customers);
        if (selected != null && customers.contains(selected)) customerBox.setValue(selected);
        customerTable.getItems().setAll(customers);
    }

    public void showMessage(String text, boolean isError) {
        messageLabel.setStyle("-fx-text-fill: " + (isError ? "#c0392b" : "#27ae60") + ";");
        messageLabel.setText(text);
    }

    public void clearRegisterForm() {
        for (TextField f : new TextField[] {usernameField, customerIdField, addressField,
                phoneField, emailField, firstNameField, surnameField, idNumberField,
                companyNameField, regNumberField}) {
            f.clear();
        }
        passwordField.clear();
        employedBox.setSelected(false);
    }

    public void clearAccountForm() {
        accountNumberField.clear();
        branchField.clear();
        openingDepositField.clear();
        employerNameField.clear();
        employerAddressField.clear();
    }

    // ================= what the controller can read =================

    public String getCustomerType() {
         return customerTypeBox.getValue(); }
    public String getUsername() { 
        return usernameField.getText(); }
    public String getPassword() { 
        return passwordField.getText(); }
    public String getCustomerId() {
         return customerIdField.getText(); }
    public String getAddress() { 
        return addressField.getText(); }
    public String getPhone() { 
        return phoneField.getText(); }
    public String getEmail() { 
        return emailField.getText(); }
    public String getFirstName() {
         return firstNameField.getText(); }
    public String getSurname() {
         return surnameField.getText(); }
    public String getIdNumber() {
         return idNumberField.getText(); }
    public boolean isEmployed() {
         return employedBox.isSelected(); }
    public String getCompanyName() {
         return companyNameField.getText(); }
    public String getRegistrationNumber() { 
        return regNumberField.getText(); }

    public Customer getSelectedCustomer() {
         return customerBox.getValue(); }
    public String getAccountType() {
         return accountTypeBox.getValue(); }
    public String getAccountNumber() {
         return accountNumberField.getText(); }
    public String getBranch() {
         return branchField.getText(); }
    public String getOpeningDeposit() {
         return openingDepositField.getText(); }
    public String getEmployerName() { 
        return employerNameField.getText(); }
    public String getEmployerAddress() {
         return employerAddressField.getText(); }

    // ================= hooks for the controller =================

    public void setOnRegister(Runnable action) {
         registerButton.setOnAction(e -> action.run()); }
    public void setOnOpenAccount(Runnable action) {
         openAccountButton.setOnAction(e -> action.run()); }
    public void setOnRunInterest(Runnable action) {
         interestButton.setOnAction(e -> action.run()); }
    public void setOnLogout(Runnable action) { 
        logoutButton.setOnAction(e -> action.run()); }
}