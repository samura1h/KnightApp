package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @Test
    void testSendAsyncDoesNotThrowException() {

        assertDoesNotThrow(() -> {
            EmailService.sendAsync("Test Subject", "Test Body");
        }, "sendAsync should catch exceptions and not crash the application");
    }
    
    @Test
    void testSendDoesNotThrowException() {
        assertDoesNotThrow(() -> {
            EmailService.send("Test Subject", "Test Body");
        }, "send should catch exceptions internally and not crash");
    }

    @Test
    void testSendExceptionHandling() {
        org.mockito.MockedStatic<javax.mail.internet.InternetAddress> mocked = 
             org.mockito.Mockito.mockStatic(javax.mail.internet.InternetAddress.class, org.mockito.Mockito.CALLS_REAL_METHODS);
        try {
            mocked.when(() -> javax.mail.internet.InternetAddress.parse(org.mockito.Mockito.anyString()))
                  .thenThrow(new javax.mail.internet.AddressException("Mocked Exception"));
            assertDoesNotThrow(() -> EmailService.send("Subj", "Body"));
        } finally {
            mocked.close();
        }
    }

    @Test
    void testTestModeToggle() {
        boolean original = EmailService.isTestMode();
        try {
            EmailService.setTestMode(true);
            assertTrue(EmailService.isTestMode());
            EmailService.setTestMode(false);
            assertFalse(EmailService.isTestMode());
        } finally {
            EmailService.setTestMode(original);
        }
    }

    @Test
    void testAuthenticator() throws Exception {
        Class<?> authClass = Class.forName("service.EmailService$1");
        java.lang.reflect.Constructor<?> constructor = authClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        javax.mail.Authenticator auth = (javax.mail.Authenticator) constructor.newInstance();
        
        java.lang.reflect.Method method = javax.mail.Authenticator.class.getDeclaredMethod("getPasswordAuthentication");
        method.setAccessible(true);
        javax.mail.PasswordAuthentication pa = (javax.mail.PasswordAuthentication) method.invoke(auth);
        
        assertEquals("bazgoten@gmail.com", pa.getUserName());
        assertEquals("aeyanpsvvkioqwff", pa.getPassword());
    }

    @Test
    void testConstructorForCoverage() {
        assertDoesNotThrow(() -> new EmailService());
    }
}
