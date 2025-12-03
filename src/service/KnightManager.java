package service;

import model.Knight;
import repository.EquipmentRepository;
import repository.KnightRepository;
// --- ІМПОРТИ ЛОГЕРА ---
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

public class KnightManager {
    // Ініціалізація логера
    private static final Logger logger = LogManager.getLogger(KnightManager.class);

    private KnightRepository knightRepository;
    private EquipmentRepository equipmentRepository;
    private Knight activeKnight;

    public KnightManager(KnightRepository kRepo, EquipmentRepository eRepo) {
        this.knightRepository = kRepo;
        this.equipmentRepository = eRepo;
    }

    public void loadFromDisk() {
        logger.info("Користувач ініціював завантаження даних з диска."); // ЛОГ
        try {
            knightRepository.loadData();
        } catch (Exception e) {
            logger.error("Непередбачена помилка при виклику завантаження!", e); // ЛОГ ERROR
        }
    }

    public void addKnight(Knight k) {
        knightRepository.save(k);
        logger.info("Додано нового лицаря: " + k.getName()); // ЛОГ

        if (activeKnight == null) {
            activeKnight = k;
            logger.info("Лицар " + k.getName() + " автоматично встановлений активним."); // ЛОГ
        }
    }

    public void removeKnight(int id) {
        knightRepository.remove(id);
        logger.info("Видалено лицаря з ID: " + id); // ЛОГ

        if (activeKnight != null && activeKnight.getId() == id) {
            activeKnight = null;
            logger.warn("Активного лицаря було видалено. Поточний вибір скинуто."); // ЛОГ WARN
        }
    }

    public void reloadSystem() {
        logger.info("Початок повного перезавантаження системи..."); // ЛОГ
        knightRepository.reload();
        equipmentRepository.reload();
        activeKnight = null;
        logger.info("Систему успішно перезавантажено."); // ЛОГ
    }

    public void setActiveKnight(int id) {
        Knight k = knightRepository.findById(id);
        if (k != null) {
            activeKnight = k;
            logger.info("Змінено активного лицаря на: " + k.getName()); // ЛОГ
        } else {
            logger.warn("Спроба обрати неіснуючого лицаря з ID: " + id); // ЛОГ WARN
        }
    }

    public Knight getActiveKnight() { return activeKnight; }
    public Map<Integer, Knight> getAllKnights() { return knightRepository.findAll(); }

    public void saveAll() {
        logger.info("Спроба збереження всіх даних..."); // ЛОГ
        knightRepository.saveData();
    }
}