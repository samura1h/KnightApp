package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @Test
    void testSendAsyncDoesNotThrowException() {
        // Перевіряємо, що асинхронна відправка не кидає виключень, 
        // навіть якщо налаштування пошти некоректні або відсутній інтернет.
        // Це гарантує fail-safe поведінку.
        
        assertDoesNotThrow(() -> {
            EmailService.sendAsync("Test Subject", "Test Body");
        }, "sendAsync should catch exceptions and not crash the application");
    }
    
    @Test
    void testSendDoesNotThrowException() {
        // Також перевіряємо синхронну відправку.
        // Оскільки ми не маємо дійсних облікових даних у тесті, ми очікуємо,
        // що метод всередині перехопить MessagingException і залогує помилку, не кидаючи RuntimeException.
        assertDoesNotThrow(() -> {
            EmailService.send("Test Subject", "Test Body");
        }, "send should catch exceptions internally and not crash");
    }
}
