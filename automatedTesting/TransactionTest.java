import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testTransactionCreation() {
        Transaction t = new Transaction(Transaction.Type.DEPOSIT, 100, "Test");

        assertEquals(100, t.getAmount());
        assertEquals(Transaction.Type.DEPOSIT, t.getType());
        assertNotNull(t.getTimestamp());
    }

    @Test
    void testToString() {
        Transaction t = new Transaction(Transaction.Type.DEPOSIT, 100, "Test");

        String output = t.toString();

        assertTrue(output.contains("DEPOSIT"));
        assertTrue(output.contains("100"));
    }
}
