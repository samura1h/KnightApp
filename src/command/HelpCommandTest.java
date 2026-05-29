package command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {

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

    @Test
    void testExecute() throws UnsupportedEncodingException {
        HelpCommand helpCommand = new HelpCommand();
        helpCommand.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо ключові елементи виводу
        assertTrue(output.contains("--- ДОВІДКА ---"), "Має бути заголовок");
        assertTrue(output.contains("1. Створити лицаря"), "Має бути пункт 1");
        assertTrue(output.contains("11. Вихід"), "Має бути останній пункт");
    }
}