import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void testInterestApplied() {
        SavingsAccount savings = new SavingsAccount("SAV1", "user", 100);

        savings.applyInterest();

        assertTrue(savings.getBalance() > 100);
    }

    @Test
    void testWithdrawalLimit() {
        SavingsAccount savings = new SavingsAccount("SAV1", "user", 1000);

        savings.withdraw(100);
        savings.withdraw(100);
        savings.withdraw(100);

        assertThrows(IllegalStateException.class, () -> {
            savings.withdraw(100);
        });
    }

    @Test
    void testWithdrawalsRemaining() {
        SavingsAccount savings = new SavingsAccount("SAV1", "user", 1000);

        savings.withdraw(100);

        assertEquals(2, savings.getWithdrawalsRemaining());
    }
}