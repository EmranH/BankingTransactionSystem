import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a bank account that supports deposits, withdrawals,
 * and stores a history of transactions.
 * Each account also has an associated SavingsAccount.
 */
public class BankAccount implements Serializable {

    // Current balance of the account
    private double balance;

    // List used to store all past transactions
    private final List<Transaction> transactions;

    // User identity fields
    private final String username;
    private final String pin;
    private final String accountId;

    // Each user has a savings account linked to their main account
    private final SavingsAccount savingsAccount;

    /**
     * Creates a bank account with login credentials.
     * Automatically creates a linked SavingsAccount with a zero starting balance.
     * A separate account ID is generated for the savings account by appending "-SAV".
     */
    public BankAccount(String accountId, String username, String pin, double initialBalance) {
        this.accountId = accountId;
        this.username = username;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();

        // Automatically create a savings account linked to this account
        this.savingsAccount = new SavingsAccount(accountId + "-SAV", username, pin, 0);
    }

    public String getUsername() {
        return username;
    }

    public String getAccountId() {
        return accountId;
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    /**
     * Verifies login credentials.
     */
    public boolean authenticate(String username, String pin) {
        return this.username.equals(username) && this.pin.equals(pin);
    }

    /**
     * Adds money to the account.
     * The method is synchronized so multiple threads cannot
     * modify the balance at the same time.
     */
    public synchronized void deposit(double amount) {

        // Validate the amount before processing
        validateAmount(amount);

        // Update account balance
        balance += amount;

        // Record the deposit in the transaction history
        recordTransaction(
                new Transaction(
                        Transaction.Type.DEPOSIT,
                        amount,
                        "Deposit made"
                )
        );
    }

    /**
     * Removes money from the account if sufficient funds exist.
     */
    public synchronized void withdraw(double amount) {

        // Ensure withdrawal amount is valid
        validateAmount(amount);

        // Prevent withdrawing more money than available
        if (balance < amount) {
            throw new IllegalStateException("Insufficient funds for withdrawal.");
        }

        // Deduct money from the balance
        balance -= amount;

        // Record the withdrawal in the transaction history
        recordTransaction(
                new Transaction(
                        Transaction.Type.WITHDRAWAL,
                        amount,
                        "Withdrawal made"
                )
        );
    }

    /**
     * Returns the current balance.
     * Method is synchronized to ensure consistency in multithreaded environments.
     */
    public synchronized double getBalance() {
        return balance;
    }

    /**
     * Returns a read-only version of the transaction history.
     * Collections.unmodifiableList prevents external code
     * from modifying the internal transaction list.
     */
    public synchronized List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Internal helper method responsible for storing a transaction.
     * Keeping this logic separate improves readability and maintainability.
     */
    public synchronized void recordTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Ensures the amount entered is valid.
     * Prevents negative or zero transactions.
     */
    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}