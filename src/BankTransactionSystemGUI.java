import javax.swing.*;
import java.awt.*;

public class BankTransactionSystemGUI {

    private Bank bank;
    private BankAccount currentAccount;

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JLabel balanceLabel;
    private JLabel savingsLabel;

    public BankTransactionSystemGUI() {

        bank = Bank.loadFromFile();

        frame = new JFrame("Bank System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPage(), "LOGIN");
        mainPanel.add(createDashboardPage(), "DASHBOARD");
        mainPanel.add(createSavingsPage(), "SAVINGS");
        mainPanel.add(createHistoryPage(), "HISTORY");

        frame.setContentPane(mainPanel);

        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(mainPanel, "LOGIN");
    }

    // LOGIN PAGE
    private JPanel createLoginPage() {

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(new JLabel("Username"));
        panel.add(username);
        panel.add(new JLabel("Password"));
        panel.add(password);
        panel.add(loginBtn);
        panel.add(registerBtn);

        // LOGIN
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

        // REGISTER
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

    // DASHBOARD
    private JPanel createDashboardPage() {

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField amountField = new JTextField();

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton savingsBtn = new JButton("Savings");
        JButton historyBtn = new JButton("Transactions");
        JButton logoutBtn = new JButton("Logout");

        panel.add(balanceLabel);
        panel.add(amountField);
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(savingsBtn);
        panel.add(historyBtn);
        panel.add(logoutBtn);

        // DEPOSIT
        depositBtn.addActionListener(e -> {
            try {
                currentAccount.deposit(Double.parseDouble(amountField.getText()));
                bank.saveToFile();
                updateBalance();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // WITHDRAW
        withdrawBtn.addActionListener(e -> {
            try {
                currentAccount.withdraw(Double.parseDouble(amountField.getText()));
                bank.saveToFile();
                updateBalance();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        savingsBtn.addActionListener(e -> {
            updateSavings();
            cardLayout.show(mainPanel, "SAVINGS");
        });

        historyBtn.addActionListener(e -> cardLayout.show(mainPanel, "HISTORY"));

        logoutBtn.addActionListener(e -> {
            currentAccount = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        return panel;
    }

    // SAVINGS PAGE
    private JPanel createSavingsPage() {

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        savingsLabel = new JLabel("", SwingConstants.CENTER);
        savingsLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField amount = new JTextField();

        JButton deposit = new JButton("Deposit");
        JButton withdraw = new JButton("Withdraw");
        JButton back = new JButton("Back");

        panel.add(savingsLabel);
        panel.add(amount);
        panel.add(deposit);
        panel.add(withdraw);
        panel.add(back);

        deposit.addActionListener(e -> {
            try {
                currentAccount.getSavingsAccount().deposit(Double.parseDouble(amount.getText()));
                bank.saveToFile();
                updateSavings();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        withdraw.addActionListener(e -> {
            try {
                currentAccount.getSavingsAccount().withdraw(Double.parseDouble(amount.getText()));
                bank.saveToFile();
                updateSavings();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        return panel;
    }

    // HISTORY PAGE
    private JPanel createHistoryPage() {

        JPanel panel = new JPanel(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);

        JButton back = new JButton("Back");

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(back, BorderLayout.SOUTH);

        back.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                if (currentAccount != null) {
                    StringBuilder text = new StringBuilder();
                    for (Transaction t : currentAccount.getTransactionHistory()) {
                        text.append(t.toString()).append("\n");
                    }
                    area.setText(text.toString());
                }
            }
        });

        return panel;
    }

    // HELPERS
    private void updateBalance() {
        balanceLabel.setText("Balance: £" + currentAccount.getBalance());
    }

    private void updateSavings() {
        savingsLabel.setText("Savings: £" + currentAccount.getSavingsAccount().getBalance());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}