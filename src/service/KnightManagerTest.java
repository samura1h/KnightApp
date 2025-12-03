package service;

import model.Knight;
import model.Rank; // Імпортуємо Rank
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.EquipmentRepository;
import repository.KnightRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnightManagerTest {

    @Mock
    private KnightRepository knightRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private KnightManager knightManager;

    private Knight testKnight;

    @BeforeEach
    void setUp() {
        // ВИПРАВЛЕНО: Використовуємо правильний конструктор Knight(Name, Orden, Rank)
        // ID генерується автоматично всередині класу Knight
        testKnight = new Knight("Lancelot", "Templars", Rank.MASTER);
    }

    @Test
    void testAddKnight_FirstOneBecomesActive() {
        knightManager.addKnight(testKnight);

        verify(knightRepository).save(testKnight);
        assertEquals(testKnight, knightManager.getActiveKnight());
    }

    @Test
    void testRemoveKnight() {
        knightManager.addKnight(testKnight);

        // Отримуємо справжній ID, який згенерувався автоматично
        int realId = testKnight.getId();

        knightManager.removeKnight(realId);

        verify(knightRepository).remove(realId);
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testSetActiveKnight_Success() {
        int realId = testKnight.getId();

        // Навчаємо мок повертати нашого лицаря за його ID
        when(knightRepository.findById(realId)).thenReturn(testKnight);

        knightManager.setActiveKnight(realId);

        assertEquals(testKnight, knightManager.getActiveKnight());
    }

    @Test
    void testSetActiveKnight_NotFound() {
        when(knightRepository.findById(999)).thenReturn(null);

        knightManager.setActiveKnight(999);

        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testReloadSystem() {
        knightManager.reloadSystem();

        verify(knightRepository, times(1)).reload();
        verify(equipmentRepository, times(1)).reload();

        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testLoadFromDisk() {
        knightManager.loadFromDisk();
        verify(knightRepository).loadData();
    }
}