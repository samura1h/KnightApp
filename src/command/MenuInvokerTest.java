package command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MenuInvokerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    // --- Mock Команда ---
    // Проста команда, яка просто ставить прапорець "виконано"
    static class MockCommand implements Command {
        public boolean executed = false;
        @Override
        public void execute() {
            executed = true;
            System.out.println("Mock executed!");
        }
    }

    private Scanner mockScanner(String input) {
        // Додаємо перенос рядка, щоб nextLine() спрацював коректно
        String fullInput = input + System.lineSeparator();
        return new Scanner(new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    @Test
    void testRun_SelectByNumber() {
        // Сценарій: В меню є пункт "1. Test". Користувач вводить "1".

        Scanner scanner = mockScanner("1");
        MenuInvoker invoker = new MenuInvoker(scanner);
        MockCommand cmd = new MockCommand();

        invoker.register("1. Test Command", cmd);

        // Запускаємо invoker. Оскільки там while(true), він впаде, коли закінчиться ввід.
        // Це очікувано, тому обгортаємо в try-catch.
        try {
            invoker.run();
        } catch (NoSuchElementException e) {
            // Ігноруємо помилку закінчення вводу
        }

        // Головна перевірка: чи виконалась команда ПЕРЕД тим, як сканер спорожнів?
        assertTrue(cmd.executed, "Команда мала виконатися при введенні '1'");
    }

    @Test
    void testRun_SelectByFullName() {
        // Сценарій: Вводимо повну назву пункту "Exit"

        Scanner scanner = mockScanner("Exit");
        MenuInvoker invoker = new MenuInvoker(scanner);
        MockCommand cmd = new MockCommand();

        invoker.register("Exit", cmd);

        try {
            invoker.run();
        } catch (NoSuchElementException e) { }

        assertTrue(cmd.executed, "Команда мала виконатися при введенні 'Exit'");
    }

    @Test
    void testRun_InvalidCommand() throws UnsupportedEncodingException {
        // Сценарій: Вводимо "99", такої команди немає

        Scanner scanner = mockScanner("99");
        MenuInvoker invoker = new MenuInvoker(scanner);
        MockCommand cmd = new MockCommand(); // Ця команда не має виконатись

        invoker.register("1. Test", cmd);

        try {
            invoker.run();
        } catch (NoSuchElementException e) { }

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertFalse(cmd.executed, "Команда НЕ мала виконатися");
        assertTrue(output.contains("Невідома команда"), "Має вивести повідомлення про помилку");
    }
}