package repository;

import model.Knight;
// --- ІМПОРТИ ЛОГЕРА ---
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KnightRepository {
    private static final Logger logger = LogManager.getLogger(KnightRepository.class);

    private Map<Integer, Knight> knights = new HashMap<>();
    private String fileName;

    public KnightRepository() {
        this("knights.txt");
    }

    public KnightRepository(String fileName) {
        this.fileName = fileName;
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(fileName);

        if (!file.exists()) {
            logger.warn("Файл збережень не знайдено (" + fileName + "). Створено нову базу."); // ЛОГ WARN
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            knights = (Map<Integer, Knight>) ois.readObject();
            logger.info("Успішно завантажено " + knights.size() + " лицарів з файлу."); // ЛОГ
        } catch (IOException | ClassNotFoundException e) {
            // ЛОГ ERROR (Критично, бо користувач може втратити прогрес)
            logger.error("КРИТИЧНА ПОМИЛКА: Файл збережень пошкоджено або застарів!", e);
            knights = new HashMap<>();
        }
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(knights);
            logger.info("Дані успішно збережено на диск."); // ЛОГ
        } catch (IOException e) {
            // ЛОГ ERROR (Критично - неможливо зберегтися)
            logger.error("КРИТИЧНА ПОМИЛКА: Не вдалося записати файл збережень!", e);
        }
    }

    public void reload() {
        logger.info("Очищення пам'яті лицарів та перезавантаження...");
        knights.clear();
        loadData();
    }

    public void save(Knight k) { knights.put(k.getId(), k); }
    public Knight findById(int id) { return knights.get(id); }
    public Map<Integer, Knight> findAll() { return knights; }
    public void remove(int id) { knights.remove(id); }
}