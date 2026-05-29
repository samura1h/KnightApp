import command.*;
import repository.DatabaseManager;
import repository.EquipmentRepository;
import repository.KnightRepository;
import service.EmailService;
import service.KnightManager;
import service.LoggerService; // Імпорт
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // === Set UTF-8 for correct character output ===
        // Change Windows console code page to UTF-8
        try {
            new ProcessBuilder("cmd", "/c", "chcp", "65001")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .waitFor();
        } catch (Exception ignored) {}

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // Log application launch event
        LoggerService.info("APPLICATION LAUNCH (System Start)");

        // Send startup notification email (asynchronously)
        EmailService.sendAsync("App Started", "Application has started.");

        // Shutdown hook to log exit event
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LoggerService.info("APPLICATION SHUTDOWN (System Stop)");
            EmailService.send("App Stopped", "Application has stopped.");
        }));

        // Check if user wants CLI via arguments
        boolean startCli = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg)) {
                startCli = true;
                break;
            }
        }

        if (startCli) {
            System.out.println("=========================================");
            System.out.println("   KNIGHT ORDER MANAGEMENT SYSTEM v1.0   ");
            System.out.println("   (Command Line Interface Mode)         ");
            System.out.println("=========================================");
            
            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            LoggerService.info("APPLICATION LAUNCH (CLI Mode)");
            
            // Ініціалізуємо базу даних SQLite
            DatabaseManager.getInstance();
            
            EquipmentRepository equipRepo = new EquipmentRepository();
            KnightRepository knightRepo = new KnightRepository();
            knightRepo.loadData(); // Завантажуємо лицарів з БД
            KnightManager knightManager = new KnightManager(knightRepo, equipRepo);

            MenuInvoker menu = new MenuInvoker(scanner);

            menu.register("1. Create or Load Knight", new CreateOrLoadKnightCommand(knightManager, scanner));
            menu.register("2. Delete Knight", new DeleteKnightCommand(knightManager, scanner));
            menu.register("3. Quick Select Active Knight", new SelectKnightCommand(knightManager, scanner));
            menu.register("4. Equip Knight", new EquipKnightCommand(knightManager, equipRepo, scanner));
            menu.register("5. Knight Status", new ShowKnightStatusCommand(knightManager));
            menu.register("6. Equipment Cost", new CalcEquipmentCostCommand(knightManager, equipRepo, scanner));
            menu.register("7. Sort Ammunition", new SortEquipmentCommand(knightManager, equipRepo, scanner));
            menu.register("8. Find by Price", new FindEquipmentByPriceCommand(knightManager, equipRepo, scanner));
            menu.register("9. Reload System", new ReloadSystemCommand(knightManager));
            menu.register("10. Help", new HelpCommand());

            menu.register("11. Exit", () -> {
                LoggerService.info("User requested exit.");
                knightManager.saveAll();
                System.exit(0);
            });

            menu.run();
        } else {
            // Default mode: Graphical User Interface
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
                
                if (t instanceof UnsupportedClassVersionError || 
                   (t.getCause() != null && t.getCause() instanceof UnsupportedClassVersionError)) {
                    System.err.println("\n---------------------------------------------------------");
                    System.err.println("VERSION MISMATCH DETECTED!");
                    System.err.println("Your current Java version: " + System.getProperty("java.version"));
                    System.err.println("JavaFX 26 requires at least JDK 24 (Class Version 68.0).");
                    System.err.println("PLEASE USE JDK 26 TO RUN THIS APPLICATION.");
                    System.err.println("Found JDK 26 at: C:\\Program Files\\Java\\jdk-26.0.1");
                    System.err.println("---------------------------------------------------------\n");
                }
                t.printStackTrace();
                
                System.out.println("\nFallback: Would you like to start the CLI instead? (y/n)");
                Scanner fallbackScanner = new Scanner(System.in);
                if (fallbackScanner.nextLine().equalsIgnoreCase("y")) {
                    main(new String[]{"--cli"});
                }
            }
        }
    }
}