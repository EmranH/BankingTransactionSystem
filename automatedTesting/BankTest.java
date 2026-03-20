import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void testCreateAccount() {
        Bank bank = new Bank();

        bank.createAccount("user1", 100);
        BankAccount account = bank.getAccount("user1");

        assertNotNull(account);
        assertEquals("user1", account.getUsername());
    }

    @Test
    void testDuplicateUser() {
        Bank bank = new Bank();
        bank.createAccount("user1", 100);

        assertThrows(IllegalArgumentException.class, () -> {
            bank.createAccount("user1", 200);
        });
    }

    @Test
    void testGetInvalidUser() {
        Bank bank = new Bank();

        assertThrows(IllegalArgumentException.class, () -> {
            bank.getAccount("unknown");
        });
    }

    @Test
    void testTransfer() {
        Bank bank = new Bank();

        String id1 = bank.createAccount("user1", 100);
        String id2 = bank.createAccount("user2", 50);

        bank.transfer(id1, id2, 50);

        BankAccount acc1 = bank.getAccount("user1");
        BankAccount acc2 = bank.getAccount("user2");

        assertEquals(50, acc1.getBalance());
        assertEquals(100, acc2.getBalance());
    }

    @Test
    void testTransferSameAccount() {
        Bank bank = new Bank();

        String id = bank.createAccount("user1", 100);

        assertThrows(IllegalArgumentException.class, () -> {
            bank.transfer(id, id, 50);
        });
    }
}