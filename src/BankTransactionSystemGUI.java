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

    // 🎨 Theme
    private final Color PRIMARY = new Color(25, 118, 210);
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;

    private final Font TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private final Font SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TEXT = new Font("Segoe UI", Font.PLAIN, 16);

    public BankTransactionSystemGUI() {
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
        strength.setFont(TEXT);

        pass.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                String s = getStrength(new String(pass.getPassword()));
                strength.setText("Strength: " + s);
                strength.setForeground(s.equals("Strong") ? PRIMARY : Color.RED);
            }
            public void insertUpdate(DocumentEvent e){update();}
            public void removeUpdate(DocumentEvent e){update();}
            public void changedUpdate(DocumentEvent e){update();}
        });

        JButton register = createButton("Register");
        JButton cancel = createButton("Cancel");

        d.add(label("Username")); d.add(user);
        d.add(label("Password")); d.add(pass);
        d.add(label("Confirm")); d.add(confirm);
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

        frame = new JFrame("Bank System");
        frame.setMinimumSize(new Dimension(800, 700));
        frame.setSize(900, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(15,15));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        root.setBackground(BACKGROUND);

        // ===== HEADER (USERNAME + BALANCE) =====
        JPanel header = new JPanel(new GridLayout(2,1));
        header.setBackground(BACKGROUND);

        JLabel userLabel = new JLabel("Welcome, " + currentAccount.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);

        balanceLabel = new JLabel("Balance: £" + currentAccount.getBalance());
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        balanceLabel.setForeground(PRIMARY);

        header.add(userLabel);
        header.add(balanceLabel);

        // ===== MAIN ACCOUNT (BIGGER) =====
        JPanel mainPanel = createCard("Main Account");
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;

        JTextField depositField = input();
        JTextField withdrawField = input();
        JTextField transferField = input();
        JTextField toField = input();

        JButton depositBtn = createButton("Deposit");
        JButton withdrawBtn = createButton("Withdraw");
        JButton transferBtn = createButton("Transfer");
        JButton historyBtn = createButton("View Transactions");

        // Deposit
        gbc.gridx=0; gbc.gridy=y;
        mainPanel.add(label("Deposit amount (£):"), gbc);

        gbc.gridx=1; gbc.weightx=2;
        mainPanel.add(depositField, gbc);

        gbc.gridx=2; gbc.weightx=0;
        mainPanel.add(depositBtn, gbc);

        y++;

        // Withdraw
        gbc.gridx=0; gbc.gridy=y;
        mainPanel.add(label("Withdraw amount (£):"), gbc);

        gbc.gridx=1; gbc.weightx=2;
        mainPanel.add(withdrawField, gbc);

        gbc.gridx=2; gbc.weightx=0;
        mainPanel.add(withdrawBtn, gbc);

        y++;

        // Account ID display
        JLabel idLabel = new JLabel("Your Account ID: " + currentAccount.getAccountId());
        idLabel.setFont(TEXT);
        idLabel.setForeground(Color.GRAY);

        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=3;
        mainPanel.add(idLabel, gbc);

        y++; gbc.gridwidth=1;

        // Transfer
        gbc.gridx=0; gbc.gridy=y;
        mainPanel.add(label("Transfer to (Account ID):"), gbc);

        gbc.gridx=1;
        mainPanel.add(toField, gbc);

        y++;

        gbc.gridx=0; gbc.gridy=y;
        mainPanel.add(label("Transfer amount (£):"), gbc);

        gbc.gridx=1;
        mainPanel.add(transferField, gbc);

        gbc.gridx=2;
        mainPanel.add(transferBtn, gbc);

        y++;

        gbc.gridx=1; gbc.gridy=y;
        mainPanel.add(historyBtn, gbc);

        // ===== ACTIONS =====
        depositBtn.addActionListener(e -> handle(() ->
                currentAccount.deposit(Double.parseDouble(depositField.getText()))
        ));

        withdrawBtn.addActionListener(e -> handle(() ->
                currentAccount.withdraw(Double.parseDouble(withdrawField.getText()))
        ));

        transferBtn.addActionListener(e -> handle(() ->
                bank.transfer(currentAccount.getAccountId(),
                        toField.getText(),
                        Double.parseDouble(transferField.getText()))
        ));

        historyBtn.addActionListener(e -> showHistory());

        // ===== SAVINGS (SMALLER + INFO) =====
        JPanel savingsPanel = createCard("Savings Account");
        savingsPanel.setLayout(new GridBagLayout());

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(8,8,8,8);
        sgbc.fill = GridBagConstraints.HORIZONTAL;

        int sy = 0;

        JLabel info = new JLabel("Savings earns 2% monthly interest. Max 3 withdrawals/month.");
        info.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        info.setForeground(Color.DARK_GRAY);

        sgbc.gridx=0; sgbc.gridy=sy; sgbc.gridwidth=3;
        savingsPanel.add(info, sgbc);

        sy++; sgbc.gridwidth=1;

        JTextField sDep = input();
        JTextField sWith = input();

        savingsLabel = new JLabel("Savings: £" + currentAccount.getSavingsAccount().getBalance());
        savingsLabel.setFont(TEXT);

        JButton sDepBtn = createButton("Deposit");
        JButton sWithBtn = createButton("Withdraw");

        // Deposit
        sgbc.gridx=0; sgbc.gridy=sy;
        savingsPanel.add(label("Deposit (£):"), sgbc);

        sgbc.gridx=1;
        savingsPanel.add(sDep, sgbc);

        sgbc.gridx=2;
        savingsPanel.add(sDepBtn, sgbc);

        sy++;

        // Withdraw
        sgbc.gridx=0; sgbc.gridy=sy;
        savingsPanel.add(label("Withdraw (£):"), sgbc);

        sgbc.gridx=1;
        savingsPanel.add(sWith, sgbc);

        sgbc.gridx=2;
        savingsPanel.add(sWithBtn, sgbc);

        sy++;

        sgbc.gridx=1; sgbc.gridy=sy;
        savingsPanel.add(savingsLabel, sgbc);

        // actions
        sDepBtn.addActionListener(e -> handle(() ->
                currentAccount.getSavingsAccount().deposit(Double.parseDouble(sDep.getText()))
        ));

        sWithBtn.addActionListener(e -> handle(() ->
                currentAccount.getSavingsAccount().withdraw(Double.parseDouble(sWith.getText()))
        ));

        // ===== LOGOUT =====
        JButton logout = createButton("Logout");
        logout.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        // ===== LAYOUT (MAIN BIGGER THAN SAVINGS) =====
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BACKGROUND);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        // Main bigger
        c.gridx = 0; c.gridy = 0;
        c.weighty = 0.7;
        center.add(mainPanel, c);

        // Savings smaller
        c.gridy = 1;
        c.weighty = 0.3;
        center.add(savingsPanel, c);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(logout, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);

        frame.setContentPane(scroll);
        frame.setVisible(true);
    }

    // ================= HELPERS =================

    private JButton createButton(String text) {
        JButton b = new JButton(text);

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // FIX WHITE BUTTON BUG
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);

        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        b.setOpaque(true);
        b.setContentAreaFilled(true);

        b.setFont(TEXT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return b;
    }

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

    private JTextField input() {
        JTextField f = new JTextField();
        f.setFont(TEXT);
        return f;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(TEXT);
        return l;
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
        area.setFont(TEXT);

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