import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bank class responsible for managing multiple accounts.
 * Uses a Map where the key is the account ID and the value is the BankAccount object.
 */
public class Bank {

    // Stores all accounts in the bank
    private final Map<String, BankAccount> accounts;

    public Bank() {
        // HashMap allows fast lookup of accounts by ID
        this.accounts = new HashMap<>();
    }

    /**
     * Creates a new bank account with an automatically generated ID.
     * @param initialBalance starting balance
     * @return account ID
     */
    public synchronized String createAccount(double initialBalance) {

        // Generate a unique account ID
        String accountId = generateAccountId();

        // Create the new account
        BankAccount account = new BankAccount(initialBalance);

        // Store it in the map
        accounts.put(accountId, account);

        return accountId;
    }

    /**
     * Retrieves an account by ID.
     */
    public synchronized BankAccount getAccount(String accountId) {

        BankAccount account = accounts.get(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        return account;
    }

    /**
     * Transfers money between two accounts.
     *
     * The operation must be atomic:
     * - If any part fails, the transfer does not complete.
     * - Prevents inconsistent states where money disappears or duplicates.
     */
    public synchronized void transfer(String fromId, String toId, double amount) {

        // Validate that both accounts exist
        BankAccount fromAccount = accounts.get(fromId);
        BankAccount toAccount = accounts.get(toId);

        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account not found: " + fromId);
        }

        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account not found: " + toId);
        }

        // Prevent transferring to the same account
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        // Perform withdrawal first (this may throw insufficient funds exception)
        fromAccount.withdraw(amount);

        // Deposit into the receiving account
        toAccount.deposit(amount);

        /*
         * Record additional transaction notes to clarify that this was a transfer.
         * This helps when auditing the transaction history later.
         */
        fromAccount.recordTransaction(
                new Transaction(
                        Transaction.Type.WITHDRAWAL,
                        amount,
                        "Transfer to account " + toId
                )
        );

        toAccount.recordTransaction(
                new Transaction(
                        Transaction.Type.DEPOSIT,
                        amount,
                        "Transfer from account " + fromId
                )
        );
    }
    /**
     * Generates a unique account ID using UUID.
     * This ensures no two accounts share the same ID.
     */
    private String generateAccountId() {

        return "ACC-" + UUID.randomUUID().toString().substring(0,8);
    }
}