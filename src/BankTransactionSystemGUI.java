import javax.swing.*;
import java.awt.*;

/**
 * Full banking GUI:
 * - Login / Register system
 * - Main banking interface
 * - Works with persistent Bank class
 */
public class BankTransactionSystemGUI {

    private Bank bank;
    private BankAccount currentAccount;

    private JFrame frame;
    private JLabel balanceLabel;

    public BankTransactionSystemGUI() {

        // Load saved bank data (or create new if first run)
        bank = Bank.loadFromFile();

        showLoginScreen();
    }

    /**
     * Displays login/register screen before accessing the system.
     */
    private void showLoginScreen() {

        JTextField usernameField = new JTextField();
        JPasswordField pinField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "PIN:", pinField
        };

        int option = JOptionPane.showConfirmDialog(
                null,
                message,
                "Login or Register",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        String username = usernameField.getText().trim();
        String pin = new String(pinField.getPassword());

        try {
            // Try login
            currentAccount = bank.login(username, pin);

        } catch (Exception e) {

            // If login fails → ask to register
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "User not found. Create new account?",
                    "Register",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                bank.createAccount(username, pin, 0);
                currentAccount = bank.login(username, pin);
                bank.saveToFile();
            } else {
                showLoginScreen();
                return;
            }
        }

        createMainUI();
    }

    /**
     * Main banking interface after login.
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

        /**
         * Deposit logic
         */
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

        /**
         * Withdraw logic
         */
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
         * Show transaction history
         */
        historyButton.addActionListener(e -> showTransactionHistory());

        /**
         * Logout → return to login screen
         */
        logoutButton.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        // Layout
        JPanel panel = new JPanel(new GridLayout(9, 2));

        panel.add(new JLabel("Deposit Amount:"));
        panel.add(depositField);
        panel.add(depositButton);

        panel.add(new JLabel("Withdraw Amount:"));
        panel.add(withdrawField);
        panel.add(withdrawButton);

        panel.add(new JLabel("From Account:"));
        panel.add(fromField);

        panel.add(new JLabel("To Account:"));
        panel.add(toField);

        panel.add(new JLabel("Transfer Amount:"));
        panel.add(transferField);
        panel.add(transferButton);

        panel.add(balanceLabel);
        panel.add(historyButton);

        panel.add(logoutButton);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Updates balance label dynamically.
     */
    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: £" + currentAccount.getBalance());
    }

    /**
     * Displays transaction history in a scrollable window.
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
     * Displays error messages consistently.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}