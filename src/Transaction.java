import java.time.LocalDateTime;

/**
 * Represents a single banking transaction.
 * A transaction records what happened in the account
 * (deposit or withdrawal), the amount, and when it occurred.
 */
public class Transaction {

    /**
     * Enum used to clearly define the type of transaction.
     * Using an enum prevents invalid values like random strings.
     */
    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }

    // Type of transaction (deposit or withdrawal)
    private final Type type;

    // Amount of money involved in the transaction
    private final double amount;

    // Timestamp recording when the transaction occurred
    private final LocalDateTime timestamp;

    // Optional description or note for the transaction
    private final String note;

    /**
     * Constructor used to create a new transaction record.
     * The timestamp is automatically generated when the transaction is created.
     */
    public Transaction(Type type, double amount, String note) {
        this.type = type;
        this.amount = amount;
        this.note = note;

        // Automatically capture the current date/time
        this.timestamp = LocalDateTime.now();
    }

    // Getter methods allow read-only access to transaction data

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getNote() {
        return note;
    }

    /**
     * Converts the transaction object into a readable string.
     * This is useful when displaying transaction history in the UI.
     */
    @Override
    public String toString() {
        return timestamp + " | " + type + " | £" + amount +
                (note != null ? " | " + note : "");
    }
}