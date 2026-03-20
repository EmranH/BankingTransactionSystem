import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Full banking GUI:
 * - Login / Register system
 * - Main banking interface
 *  - Savings account section with deposit, withdrawal, and balance display
 * - Works with persistent Bank class
 */
public class BankTransactionSystemGUI {

    private Bank bank;
    private BankAccount currentAccount;

    private JFrame frame;
    private JLabel balanceLabel;
    private JLabel savingsBalanceLabel;


    public BankTransactionSystemGUI() {

        // Load saved bank data (or create new if first run)
        bank = Bank.loadFromFile();

        showLoginScreen();
    }

    /**
     * Displays that handles only login process
     */
    private void showLoginScreen() {

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                null,
                message,
                "Login",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username and password cannot be empty.");
            showLoginScreen();
            return;
        }

        boolean validLogin = UserDAO.loginUser(username, password);

        if (validLogin) {
            try {
                currentAccount = bank.getAccount(username);
            } catch (Exception e) {
                try {
                    bank.createAccount(username, 0);
                    currentAccount = bank.getAccount(username);
                    bank.saveToFile();
                } catch (Exception ex) {
                    showError("Login worked in database, but bank account could not be loaded.");
                    return;
                }
            }

            createMainUI();
        } else {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Login failed.\nWould you like to create a new account?",
                    "Account Not Found",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                showRegisterScreen();
            } else {
                showLoginScreen();
            }
        }
    }

    /**
     * Display that handles the registration process
     */
    private void showRegisterScreen() {
        JDialog registerDialog = new JDialog((Frame) null, "Create New Account", true);
        registerDialog.setSize(500, 360);
        registerDialog.setLocationRelativeTo(null);
        registerDialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);
        JPasswordField confirmPasswordField = new JPasswordField(18);

        char defaultPasswordEcho = passwordField.getEchoChar();
        char defaultConfirmEcho = confirmPasswordField.getEchoChar();

        JCheckBox showPasswordBox = new JCheckBox("Show");
        JCheckBox showConfirmPasswordBox = new JCheckBox("Show");

        JLabel strengthLabel = new JLabel("Password strength: ");
        strengthLabel.setForeground(Color.BLACK);

        JLabel rulesLabel = new JLabel(
                "<html><span style='color:#555555;'>Password rules: 8+ characters, uppercase, lowercase, number. " +
                        "Special character makes it strong.</span></html>"
        );

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateStrength() {
                String password = new String(passwordField.getPassword());
                String strength = getPasswordStrength(password);
                strengthLabel.setText("Password strength: " + strength);
                strengthLabel.setForeground(getStrengthColor(strength));
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStrength();
            }
        });

        showPasswordBox.addActionListener(e -> {
            if (showPasswordBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultPasswordEcho);
            }
        });

        showConfirmPasswordBox.addActionListener(e -> {
            if (showConfirmPasswordBox.isSelected()) {
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                confirmPasswordField.setEchoChar(defaultConfirmEcho);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Choose Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Choose Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(showPasswordBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(showConfirmPasswordBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(strengthLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(rulesLabel, gbc);

        JPanel buttonPanel = new JPanel();

        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "All fields are required.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match.");
                return;
            }

            String strength = getPasswordStrength(password);
            if (strength.equals("Weak")) {
                JOptionPane.showMessageDialog(registerDialog,
                        "Password is too weak.\nUse at least 8 characters with upper, lower, and numbers.");
                return;
            }

            boolean registered = UserDAO.registerUser(username, password);

            if (registered) {
                try {
                    bank.createAccount(username, 0);
                    bank.saveToFile();
                } catch (Exception ex) {
                    showError("User added to database, but bank account could not be created.");
                    return;
                }

                JOptionPane.showMessageDialog(registerDialog, "Account created successfully. Please log in.");
                registerDialog.dispose();
                showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(registerDialog,
                        "Username already exists. Please choose another username.");
            }
        });

        cancelButton.addActionListener(e -> {
            registerDialog.dispose();
            showLoginScreen();
        });

        registerDialog.add(formPanel, BorderLayout.CENTER);
        registerDialog.add(buttonPanel, BorderLayout.SOUTH);

        registerDialog.setResizable(false);
        registerDialog.setVisible(true);
    }

    private String getPasswordStrength(String password) {
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        int score = 0;

        if (password.length() >= 8) score++;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        if (password.length() < 6 || score <= 2) {
            return "Weak";
        } else if (score == 3 || score == 4) {
            return "Acceptable";
        } else {
            return "Strong";
        }
    }

    private Color getStrengthColor(String strength) {
        switch (strength) {
            case "Weak":
                return Color.RED;
            case "Acceptable":
                return Color.ORANGE;
            case "Strong":
                return new Color(0, 128, 0);
            default:
                return Color.BLACK;
        }
    }

    /**
     * Main banking interface after login.
     * Split into two sections:
     * Top: main account operations (deposit, withdraw, transfer)
     * Bottom: savings account operations (deposit, withdraw, balance)
     */
    private void createMainUI() {

        frame = new JFrame("Bank System - " + currentAccount.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField depositField = new JTextField(10);
        JTextField withdrawField = new JTextField(10);

        JTextField fromField = new JTextField(10);
        JTextField toField = new JTextField(10);
        JTextField transferField = new JTextField(10);

        // Pre-fill "from" account
        fromField.setText(currentAccount.getAccountId());

        balanceLabel = new JLabel();
        updateBalanceLabel();

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton historyButton = new JButton("View Transactions");
        JButton logoutButton = new JButton("Logout");

        // ── Savings account fields ──
        JTextField savingsDepositField = new JTextField(10);
        JTextField savingsWithdrawField = new JTextField(10);

        savingsBalanceLabel = new JLabel();
        updateSavingsBalanceLabel();

        JButton savingsDepositButton = new JButton("Deposit to Savings");
        JButton savingsWithdrawButton = new JButton("Withdraw from Savings");
        JButton savingsHistoryButton = new JButton("View Savings Transactions");

        // ── Main account button logic ──

        //Deposit logic

        depositButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(depositField.getText());
                currentAccount.deposit(amount);

                bank.saveToFile(); // persist changes
                updateBalanceLabel();

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        //Withdraw logic

        withdrawButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(withdrawField.getText());
                currentAccount.withdraw(amount);

                bank.saveToFile();
                updateBalanceLabel();

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        /**
         * Transfer logic
         */
        transferButton.addActionListener(e -> {
            try {
                String fromId = fromField.getText().trim();
                String toId = toField.getText().trim();
                double amount = Double.parseDouble(transferField.getText());

                bank.transfer(fromId, toId, amount);

                bank.saveToFile();
                updateBalanceLabel();

                JOptionPane.showMessageDialog(null, "Transfer successful!");

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        /**
         * Show main account transaction history
         */
        historyButton.addActionListener(e -> showTransactionHistory());

        /**
         * Logout → return to login screen
         */
        logoutButton.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        // ── Savings account button logic ──

        /**
         * Deposit into savings account
         */
        savingsDepositButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(savingsDepositField.getText());
                currentAccount.getSavingsAccount().deposit(amount);

                bank.saveToFile();
                updateSavingsBalanceLabel();

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        /**
         * Withdraw from savings account.
         * SavingsAccount.withdraw() will throw an error if the monthly limit is reached.
         */
        savingsWithdrawButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(savingsWithdrawField.getText());
                currentAccount.getSavingsAccount().withdraw(amount);

                bank.saveToFile();
                updateSavingsBalanceLabel();

                // Show remaining withdrawals after a successful withdrawal
                int remaining = currentAccount.getSavingsAccount().getWithdrawalsRemaining();
                JOptionPane.showMessageDialog(null,
                        "Withdrawal successful!\nWithdrawals remaining this month: " + remaining);

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        /**
         * Show savings account transaction history
         */
        savingsHistoryButton.addActionListener(e -> showSavingsTransactionHistory());

        // Layout

        // Main account panel
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Main Account"));

        panel.add(new JLabel("Deposit Amount:"));
        panel.add(depositField);
        panel.add(depositButton);
        panel.add(new JLabel());

        panel.add(new JLabel("Withdraw Amount:"));
        panel.add(withdrawField);
        panel.add(withdrawButton);
        panel.add(new JLabel());

        panel.add(new JLabel("From Account:"));
        panel.add(fromField);

        panel.add(new JLabel("To Account:"));
        panel.add(toField);

        panel.add(new JLabel("Transfer Amount:"));
        panel.add(transferField);
        panel.add(transferButton);
        panel.add(new JLabel());

        panel.add(balanceLabel);
        panel.add(historyButton);

        // Savings account panel
        JPanel savingsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        savingsPanel.setBorder(BorderFactory.createTitledBorder("Savings Account (2% Monthly Interest)"));

        savingsPanel.add(new JLabel("Deposit Amount:"));
        savingsPanel.add(savingsDepositField);
        savingsPanel.add(savingsDepositButton);
        savingsPanel.add(new JLabel());

        savingsPanel.add(new JLabel("Withdraw Amount:"));
        savingsPanel.add(savingsWithdrawField);
        savingsPanel.add(savingsWithdrawButton);
        savingsPanel.add(new JLabel());

        savingsPanel.add(savingsBalanceLabel);
        savingsPanel.add(savingsHistoryButton);

        // Logout button panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(logoutButton);

        // Combine all panels into the main frame
        frame.setLayout(new BorderLayout(10, 10));
        frame.add(panel, BorderLayout.NORTH);
        frame.add(savingsPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setMinimumSize(new Dimension(400, 500));
        frame.setVisible(true);
    }

    /**
     * Updates the main account balance label dynamically.
     */
    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: £" + currentAccount.getBalance());
    }

    /**
     * Updates the savings account balance label dynamically.
     */
    private void updateSavingsBalanceLabel() {
        savingsBalanceLabel.setText("Savings Balance: £" + String.format("%.2f", currentAccount.getSavingsAccount().getBalance()));
    }

    /**
     * Displays main account transaction history in a scrollable window.
     */
    private void showTransactionHistory() {

        StringBuilder history = new StringBuilder();

        for (Transaction t : currentAccount.getTransactionHistory()) {
            history.append(t.toString()).append("\n");
        }

        JTextArea textArea = new JTextArea(history.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(
                frame,
                scrollPane,
                "Transaction History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays savings account transaction history in a scrollable window.
     * Shows all deposits (including interest) and withdrawals on the savings account.
     */
    private void showSavingsTransactionHistory() {

        StringBuilder history = new StringBuilder();

        for (Transaction t : currentAccount.getSavingsAccount().getTransactionHistory()) {
            history.append(t.toString()).append("\n");
        }

        JTextArea textArea = new JTextArea(history.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(
                frame,
                scrollPane,
                "Savings Transaction History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays error messages consistently.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}
