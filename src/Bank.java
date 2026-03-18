import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.*;

/**
 * Bank class responsible for managing multiple accounts.
 * Uses a Map where the key is the account ID and the value is the BankAccount object.
 * Also handles automatic monthly interest application across all savings accounts.
 */
public class Bank implements Serializable{

    // Stores all accounts in the bank by username
    private Map<String, BankAccount> accountsByUsername;

    // Scheduler is transient so it is not saved to file — it is recreated on load
    // Serializable classes cannot save live threads, so transient excludes it
    private transient ScheduledExecutorService scheduler;

    public Bank() {
        // HashMap allows fast lookup of accounts by ID
        this.accountsByUsername = new HashMap<>();

        // Start the monthly interest scheduler when the bank is created
        startInterestScheduler();
    }

    /**
     * Starts a background scheduler that automatically applies monthly interest
     * to all savings accounts.
     *
     * Uses a ScheduledExecutorService to run applyMonthlyInterest()
     * once every 30 days without any manual trigger needed.
     *
     * The scheduler is marked transient so it is not serialized —
     * it is always restarted fresh when the bank is loaded from file.
     */
    private void startInterestScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Schedule applyMonthlyInterest to run every 30 days
        // Initial delay of 30 days means it won't run immediately on startup
        scheduler.scheduleAtFixedRate(
                this::applyMonthlyInterest,
                30,   // initial delay before first run
                30,   // repeat every 30 days
                TimeUnit.DAYS
        );
    }

    /**
     * Applies monthly interest to every savings account in the bank.
     * Called automatically by the scheduler every 30 days.
     * Saves the updated balances to file after applying interest.
     */
    public synchronized void applyMonthlyInterest() {

        // Loop through all accounts and apply interest to each savings account
        for (BankAccount account : accountsByUsername.values()) {
            account.getSavingsAccount().applyInterest();
        }

        // Persist the updated balances to file
        saveToFile();
    }

    /**
     * Creates a new user account.
     */
    public synchronized String createAccount(String username, String pin, double initialBalance) {

        if (accountsByUsername.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String accountId = generateAccountId();

        BankAccount account = new BankAccount(accountId, username, pin, initialBalance);

        accountsByUsername.put(username, account);

        return accountId;
    }

    /**
     * Authenticates a user and returns their account.
     */
    public synchronized BankAccount login(String username, String pin) {

        BankAccount account = accountsByUsername.get(username);

        if (account == null || !account.authenticate(username, pin)) {
            throw new IllegalArgumentException("Invalid username or PIN.");
        }

        return account;
    }

    /**
     * Finds an account using its account ID.
     */
    private BankAccount getAccountById(String accountId) {

        // Loop through all accounts to find matching ID
        for (BankAccount account : accountsByUsername.values()) {
            if (account.getAccountId().equals(accountId)) {
                return account;
            }
        }

        throw new IllegalArgumentException("Account not found: " + accountId);
    }

    /**
     * Transfers money between two accounts.
     *
     * The operation must be atomic:
     * - If any part fails, the transfer does not complete.
     * - Prevents inconsistent states where money disappears or duplicates.
     */

    public synchronized void transfer(String fromId, String toId, double amount) {

        // Find accounts by account ID
        BankAccount fromAccount = getAccountById(fromId);
        BankAccount toAccount = getAccountById(toId);

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        // Perform atomic transfer
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    /**
     * Generates a unique account ID using UUID.
     * This ensures no two accounts share the same ID.
     */
    private String generateAccountId() {

        return "ACC-" + UUID.randomUUID().toString().substring(0,8);
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
     * If file does not exist, returns a new empty bank.
     */
    public static Bank loadFromFile() {

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("bank.dat"))) {

            // Read entire Bank object
            Bank bank = (Bank) in.readObject();

            // Restart the scheduler since transient fields are not saved
            bank.startInterestScheduler();

            return bank;

        } catch (Exception e) {

            // First run OR file missing → return new bank
            return new Bank();
        }
    }
}