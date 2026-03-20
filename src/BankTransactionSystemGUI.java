import javax.swing.*;
import java.awt.*;

public class BankTransactionSystemGUI {

    private Bank bank;
    private BankAccount currentAccount;

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Labels for balances
    private JLabel balanceLabel;
    private JLabel savingsLabel;

    public BankTransactionSystemGUI() {
        System.out.println("App started");

        // Load saved bank data
        bank = Bank.loadFromFile();

        // Main window setup
        frame = new JFrame("Bank System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use CardLayout to switch between pages
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add all pages
        mainPanel.add(createLoginPage(), "LOGIN");
        mainPanel.add(createDashboardPage(), "DASHBOARD");
        mainPanel.add(createSavingsPage(), "SAVINGS");
        mainPanel.add(createHistoryPage(), "HISTORY");

        frame.setContentPane(mainPanel);

        // Window sizing
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        // Start on login screen
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ───────── LOGIN PAGE ─────────
    private JPanel createLoginPage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JTextField username = new JTextField(15);
        JPasswordField password = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        // Layout components
        gbc.gridy = 0;
        panel.add(new JLabel("Username"), gbc);

        gbc.gridy = 1;
        panel.add(username, gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Password"), gbc);

        gbc.gridy = 3;
        panel.add(password, gbc);

        gbc.gridy = 4;
        panel.add(loginBtn, gbc);

        gbc.gridy = 5;
        panel.add(registerBtn, gbc);

        // Login logic
        loginBtn.addActionListener(e -> {
            try {
                currentAccount = bank.getAccount(username.getText());
                updateBalance();
                updateSavings();
                cardLayout.show(mainPanel, "DASHBOARD");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Login failed");
            }
        });

        // Register logic
        registerBtn.addActionListener(e -> {
            try {
                bank.createAccount(username.getText(), 0);
                bank.saveToFile();
                JOptionPane.showMessageDialog(frame, "Account created!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Username exists");
            }
        });

        return panel;
    }

    // ───────── DASHBOARD ─────────
    private JPanel createDashboardPage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Balance display
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 26));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(balanceLabel, gbc);

        // Input field
        JTextField amountField = new JTextField();

        gbc.gridy = 1;
        panel.add(amountField, gbc);

        // Deposit / Withdraw buttons
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(depositBtn, gbc);

        gbc.gridx = 1;
        panel.add(withdrawBtn, gbc);

        // Navigation buttons
        JButton savingsBtn = new JButton("Savings");
        JButton historyBtn = new JButton("Transactions");

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(savingsBtn, gbc);

        gbc.gridx = 1;
        panel.add(historyBtn, gbc);

        // Logout button
        JButton logoutBtn = new JButton("Logout");

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(logoutBtn, gbc);

        // Deposit action
        depositBtn.addActionListener(e -> {
            try {
                currentAccount.deposit(Double.parseDouble(amountField.getText()));
                bank.saveToFile();
                updateBalance();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // Withdraw action
        withdrawBtn.addActionListener(e -> {
            try {
                currentAccount.withdraw(Double.parseDouble(amountField.getText()));
                bank.saveToFile();
                updateBalance();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // Navigate to savings page
        savingsBtn.addActionListener(e -> {
            updateSavings();
            cardLayout.show(mainPanel, "SAVINGS");
        });

        // Navigate to history page
        historyBtn.addActionListener(e -> cardLayout.show(mainPanel, "HISTORY"));

        // Logout
        logoutBtn.addActionListener(e -> {
            currentAccount = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        return panel;
    }

    // ───────── SAVINGS PAGE ─────────
    private JPanel createSavingsPage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Savings balance display
        savingsLabel = new JLabel();
        savingsLabel.setFont(new Font("Arial", Font.BOLD, 22));
        savingsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(savingsLabel, gbc);

        JTextField amount = new JTextField();

        gbc.gridy = 1;
        panel.add(amount, gbc);

        JButton deposit = new JButton("Deposit");
        JButton withdraw = new JButton("Withdraw");

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(deposit, gbc);

        gbc.gridx = 1;
        panel.add(withdraw, gbc);

        JButton back = new JButton("Back");

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(back, gbc);

        // Deposit into savings
        deposit.addActionListener(e -> {
            try {
                currentAccount.getSavingsAccount().deposit(Double.parseDouble(amount.getText()));
                bank.saveToFile();
                updateSavings();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // Withdraw from savings
        withdraw.addActionListener(e -> {
            try {
                currentAccount.getSavingsAccount().withdraw(Double.parseDouble(amount.getText()));
                bank.saveToFile();
                updateSavings();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // Back to dashboard
        back.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        return panel;
    }

    // ───────── HISTORY PAGE ─────────
    private JPanel createHistoryPage() {

        JPanel panel = new JPanel(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);

        JButton back = new JButton("Back");

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(back, BorderLayout.SOUTH);

        // Back to dashboard
        back.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        // Load transactions when page opens
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                StringBuilder text = new StringBuilder();
                for (Transaction t : currentAccount.getTransactionHistory()) {
                    text.append(t.toString()).append("\n");
                }
                area.setText(text.toString());
            }
        });

        return panel;
    }

    // ───────── HELPERS ─────────

    // Update main balance label
    private void updateBalance() {
        if (currentAccount != null) {
            balanceLabel.setText("Balance: £" + currentAccount.getBalance());
        } else {
            balanceLabel.setText("Balance: £0.00");
        }
    }

    // Update savings balance label
    private void updateSavings() {
        if (currentAccount != null && currentAccount.getSavingsAccount() != null) {
            savingsLabel.setText("Savings: £" + currentAccount.getSavingsAccount().getBalance());
        } else {
            savingsLabel.setText("Savings: £0.00");
        }
    }

    // Show error popup
    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}
