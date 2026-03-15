import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankTransactionSystemGUI {

    private static BankAccount account = new BankAccount(1000);

    public static void main(String[] args) {

        JFrame frame = new JFrame("Bank Account");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("BANK ACCOUNT", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(title, gbc);

        // Balance Label
        JLabel balanceText = new JLabel("Current Balance:");
        balanceText.setFont(new Font("Arial", Font.PLAIN,16));

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(balanceText, gbc);

        // Balance Display
        JLabel balanceAmount = new JLabel("£" + account.getBalance(), JLabel.CENTER);
        balanceAmount.setOpaque(true);
        balanceAmount.setBackground(Color.LIGHT_GRAY);
        balanceAmount.setFont(new Font("Arial", Font.BOLD,18));

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(balanceAmount, gbc);

        // Withdraw Title
        JLabel withdrawTitle = new JLabel("Withdraw", JLabel.CENTER);
        withdrawTitle.setFont(new Font("Arial", Font.BOLD,18));

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        panel.add(withdrawTitle, gbc);

        // Withdraw Field
        JTextField withdrawField = new JTextField();

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(withdrawField, gbc);

        // Withdraw Button
        JButton withdrawButton = new JButton("Withdraw");

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(withdrawButton, gbc);

        // Deposit Title
        JLabel depositTitle = new JLabel("Deposit", JLabel.CENTER);
        depositTitle.setFont(new Font("Arial", Font.BOLD,18));

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(depositTitle, gbc);

        // Deposit Field
        JTextField depositField = new JTextField();

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(depositField, gbc);

        // Deposit Button
        JButton depositButton = new JButton("Deposit");

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(depositButton, gbc);

        // Status Label
        JLabel statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC,14));

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(statusLabel, gbc);

        frame.add(panel);
        frame.setVisible(true);

        // Deposit Action
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = depositField.getText().trim();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a deposit amount.");
                    return;
                }

                try {

                    double amount = Double.parseDouble(input);

                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(frame, "Please enter a positive amount.");
                        return;
                    }

                    new Thread(() -> {

                        account.deposit(amount);

                        SwingUtilities.invokeLater(() -> {
                            balanceAmount.setText("£" + account.getBalance());
                            statusLabel.setText("Deposit successful.");
                            depositField.setText("");
                        });

                    }).start();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter numbers only.");
                }
            }
        });

        // Withdraw Action
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = withdrawField.getText().trim();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a withdrawal amount.");
                    return;
                }

                try {

                    double amount = Double.parseDouble(input);

                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(frame, "Please enter a positive amount.");
                        return;
                    }

                    new Thread(() -> {

                        account.withdraw(amount);

                        SwingUtilities.invokeLater(() -> {
                            balanceAmount.setText("£" + account.getBalance());
                            statusLabel.setText("Withdrawal processed.");
                            withdrawField.setText("");
                        });

                    }).start();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter numbers only.");
                }
            }
        });
    }
}
