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
        logger.info("User initiated data loading from disk."); // ЛОГ
        try {
            knightRepository.loadData();
        } catch (Exception e) {
            logger.error("Unexpected error during loading!", e); // ЛОГ ERROR
        }
    }

    public void addKnight(Knight k) {
        knightRepository.save(k);
        logger.info("Added new knight: " + k.getName()); // ЛОГ

        if (activeKnight == null) {
            activeKnight = k;
            logger.info("Knight " + k.getName() + " was automatically set as active."); // ЛОГ
        }
    }

    public void removeKnight(int id) {
        knightRepository.remove(id);
        logger.info("Removed knight with ID: " + id); // ЛОГ

        if (activeKnight != null && activeKnight.getId() == id) {
            activeKnight = null;
            logger.warn("Active knight was removed. Current selection cleared."); // ЛОГ WARN
        }
    }

    public void reloadSystem() {
        logger.info("Starting full system reload..."); // ЛОГ
        knightRepository.reload();
        equipmentRepository.reload();
        activeKnight = null;
        logger.info("System successfully reloaded."); // ЛОГ
    }

    public void setActiveKnight(int id) {
        Knight k = knightRepository.findById(id);
        if (k != null) {
            activeKnight = k;
            logger.info("Changed active knight to: " + k.getName()); // ЛОГ
        } else {
            logger.warn("Attempted to select non-existent knight with ID: " + id); // ЛОГ WARN
        }
    }

    public Knight getActiveKnight() { return activeKnight; }
    public Map<Integer, Knight> getAllKnights() { return knightRepository.findAll(); }

    public void saveAll() {
        logger.info("Attempting to save all data..."); // ЛОГ
        knightRepository.saveData();
    }
}