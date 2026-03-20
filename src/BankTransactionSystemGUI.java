import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BankTransactionSystemGUI {

    private Bank bank;
    private BankAccount currentAccount;

    private JFrame frame;
    private JLabel balanceLabel;
    private JLabel savingsBalanceLabel;

    public BankTransactionSystemGUI() {
        bank = Bank.loadFromFile();
        showLoginScreen();
    }

    // ---------- LOGIN ----------
    private void showLoginScreen() {

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);

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

        try {
            currentAccount = bank.login(username, password);
            createMainUI();
        } catch (Exception e) {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Login failed.\nCreate a new account?",
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

    // ---------- REGISTER ----------
    private void showRegisterScreen() {

        JDialog dialog = new JDialog((Frame) null, "Register", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JLabel strengthLabel = new JLabel("Strength:");

        password.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String strength = getPasswordStrength(new String(password.getPassword()));
                strengthLabel.setText("Strength: " + strength);
                strengthLabel.setForeground(getStrengthColor(strength));
            }

            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");

        dialog.add(new JLabel("Username:"));
        dialog.add(username);

        dialog.add(new JLabel("Password:"));
        dialog.add(password);

        dialog.add(new JLabel("Confirm:"));
        dialog.add(confirm);

        dialog.add(strengthLabel);
        dialog.add(new JLabel());

        dialog.add(registerBtn);
        dialog.add(cancelBtn);

        registerBtn.addActionListener(e -> {
            String user = username.getText().trim();
            String pass = new String(password.getPassword());
            String conf = new String(confirm.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Fields cannot be empty");
                return;
            }

            if (!pass.equals(conf)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match");
                return;
            }

            if (getPasswordStrength(pass).equals("Weak")) {
                JOptionPane.showMessageDialog(dialog, "Password too weak");
                return;
            }

            try {
                bank.createAccount(user, pass, 0);
                bank.saveToFile();
                JOptionPane.showMessageDialog(dialog, "Account created!");
                dialog.dispose();
                showLoginScreen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Username exists");
            }
        });

        cancelBtn.addActionListener(e -> {
            dialog.dispose();
            showLoginScreen();
        });

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // ---------- PASSWORD STRENGTH ----------
    private String getPasswordStrength(String password) {
        boolean upper = password.matches(".*[A-Z].*");
        boolean lower = password.matches(".*[a-z].*");
        boolean digit = password.matches(".*\\d.*");

        int score = 0;
        if (password.length() >= 8) score++;
        if (upper) score++;
        if (lower) score++;
        if (digit) score++;

        if (score <= 2) return "Weak";
        if (score == 3) return "Medium";
        return "Strong";
    }

    private Color getStrengthColor(String s) {
        switch (s) {
            case "Weak": return Color.RED;
            case "Medium": return Color.ORANGE;
            case "Strong": return new Color(0,128,0);
            default: return Color.BLACK;
        }
    }

    // ---------- MAIN UI ----------
    private void createMainUI() {

        frame = new JFrame("Bank System - " + currentAccount.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField depositField = new JTextField(10);
        JTextField withdrawField = new JTextField(10);

        JTextField fromField = new JTextField(10);
        JTextField toField = new JTextField(10);
        JTextField transferField = new JTextField(10);

        fromField.setText(currentAccount.getAccountId());

        balanceLabel = new JLabel();
        updateBalanceLabel();

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton historyButton = new JButton("View Transactions");
        JButton logoutButton = new JButton("Logout");

        // ---- Savings ----
        JTextField savingsDepositField = new JTextField(10);
        JTextField savingsWithdrawField = new JTextField(10);

        savingsBalanceLabel = new JLabel();
        updateSavingsBalanceLabel();

        JButton savingsDepositButton = new JButton("Deposit to Savings");
        JButton savingsWithdrawButton = new JButton("Withdraw from Savings");
        JButton savingsHistoryButton = new JButton("View Savings Transactions");

        // ---------- ACTIONS ----------

        depositButton.addActionListener(e -> {
            try {
                currentAccount.deposit(Double.parseDouble(depositField.getText()));
                bank.saveToFile();
                updateBalanceLabel();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        withdrawButton.addActionListener(e -> {
            try {
                currentAccount.withdraw(Double.parseDouble(withdrawField.getText()));
                bank.saveToFile();
                updateBalanceLabel();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        transferButton.addActionListener(e -> {
            try {
                bank.transfer(
                        fromField.getText().trim(),
                        toField.getText().trim(),
                        Double.parseDouble(transferField.getText())
                );
                bank.saveToFile();
                updateBalanceLabel();
                JOptionPane.showMessageDialog(null, "Transfer successful!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        historyButton.addActionListener(e -> showTransactionHistory());

        logoutButton.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        // ---- Savings actions ----

        savingsDepositButton.addActionListener(e -> {
            try {
                currentAccount.getSavingsAccount().deposit(Double.parseDouble(savingsDepositField.getText()));
                bank.saveToFile();
                updateSavingsBalanceLabel();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        savingsWithdrawButton.addActionListener(e -> {
            try {
                currentAccount.getSavingsAccount().withdraw(Double.parseDouble(savingsWithdrawField.getText()));
                bank.saveToFile();
                updateSavingsBalanceLabel();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        savingsHistoryButton.addActionListener(e -> showSavingsTransactionHistory());

        // ---------- LAYOUT ----------

        JPanel mainPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Main Account"));

        mainPanel.add(new JLabel("Deposit:"));
        mainPanel.add(depositField);
        mainPanel.add(depositButton);
        mainPanel.add(new JLabel());

        mainPanel.add(new JLabel("Withdraw:"));
        mainPanel.add(withdrawField);
        mainPanel.add(withdrawButton);
        mainPanel.add(new JLabel());

        mainPanel.add(new JLabel("From ID:"));
        mainPanel.add(fromField);

        mainPanel.add(new JLabel("To ID:"));
        mainPanel.add(toField);

        mainPanel.add(new JLabel("Transfer Amount:"));
        mainPanel.add(transferField);
        mainPanel.add(transferButton);
        mainPanel.add(new JLabel());

        mainPanel.add(balanceLabel);
        mainPanel.add(historyButton);

        JPanel savingsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        savingsPanel.setBorder(BorderFactory.createTitledBorder("Savings"));

        savingsPanel.add(new JLabel("Deposit:"));
        savingsPanel.add(savingsDepositField);
        savingsPanel.add(savingsDepositButton);
        savingsPanel.add(new JLabel());

        savingsPanel.add(new JLabel("Withdraw:"));
        savingsPanel.add(savingsWithdrawField);
        savingsPanel.add(savingsWithdrawButton);
        savingsPanel.add(new JLabel());

        savingsPanel.add(savingsBalanceLabel);
        savingsPanel.add(savingsHistoryButton);

        JPanel bottom = new JPanel();
        bottom.add(logoutButton);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(savingsPanel, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ---------- HELPERS ----------
    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: £" + currentAccount.getBalance());
    }

    private void updateSavingsBalanceLabel() {
        savingsBalanceLabel.setText(
                "Savings: £" + currentAccount.getSavingsAccount().getBalance()
        );
    }

    private void showTransactionHistory() {
        StringBuilder history = new StringBuilder();

        for (Transaction t : currentAccount.getTransactionHistory()) {
            history.append(t.toString()).append("\n");
        }

        JTextArea area = new JTextArea(history.toString());
        area.setEditable(false);

        JOptionPane.showMessageDialog(frame, new JScrollPane(area), "History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSavingsTransactionHistory() {
        StringBuilder history = new StringBuilder();

        for (Transaction t : currentAccount.getSavingsAccount().getTransactionHistory()) {
            history.append(t.toString()).append("\n");
        }

        JTextArea area = new JTextArea(history.toString());
        area.setEditable(false);

        JOptionPane.showMessageDialog(frame, new JScrollPane(area), "Savings History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}