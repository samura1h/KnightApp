package service;

import model.Knight;
import repository.EquipmentRepository;
import repository.KnightRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

public class KnightManager {
    
    private static final Logger logger = LogManager.getLogger(KnightManager.class);

    private KnightRepository knightRepository;
    private EquipmentRepository equipmentRepository;
    private Knight activeKnight;

    public KnightManager(KnightRepository kRepo, EquipmentRepository eRepo) {
        this.knightRepository = kRepo;
        this.equipmentRepository = eRepo;
    }

    public void loadFromDisk() {
        logger.info("User initiated data loading from disk."); 
        try {
            knightRepository.loadData();
        } catch (Exception e) {
            service.LoggerService.error("Unexpected error during loading!", e); 
        }
    }

    public void addKnight(Knight k) {
        knightRepository.save(k);
        logger.info("Added new knight: " + k.getName()); 

        if (activeKnight == null) {
            activeKnight = k;
            logger.info("Knight " + k.getName() + " was automatically set as active."); 
        }
    }

    public void removeKnight(int id) {
        knightRepository.remove(id);
        logger.info("Removed knight with ID: " + id); 

        if (activeKnight != null && activeKnight.getId() == id) {
            activeKnight = null;
            logger.warn("Active knight was removed. Current selection cleared."); 
        }
    }

    public void reloadSystem() {
        logger.info("Starting full system reload..."); 
        knightRepository.reload();
        equipmentRepository.reload();
        activeKnight = null;
        logger.info("System successfully reloaded."); 
    }

    public void setActiveKnight(int id) {
        Knight k = knightRepository.findById(id);
        if (k != null) {
            activeKnight = k;
            logger.info("Changed active knight to: " + k.getName()); 
        } else {
            logger.warn("Attempted to select non-existent knight with ID: " + id); 
        }
    }

    public Knight getActiveKnight() { return activeKnight; }
    public Map<Integer, Knight> getAllKnights() { return knightRepository.findAll(); }

    public void saveAll() {
        logger.info("Attempting to save all data..."); 
        knightRepository.saveData();
    }

    public void saveKnight(Knight k) {
        logger.info("Saving knight: " + k.getName());
        knightRepository.save(k);
    }
}