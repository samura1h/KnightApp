package repository;

import model.equipment.*;
// --- ІМПОРТИ ЛОГЕРА ---
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {
    private static final Logger logger = LogManager.getLogger(EquipmentRepository.class);

    private List<Ammunition> catalog = new ArrayList<>();
    private String fileName;

    public EquipmentRepository() {
        this("C:\\Users\\nazar\\OneDrive\\Desktop\\JavaP\\App_Prog_3\\Knight\\src\\ammunition.txt");
    }

    public EquipmentRepository(String fileName) {
        this.fileName = fileName;
        loadFromFile();
    }

    public void reload() {
        logger.info("Перезавантаження каталогу амуніції..."); // ЛОГ
        catalog.clear();
        loadFromFile();
    }

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                parseLine(line);
            }
            logger.info("Каталог завантажено: " + catalog.size() + " предметів."); // ЛОГ
        } catch (IOException e) {
            // ЛОГ ERROR (Відправиться на пошту, бо це критично для роботи магазину)
            logger.error("КРИТИЧНА ПОМИЛКА: Не вдалося прочитати файл амуніції: " + fileName, e);
        }
    }

    private void parseLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length != 5) {
                logger.warn("Пропущено пошкоджений рядок (невірна кількість полів): " + line); // ЛОГ WARN
                return;
            }

            String type = parts[0].trim();
            String name = parts[1].trim();
            double weight = Double.parseDouble(parts[2].trim());
            double price = Double.parseDouble(parts[3].trim());
            int value = Integer.parseInt(parts[4].trim());

            switch (type) {
                case "Helmet": catalog.add(new Helmet(name, weight, price, value)); break;
                case "Breastplate": catalog.add(new Breastplate(name, weight, price, value)); break;
                case "Sword": catalog.add(new Sword(name, weight, price, value)); break;
                case "Axe": catalog.add(new Axe(name, weight, price, value)); break;
                case "Bow": catalog.add(new Bow(name, weight, price, value)); break;
                case "Knife": catalog.add(new Knife(name, weight, price, value)); break;
                case "TwoHandedSword": catalog.add(new TwoHandedSword(name, weight, price, value)); break;
                case "Mace": catalog.add(new Mace(name, weight, price, value)); break;
                case "Spear": catalog.add(new Spear(name, weight, price, value)); break;
                default: logger.warn("Невідомий тип предмета у файлі: " + type); // ЛОГ WARN
            }
        } catch (Exception e) {
            logger.error("Помилка парсингу рядка: " + line, e); // ЛОГ ERROR
        }
    }

    public List<Ammunition> getAll() {
        return catalog;
    }
}