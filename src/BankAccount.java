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
    private final String accountId;

    // Each user has a savings account linked to their main account
    private final SavingsAccount savingsAccount;

    /**
     * Creates a bank account.
     * Automatically creates a linked SavingsAccount with a zero starting balance.
     */
    public BankAccount(String accountId, String username, double initialBalance) {
        this.accountId = accountId;
        this.username = username;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();

        // Automatically create a savings account linked to this account
        this.savingsAccount = accountId.endsWith("-SAV")
                ? null
                : new SavingsAccount(accountId + "-SAV", username, 0);
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
     * Adds money to the account.
     */
    public synchronized void deposit(double amount) {
        validateAmount(amount);

        balance += amount;

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
        validateAmount(amount);

        if (balance < amount) {
            throw new IllegalStateException("Insufficient funds for withdrawal.");
        }

        balance -= amount;

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
     */
    public synchronized double getBalance() {
        return balance;
    }

    /**
     * Returns a read-only version of the transaction history.
     */
    public synchronized List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Stores a transaction in the history.
     */
    public synchronized void recordTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Ensures the amount entered is valid.
     */
    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}