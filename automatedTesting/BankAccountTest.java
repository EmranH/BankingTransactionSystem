import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void testDeposit() {
        BankAccount account = new BankAccount("ACC1", "user", 100);
        account.deposit(50);
        assertEquals(150, account.getBalance());
    }

    @Test
    void testWithdraw() {
        BankAccount account = new BankAccount("ACC1", "user", 100);
        account.withdraw(40);
        assertEquals(60, account.getBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        BankAccount account = new BankAccount("ACC1", "user", 100);

        assertThrows(IllegalStateException.class, () -> {
            account.withdraw(200);
        });
    }

    @Test
    void testInvalidAmount() {
        BankAccount account = new BankAccount("ACC1", "user", 100);

        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(-10);
        });
    }

    @Test
    void testTransactionRecorded() {
        BankAccount account = new BankAccount("ACC1", "user", 100);
        account.deposit(50);

        assertEquals(1, account.getTransactionHistory().size());
    }
}