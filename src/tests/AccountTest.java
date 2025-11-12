//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import shared.Account;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class AccountTest {
//    private Account account;
//
//    @BeforeEach
//    public void setUp() {
//        account = new Account("reoch123", "securestpassword123");
//    }
//
//    @Test
//    public void testAccountCreation() {
//        assertEquals("reoch123", account.getUsername());
//        assertEquals(1000.0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testAddBalance() {
//        account.addBalance(100);
//        assertEquals(1100.0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testDeductBalance() {
//        boolean result = account.deductBalance(100);
//        assertTrue(result);
//        assertEquals(900.0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testDeductBalanceInsufficientFunds() {
//        boolean result = account.deductBalance(2000);
//        assertFalse(result);
//        assertEquals(1000.0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testDeductExactBalance() {
//        boolean result = account.deductBalance(1000);
//        assertTrue(result);
//        assertEquals(0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testPasswordIsCorrect() {
//        assertTrue(account.checkPassword("securestpassword123"));
//    }
//
//    @Test
//    public void testPasswordIncorrect() {
//        assertFalse(account.checkPassword("wrongpasswordxd"));
//    }
//
//    @Test
//    public void testMultipleTransactions() {
//        account.addBalance(100);
//        account.deductBalance(300);
//        account.addBalance(50);
//        account.deductBalance(500);
//
//        assertEquals(350.0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testCantDeductNegativeAmount() {
//        assertThrows(IllegalArgumentException.class, () -> account.deductBalance(-100));
//        assertEquals(1000.0, account.getBalance(), 0.01);
//    }
//
//    @Test
//    public void testCantAddNegativeAmount() {
//        assertThrows(IllegalArgumentException.class, () -> account.addBalance(-100));
//        assertEquals(1000.0, account.getBalance(), 0.01);
//    }
//}
