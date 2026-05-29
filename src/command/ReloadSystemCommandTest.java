package command;

import service.KnightManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ReloadSystemCommandTest {

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

    // --- Stub для менеджера ---
    static class StubManager extends KnightManager {
        public boolean reloadCalled = false;

        public StubManager() { super(null, null); }

        @Override
        public void reloadSystem() {
            this.reloadCalled = true; // Фіксуємо виклик
        }
    }

    @Test
    void testExecute() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        ReloadSystemCommand command = new ReloadSystemCommand(manager);

        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // 1. Перевіряємо вивід попередження
        assertTrue(output.contains("!!! УВАГА !!!"), "Має бути попередження");

        // 2. Перевіряємо, чи викликався метод у менеджера
        assertTrue(manager.reloadCalled, "Метод manager.reloadSystem() мав бути викликаний");
    }
}