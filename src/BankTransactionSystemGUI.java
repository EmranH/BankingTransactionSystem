import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankTransactionSystemGUI {
    private static BankAccount account = new BankAccount(1000);

    public static void main(String[] args) {
        // Create a frame for the GUI
        JFrame frame = new JFrame("Bank Transaction System");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create UI elements
        JPanel panel = new JPanel();
        JTextField depositField = new JTextField(10);
        JTextField withdrawField = new JTextField(10);
        JLabel balanceLabel = new JLabel("Balance: " + account.getBalance());

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");

        // Layout settings
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("Deposit Amount:"));
        panel.add(depositField);
        panel.add(depositButton);
        panel.add(new JLabel("Withdraw Amount:"));
        panel.add(withdrawField);
        panel.add(withdrawButton);
        panel.add(balanceLabel);

        frame.add(panel);
        frame.setVisible(true);

        // Action for deposit
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

    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            balanceLabel.setText("Balance: " + account.getBalance());
        }
    });
}).start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter numbers only.");
        }
    }
});

        // Action for withdrawal
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

    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            balanceLabel.setText("Balance: " + account.getBalance());
        }
    });
}).start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter numbers only.");
        }
    }
});
    }
}
