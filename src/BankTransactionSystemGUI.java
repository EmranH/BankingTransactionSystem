import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BankTransactionSystemGUI {

    private Bank bank;
    private BankAccount currentAccount;

    private JFrame frame;
    private JLabel balanceLabel;
    private JLabel savingsLabel;

    //Theme
    private final Color PRIMARY = new Color(25, 118, 210); // clean blue
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;

    private final Font TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TEXT = new Font("Segoe UI", Font.PLAIN, 16);

    public BankTransactionSystemGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        bank = Bank.loadFromFile();
        showLoginScreen();
    }

    // ================= LOGIN =================
    private void showLoginScreen() {

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] msg = {
                "Username:", username,
                "Password:", password
        };

        int option = JOptionPane.showConfirmDialog(null, msg, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) System.exit(0);

        try {
            currentAccount = bank.login(username.getText(), new String(password.getPassword()));
            createMainUI();
        } catch (Exception e) {
            int choice = JOptionPane.showConfirmDialog(null, "Login failed. Register?", "Error", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) showRegisterScreen();
            else showLoginScreen();
        }
    }

    // ================= REGISTER =================
    private void showRegisterScreen() {

        JDialog d = new JDialog((Frame) null, "Register", true);
        d.setSize(350,250);
        d.setLayout(new GridLayout(5,2,10,10));

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JLabel strength = new JLabel();

        pass.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                String s = getStrength(new String(pass.getPassword()));
                strength.setText(s);
                strength.setForeground(s.equals("Strong") ? PRIMARY : Color.RED);
            }
            public void insertUpdate(DocumentEvent e){update();}
            public void removeUpdate(DocumentEvent e){update();}
            public void changedUpdate(DocumentEvent e){update();}
        });

        JButton register = createButton("Register");
        JButton cancel = createButton("Cancel");

        d.add(new JLabel("Username")); d.add(user);
        d.add(new JLabel("Password")); d.add(pass);
        d.add(new JLabel("Confirm")); d.add(confirm);
        d.add(strength); d.add(new JLabel());
        d.add(register); d.add(cancel);

        register.addActionListener(e -> {
            try {
                if (!new String(pass.getPassword()).equals(new String(confirm.getPassword())))
                    throw new Exception("Passwords don't match");

                bank.createAccount(user.getText(), new String(pass.getPassword()), 0);
                bank.saveToFile();

                JOptionPane.showMessageDialog(d, "Account created");
                d.dispose();
                showLoginScreen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage());
            }
        });

        cancel.addActionListener(e -> {
            d.dispose();
            showLoginScreen();
        });

        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    // ================= MAIN UI =================
    private void createMainUI() {

        frame = new JFrame("Bank - " + currentAccount.getUsername());
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(15,15));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        root.setBackground(BACKGROUND);

        // ===== TOP BALANCE =====
        balanceLabel = new JLabel();
        balanceLabel.setFont(TITLE);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        balanceLabel.setForeground(PRIMARY);
        updateBalance();

        // ===== MAIN CARD =====
        JPanel mainCard = createCard("Main Account");

        JTextField depositField = new JTextField();
        JTextField withdrawField = new JTextField();
        JTextField transferField = new JTextField();
        JTextField toField = new JTextField();

        JButton deposit = createButton("Deposit");
        JButton withdraw = createButton("Withdraw");
        JButton transfer = createButton("Transfer");
        JButton history = createButton("History");

        deposit.addActionListener(e -> handle(() ->
                currentAccount.deposit(Double.parseDouble(depositField.getText()))
        ));

        withdraw.addActionListener(e -> handle(() ->
                currentAccount.withdraw(Double.parseDouble(withdrawField.getText()))
        ));

        transfer.addActionListener(e -> handle(() ->
                bank.transfer(currentAccount.getAccountId(), toField.getText(),
                        Double.parseDouble(transferField.getText()))
        ));

        history.addActionListener(e -> showHistory());

        mainCard.setLayout(new GridLayout(5,2,10,10));
        mainCard.add(new JLabel("Deposit")); mainCard.add(depositField);
        mainCard.add(deposit); mainCard.add(new JLabel());

        mainCard.add(new JLabel("Withdraw")); mainCard.add(withdrawField);
        mainCard.add(withdraw); mainCard.add(new JLabel());

        mainCard.add(new JLabel("To Account")); mainCard.add(toField);
        mainCard.add(new JLabel("Amount")); mainCard.add(transferField);
        mainCard.add(transfer); mainCard.add(history);

        // ===== SAVINGS CARD =====
        JPanel savingsCard = createCard("Savings");

        JTextField sDep = new JTextField();
        JTextField sWith = new JTextField();

        savingsLabel = new JLabel();
        updateSavings();

        JButton sDeposit = createButton("Deposit");
        JButton sWithdraw = createButton("Withdraw");

        sDeposit.addActionListener(e -> handle(() ->
                currentAccount.getSavingsAccount().deposit(Double.parseDouble(sDep.getText()))
        ));

        sWithdraw.addActionListener(e -> handle(() ->
                currentAccount.getSavingsAccount().withdraw(Double.parseDouble(sWith.getText()))
        ));

        savingsCard.setLayout(new GridLayout(3,2,10,10));
        savingsCard.add(new JLabel("Deposit")); savingsCard.add(sDep);
        savingsCard.add(sDeposit); savingsCard.add(new JLabel());
        savingsCard.add(new JLabel("Withdraw")); savingsCard.add(sWith);
        savingsCard.add(sWithdraw); savingsCard.add(savingsLabel);

        // ===== LOGOUT =====
        JButton logout = createButton("Logout");
        logout.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        // ===== LAYOUT =====
        JPanel center = new JPanel(new GridLayout(2,1,10,10));
        center.setBackground(BACKGROUND);
        center.add(mainCard);
        center.add(savingsCard);

        root.add(balanceLabel, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(logout, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ================= UI HELPERS =================
    private JPanel createCard(String title) {
        JPanel p = new JPanel();
        p.setBackground(CARD);

        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                title,
                0, 0,
                SUBTITLE
        ));

        return p;
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);

        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);

        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);

        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return b;
    }

    private void handle(Runnable r) {
        try {
            r.run();
            bank.saveToFile();
            updateBalance();
            updateSavings();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: £" + currentAccount.getBalance());
    }

    private void updateSavings() {
        savingsLabel.setText("Savings: £" + currentAccount.getSavingsAccount().getBalance());
    }

    private void showHistory() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : currentAccount.getTransactionHistory()) {
            sb.append(t).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);

        JOptionPane.showMessageDialog(frame, new JScrollPane(area), "Transactions", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getStrength(String p) {
        int score = 0;
        if (p.length() >= 8) score++;
        if (p.matches(".*[A-Z].*")) score++;
        if (p.matches(".*\\d.*")) score++;
        return score < 2 ? "Weak" : score == 2 ? "Medium" : "Strong";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTransactionSystemGUI::new);
    }
}