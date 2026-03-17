import javax.swing.*;
import java.awt.*;

/**
 * Simple GUI for interacting with a BankAccount.
 * Clean Code improvements: meaningful names, extracted helper methods,
 * proper exception handling, and UI updates on the Event Dispatch Thread.
 */
public class BankTransactionSystemGUI {

    private final Bank bank;
    private String currentAccountId;
    private JLabel balanceLabel;

    public BankTransactionSystemGUI() {

        // Create the bank system
        bank = new Bank();

        // Create a default account for the user
        currentAccountId = bank.createAccount(1000);

        createAndShowUI();
    }

    private void createAndShowUI() {

        JFrame frame = new JFrame("Bank Transaction System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField depositField = new JTextField(10);
        JTextField withdrawField = new JTextField(10);

        // Input fields for transfer functionality
        JTextField fromField = new JTextField(10);
        JTextField toField = new JTextField(10);

        JTextField transferAmountField = new JTextField(10);

        // Pre-fill "from" with current account
        fromField.setText(currentAccountId);

        // Initialize the balance label
        double balance = bank.getAccount(currentAccountId).getBalance();
        balanceLabel = new JLabel("Balance: " + balance);

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");

        // Button to trigger transfer
        JButton transferButton = new JButton("Transfer");

        JButton newAccountButton = new JButton("Create Account");


        // Creates a new account and switches the GUI to it
        newAccountButton.addActionListener(e -> {

            currentAccountId = bank.createAccount(0);
            fromField.setText(currentAccountId);

            updateBalanceLabel();

            JOptionPane.showMessageDialog(
                    null,
                    "New account created: " + currentAccountId
            );
        });

        JButton historyButton = new JButton("View Transactions");

        depositButton.addActionListener(e -> handleDeposit(depositField));
        withdrawButton.addActionListener(e -> handleWithdrawal(withdrawField));
        historyButton.addActionListener(e -> showTransactionHistory());

        /**
         * Handles transferring money between accounts.
         */
        transferButton.addActionListener(e -> {
            try {
                String fromId = fromField.getText().trim();
                String toId = toField.getText().trim();
                double amount = Double.parseDouble(transferAmountField.getText());

                bank.transfer(fromId, toId, amount);

                updateBalanceLabel();

                JOptionPane.showMessageDialog(null, "Transfer successful!");

            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        });

        JPanel panel = new JPanel(new GridLayout(8, 2));

        panel.add(new JLabel("Deposit Amount:"));
        panel.add(depositField);
        panel.add(depositButton);

        panel.add(new JLabel("Withdraw Amount:"));
        panel.add(withdrawField);
        panel.add(withdrawButton);

        // Transfer section
        panel.add(new JLabel("From Account ID:"));
        panel.add(fromField);

        panel.add(new JLabel("To Account ID:"));
        panel.add(toField);

        panel.add(new JLabel("Transfer Amount:"));
        panel.add(transferAmountField);

        panel.add(transferButton);

        panel.add(balanceLabel);
        panel.add(historyButton);

        panel.add(newAccountButton);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void handleDeposit(JTextField depositField) {
        try {
            double amount = Double.parseDouble(depositField.getText());
            bank.getAccount(currentAccountId).deposit(amount);
            updateBalanceLabel();
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleWithdrawal(JTextField withdrawField) {
        try {
            double amount = Double.parseDouble(withdrawField.getText());
            bank.getAccount(currentAccountId).withdraw(amount);
            updateBalanceLabel();
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateBalanceLabel() {
        double balance = bank.getAccount(currentAccountId).getBalance();
        balanceLabel.setText("Balance: " + balance);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays all recorded transactions in a scrollable popup window.
     */
    private void showTransactionHistory() {

        // StringBuilder efficiently builds a large text output
        StringBuilder history = new StringBuilder();

        // Loop through each stored transaction
        for (Transaction t : bank.getAccount(currentAccountId).getTransactionHistory()) {
            history.append(t.toString()).append("\n");
        }

        // Display transactions inside a text area
        JTextArea textArea = new JTextArea(history.toString());
        textArea.setEditable(false);

        // ScrollPane allows the history to be scrollable if it becomes long
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Show the transaction history in a dialog window
        JOptionPane.showMessageDialog(
                null,
                scrollPane,
                "Transaction History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}