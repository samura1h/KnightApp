import repository.DatabaseManager;
import service.EmailService;
import service.LoggerService;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            service.LoggerService.error("Uncaught exception in thread " + thread.getName(), throwable);
        });

        try {
            new ProcessBuilder("cmd", "/c", "chcp", "65001")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .waitFor();
        } catch (Exception ignored) {}

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        boolean hasModulePath = false;
        try {
            for (String arg : java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                if (arg.contains("--module-path")) {
                    hasModulePath = true;
                    break;
                }
            }
        } catch (Throwable ignored) {}

        boolean isChildProcess = false;
        for (String arg : args) {
            if ("--child-process".equalsIgnoreCase(arg)) {
                isChildProcess = true;
                break;
            }
        }

        if (!hasModulePath && !isChildProcess) {
            try {
                String javaHome = System.getProperty("java.home");
                String javaBin = javaHome + java.io.File.separator + "bin" + java.io.File.separator + "java";
                String classpath = System.getProperty("java.class.path");

                java.util.List<String> command = new java.util.ArrayList<>();
                command.add(javaBin);
                command.add("--module-path");
                command.add("C:/javafx-sdk/lib");
                command.add("--add-modules");
                command.add("javafx.controls,javafx.fxml,javafx.graphics,javafx.base");
                command.add("-cp");
                command.add(classpath);
                command.add("Main");
                command.add("--child-process");
                for (String arg : args) {
                    command.add(arg);
                }

                ProcessBuilder builder = new ProcessBuilder(command);
                builder.inheritIO();
                Process process = builder.start();
                System.exit(process.waitFor());
                return;
            } catch (Exception e) {
                System.err.println("Failed to self-restart to configure JavaFX: " + e.getMessage());
            }
        }

        LoggerService.info("APPLICATION LAUNCH (System Start)");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LoggerService.info("APPLICATION SHUTDOWN (System Stop)");
        }));

        System.out.println("=========================================");
        System.out.println("   KNIGHT ORDER MANAGEMENT SYSTEM v1.0   ");
        System.out.println("   (Graphical User Interface Mode)       ");
        System.out.println("=========================================");
        System.out.println(">>> Launching GUI...");
        
        try {
            LoggerService.info("APPLICATION LAUNCH (GUI Mode)");
            gui.GuiMain.main(args);
        } catch (Throwable t) {
            System.err.println("CRITICAL ERROR: Failed to launch GUI.");
            System.err.println("Error details: " + t.getMessage());
            t.printStackTrace();
        }
    }
}