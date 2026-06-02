package service;

import model.Knight;
import model.Rank;
import repository.EquipmentRepository;
import repository.KnightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KnightManagerTest {

    static class StubKnightRepository extends KnightRepository {
        public boolean loadDataCalled = false;
        public boolean saveDataCalled = false;
        public boolean reloadCalled = false;
        public int lastRemovedId = -1;
        public Knight lastSavedKnight = null;
        public boolean throwOnLoad = false;
        public Map<Integer, Knight> fakeDb = new HashMap<>();

        public StubKnightRepository() { super(null); }

        @Override public void loadData() {
            loadDataCalled = true;
            if (throwOnLoad) throw new RuntimeException("Disk fail");
        }
        @Override public void saveData() { saveDataCalled = true; }
        @Override public void reload() { reloadCalled = true; }
        @Override public void save(Knight k) { lastSavedKnight = k; fakeDb.put(k.getId(), k); }
        @Override public void remove(int id) { lastRemovedId = id; fakeDb.remove(id); }
        @Override public Knight findById(int id) { return fakeDb.get(id); }
        @Override public Map<Integer, Knight> findAll() { return fakeDb; }
    }

    static class StubEquipmentRepository extends EquipmentRepository {
        public boolean reloadCalled = false;
        public StubEquipmentRepository() { super(null); }
        @Override public void reload() { reloadCalled = true; }
    }

    private StubKnightRepository knightRepository;
    private StubEquipmentRepository equipmentRepository;
    private KnightManager knightManager;

    @BeforeEach
    void setUp() {
        knightRepository = new StubKnightRepository();
        equipmentRepository = new StubEquipmentRepository();
        knightManager = new KnightManager(knightRepository, equipmentRepository);
    }

    @Test
    void testInitialState() {
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testLoadFromDisk_Success() {
        knightManager.loadFromDisk();
        assertTrue(knightRepository.loadDataCalled);
    }

    @Test
    void testLoadFromDisk_Exception() {
        knightRepository.throwOnLoad = true;
        assertDoesNotThrow(() -> knightManager.loadFromDisk());
        assertTrue(knightRepository.loadDataCalled);
    }

    @Test
    void testAddKnight_FirstKnightBecomesActive() {
        Knight k = new Knight(1, "Arthur", "Order", Rank.MASTER);
        knightManager.addKnight(k);
        assertEquals(k, knightRepository.lastSavedKnight);
        assertEquals(k, knightManager.getActiveKnight());
    }

    @Test
    void testAddKnight_SecondKnightDoesNotChangeActive() {
        Knight k1 = new Knight(1, "Arthur", "Order", Rank.MASTER);
        Knight k2 = new Knight(2, "Lancelot", "Order", Rank.MASTER);

        knightManager.addKnight(k1);
        knightManager.addKnight(k2);

        assertEquals(k1, knightManager.getActiveKnight());
        assertEquals(k2, knightRepository.lastSavedKnight);
    }

    @Test
    void testRemoveKnight_RemovesActiveKnight() {
        Knight k = new Knight(10, "Arthur", "Order", Rank.MASTER);
        knightManager.addKnight(k);
        assertEquals(k, knightManager.getActiveKnight());

        knightManager.removeKnight(10);
        assertEquals(10, knightRepository.lastRemovedId);
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testRemoveKnight_RemovesNonActiveKnight() {
        Knight k1 = new Knight(1, "Arthur", "Order", Rank.MASTER);
        knightManager.addKnight(k1);

        knightManager.removeKnight(2);
        assertEquals(2, knightRepository.lastRemovedId);
        assertEquals(k1, knightManager.getActiveKnight());
    }

    @Test
    void testRemoveKnight_WhenNoActiveKnightSet() {
        knightManager.removeKnight(99);
        assertEquals(99, knightRepository.lastRemovedId);
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testReloadSystem() {
        Knight k = new Knight(1, "Test", "Order", Rank.MASTER);
        knightManager.addKnight(k);

        knightManager.reloadSystem();
        assertTrue(knightRepository.reloadCalled);
        assertTrue(equipmentRepository.reloadCalled);
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testSetActiveKnight_Success() {
        Knight k = new Knight(5, "Galahad", "Order", Rank.MASTER);
        knightRepository.fakeDb.put(5, k);

        knightManager.setActiveKnight(5);
        assertEquals(k, knightManager.getActiveKnight());
    }

    @Test
    void testSetActiveKnight_NotFound() {
        knightManager.setActiveKnight(99);
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testGetAllKnights() {
        Knight k = new Knight(1, "Test", "Order", Rank.MASTER);
        knightRepository.fakeDb.put(1, k);
        Map<Integer, Knight> all = knightManager.getAllKnights();
        assertEquals(1, all.size());
        assertTrue(all.containsKey(1));
    }

    @Test
    void testSaveAll() {
        knightManager.saveAll();
        assertTrue(knightRepository.saveDataCalled);
    }
}