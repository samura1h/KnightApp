package service;

import model.Knight;
import repository.EquipmentRepository;
import repository.KnightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith підключає Mockito до JUnit 5.
// Без цього анотації @Mock та @InjectMocks не спрацюють.
@ExtendWith(MockitoExtension.class)
class KnightManagerTest {

    // Створюємо "фейковий" репозиторій лицарів.
    // Він не робить реальних запитів до файлів.
    @Mock
    private KnightRepository knightRepository;

    // Створюємо "фейковий" репозиторій спорядження.
    @Mock
    private EquipmentRepository equipmentRepository;

    // Створюємо реальний об'єкт KnightManager, який ми тестуємо.
    // Mockito автоматично знайде конструктор і підставить туди mocks (knightRepository та equipmentRepository).
    @InjectMocks
    private KnightManager knightManager;

    // --- ТЕСТИ КОНСТРУКТОРА ТА ІНІЦІАЛІЗАЦІЇ ---

    @Test
    void testInitialState() {
        // Перевіряємо, що одразу після створення активний лицар не вибраний (null)
        assertNull(knightManager.getActiveKnight(), "Спочатку активний лицар має бути null");
    }

    // --- ТЕСТИ ЗАВАНТАЖЕННЯ ДАНИХ (loadFromDisk) ---

    @Test
    void testLoadFromDisk_Success() {
        // Дія: Викликаємо метод завантаження даних
        knightManager.loadFromDisk();

        // Перевірка (Verify):
        // Ми перевіряємо, чи звернувся менеджер до репозиторію з командою loadData().
        // times(1) означає, що виклик мав бути рівно один раз.
        verify(knightRepository, times(1)).loadData();
    }

    @Test
    void testLoadFromDisk_Exception() {
        // Налаштування (Stubbing):
        // Кажемо моку: "Якщо хтось викличе loadData(), ти маєш викинути помилку RuntimeException".
        doThrow(new RuntimeException("Disk fail")).when(knightRepository).loadData();

        // Перевірка (Assert):
        // assertDoesNotThrow означає: "Виконай цей код і переконайся, що програма НЕ впала".
        // Тобто ми очікуємо, що всередині loadFromDisk стоїть try-catch.
        assertDoesNotThrow(() -> knightManager.loadFromDisk());

        // Переконуємось, що спроба завантаження все ж таки була
        verify(knightRepository, times(1)).loadData();
    }

    // --- ТЕСТИ ДОДАВАННЯ ЛИЦАРЯ (addKnight) ---

    @Test
    void testAddKnight_FirstKnightBecomesActive() {
        // Створюємо фейкового лицаря (нам не важливо, що всередині, головне, що це об'єкт Knight)
        Knight knight = mock(Knight.class);
        // Навчаємо мок: якщо спитають ім'я, відповідай "Arthur"
        when(knight.getName()).thenReturn("Arthur");

        // Дія: додаємо першого лицаря в систему
        knightManager.addKnight(knight);

        // Перевірка 1: Репозиторій мав отримати команду save(knight)
        verify(knightRepository).save(knight);

        // Перевірка 2: Оскільки це перший лицар, він автоматично стає активним
        assertEquals(knight, knightManager.getActiveKnight());
    }

    @Test
    void testAddKnight_SecondKnightDoesNotChangeActive() {
        // Створюємо двох різних фейкових лицарів
        Knight k1 = mock(Knight.class);
        when(k1.getName()).thenReturn("Arthur");

        Knight k2 = mock(Knight.class);
        when(k2.getName()).thenReturn("Lancelot");

        // Дія: додаємо першого (він стає активним)
        knightManager.addKnight(k1);
        // Дія: додаємо другого
        knightManager.addKnight(k2);

        // Перевірка: Активним має залишитися перший (k1), бо ми не перемикалися
        assertEquals(k1, knightManager.getActiveKnight());

        // Але другий лицар теж мав бути збережений у репозиторій
        verify(knightRepository).save(k2);
    }

    // --- ТЕСТИ ВИДАЛЕННЯ ЛИЦАРЯ (removeKnight) ---

    @Test
    void testRemoveKnight_RemovesActiveKnight() {
        Knight knight = mock(Knight.class);
        when(knight.getId()).thenReturn(10); // ID лицаря = 10
        when(knight.getName()).thenReturn("Arthur");

        // Підготовка: додаємо лицаря, він стає активним
        knightManager.addKnight(knight);
        assertEquals(knight, knightManager.getActiveKnight());

        // Дія: видаляємо лицаря з ID 10 (того, що зараз активний)
        knightManager.removeKnight(10);

        // Перевірка 1: Репозиторій отримав команду видалити ID 10
        verify(knightRepository).remove(10);

        // Перевірка 2: Оскільки ми видалили активного лицаря,
        // система має скинути вибір (activeKnight = null)
        assertNull(knightManager.getActiveKnight());
    }

    @Test
    void testRemoveKnight_RemovesNonActiveKnight() {
        Knight k1 = mock(Knight.class); // Це буде активний
        when(k1.getName()).thenReturn("Arthur");
        when(k1.getId()).thenReturn(1);

        // Підготовка: додаємо k1
        knightManager.addKnight(k1);

        // Дія: видаляємо якогось іншого лицаря (ID 2), якого навіть немає в пам'яті менеджера,
        // але він може бути в базі.
        knightManager.removeKnight(2);

        // Перевірка 1: Репозиторій все одно отримав наказ видалити
        verify(knightRepository).remove(2);

        // Перевірка 2: Активний лицар (k1) не зник, бо видаляли не його
        assertNotNull(knightManager.getActiveKnight());
        assertEquals(k1, knightManager.getActiveKnight());
    }

    @Test
    void testRemoveKnight_WhenNoActiveKnightSet() {
        // Ситуація: в системі ще ніхто не обраний
        // Дія: видаляємо ID 99
        knightManager.removeKnight(99);

        // Перевірка: метод спрацював коректно, репозиторій викликали
        verify(knightRepository).remove(99);
        // Активний лицар як був null, так і лишився
        assertNull(knightManager.getActiveKnight());
    }

    // --- ТЕСТИ ПЕРЕЗАВАНТАЖЕННЯ (reloadSystem) ---

    @Test
    void testReloadSystem() {
        // Підготовка: встановимо активного лицаря
        Knight k = mock(Knight.class);
        when(k.getName()).thenReturn("Test");
        knightManager.addKnight(k);

        // Дія: повне перезавантаження системи
        knightManager.reloadSystem();

        // Перевірка:
        // 1. Обидва репозиторії отримали команду reload()
        verify(knightRepository).reload();
        verify(equipmentRepository).reload();

        // 2. Активний лицар скинувся в null (система "забула" вибір)
        assertNull(knightManager.getActiveKnight(), "Після перезавантаження активний лицар має бути null");
    }

    // --- ТЕСТИ ВСТАНОВЛЕННЯ АКТИВНОГО ЛИЦАРЯ (setActiveKnight) ---

    @Test
    void testSetActiveKnight_Success() {
        Knight k = mock(Knight.class);
        when(k.getName()).thenReturn("Galahad");

        // Налаштування мока: "Коли спитають findById(5), поверни об'єкт k"
        when(knightRepository.findById(5)).thenReturn(k);

        // Дія: намагаємось встановити лицаря з ID 5 активним
        knightManager.setActiveKnight(5);

        // Перевірка: активним став саме той, кого повернув репозиторій
        assertEquals(k, knightManager.getActiveKnight());
    }

    @Test
    void testSetActiveKnight_NotFound() {
        // Налаштування мока: "Коли спитають findById(99), поверни null (не знайдено)"
        when(knightRepository.findById(99)).thenReturn(null);

        // Переконуємось, що зараз ніхто не обраний
        assertNull(knightManager.getActiveKnight());

        // Дія: намагаємось обрати неіснуючого лицаря
        knightManager.setActiveKnight(99);

        // Перевірка: активний лицар не змінився (залишився null), програма не впала
        assertNull(knightManager.getActiveKnight());
    }

    // --- ТЕСТИ ІНШИХ МЕТОДІВ (Делегування) ---

    @Test
    void testGetAllKnights() {
        // Дія: просимо список всіх лицарів
        knightManager.getAllKnights();
        // Перевірка: менеджер просто передав цей запит у репозиторій
        verify(knightRepository).findAll();
    }

    @Test
    void testSaveAll() {
        // Дія: команда "зберегти все на диск"
        knightManager.saveAll();
        // Перевірка: менеджер передав це у репозиторій
        verify(knightRepository).saveData();
    }
}