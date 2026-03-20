import java.io.Serializable;
import java.time.LocalDate;

/**
 * SavingsAccount extends BankAccount to provide a savings-specific account.
 * Key differences from a regular account:
 * - Earns monthly interest automatically applied to the balance.
 * - Limits the number of withdrawals allowed per calendar month.
 * - Interest rate is higher to encourage saving.
 */
public class SavingsAccount extends BankAccount {

    // Annual interest rate expressed as a decimal (0.02 = 2% monthly)
    private double interestRate = 0.02;

    // Maximum number of withdrawals allowed per calendar month
    private int withdrawalLimit = 3;

    // Tracks how many withdrawals have been made in the current month
    private int withdrawalsThisMonth = 0;

    // Records the date of the last withdrawal counter reset
    // Used to detect when a new month has started
    private LocalDate lastResetDate = LocalDate.now();

    /**
     * Creates a savings account with login credentials and an initial balance.
     * Calls the parent BankAccount constructor to set up shared fields.
     */
    public SavingsAccount(String accountId, String username, double initialBalance) {
        super(accountId, username, initialBalance);
    }

    /**
     * Applies monthly interest to the savings account balance.
     * Interest is calculated as: balance * interestRate
     * The result is deposited into the account, which also records
     * it automatically in the transaction history.
     *
     * Example: £100 balance at 2% → £2.00 interest added → new balance £102.00
     */
    public void applyInterest() {
        double interest = getBalance() * interestRate;

        // deposit() handles adding to balance and recording the transaction
        deposit(interest);
    }

    /**
     * Withdraws money from the savings account, subject to a monthly limit.
     *
     * Before processing the withdrawal:
     * - Checks if a new calendar month has started and resets the counter if so.
     * - Rejects the withdrawal if the monthly limit has already been reached.
     *
     * If checks pass, the parent withdraw() method handles the actual deduction
     * and validation (e.g. insufficient funds check).
     */
    @Override
    public synchronized void withdraw(double amount) {

        // Check if we've moved into a new calendar month
        LocalDate now = LocalDate.now();
        if (now.getMonth() != lastResetDate.getMonth() || now.getYear() != lastResetDate.getYear()) {

            // Reset the counter for the new month
            withdrawalsThisMonth = 0;
            lastResetDate = now;
        }

        // Block the withdrawal if the monthly limit has been reached
        if (withdrawalsThisMonth >= withdrawalLimit) {
            throw new IllegalStateException("Monthly withdrawal limit of " + withdrawalLimit + " reached.");
        }

        // Delegate to BankAccount's withdraw() for balance check and transaction recording
        super.withdraw(amount);

        // Increment the withdrawal counter after a successful withdrawal
        withdrawalsThisMonth++;
    }

    /**
     * Returns how many withdrawals the user can still make this month.
     * Useful for displaying a warning in the UI before the limit is hit.
     */
    public int getWithdrawalsRemaining() {
        return withdrawalLimit - withdrawalsThisMonth;
    }
}
