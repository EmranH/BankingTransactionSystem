import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.*;

/**
 * Bank class responsible for managing multiple accounts.
 * Uses a Map where the key is the username and the value is the BankAccount object.
 * Also handles automatic monthly interest application across all savings accounts.
 */
public class Bank implements Serializable {

    private Map<String, BankAccount> accountsByUsername;
    private transient ScheduledExecutorService scheduler;

    public Bank() {
        this.accountsByUsername = new HashMap<>();
        startInterestScheduler();
    }

    /**
     * Starts a background scheduler that automatically applies monthly interest
     * to all savings accounts every 30 days.
     */
    private void startInterestScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                this::applyMonthlyInterest,
                30,
                30,
                TimeUnit.DAYS
        );
    }

    /**
     * Applies monthly interest to every savings account in the bank.
     */
    public synchronized void applyMonthlyInterest() {
        for (BankAccount account : accountsByUsername.values()) {
            account.getSavingsAccount().applyInterest();
        }

        saveToFile();
    }

    /**
     * Creates a new user account.
     */
    public synchronized String createAccount(String username, String password, double initialBalance) {
        if (accountsByUsername.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String accountId = generateAccountId();
        BankAccount account = new BankAccount(accountId, username, password, initialBalance);
        accountsByUsername.put(username, account);

        return accountId;
    }

    public synchronized BankAccount login(String username, String password) {

        BankAccount account = accountsByUsername.get(username);

        if (account == null || !account.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        return account;
    }

    /**
     * Gets an account by username.
     */
    public synchronized BankAccount getAccount(String username) {
        BankAccount account = accountsByUsername.get(username);

        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        return account;
    }

    /**
     * Finds an account using its account ID.
     */
    private BankAccount getAccountById(String accountId) {
        for (BankAccount account : accountsByUsername.values()) {
            if (account.getAccountId().equals(accountId)) {
                return account;
            }
        }

        throw new IllegalArgumentException("Account not found: " + accountId);
    }

    /**
     * Transfers money between two accounts.
     */
    public synchronized void transfer(String fromId, String toId, double amount) {
        BankAccount fromAccount = getAccountById(fromId);
        BankAccount toAccount = getAccountById(toId);

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    /**
     * Generates a unique account ID.
     */
    private String generateAccountId() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Saves the entire bank system to file.
     */
    public void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("bank.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the bank system from file.
     * If the file does not exist, returns a new empty bank.
     */
    public static Bank loadFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("bank.dat"))) {
            Bank bank = (Bank) in.readObject();
            bank.startInterestScheduler();
            return bank;

        } catch (Exception e) {
            return new Bank();
        }
    }
}