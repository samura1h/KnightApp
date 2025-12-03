package command; // Оголошення пакету, де знаходиться цей клас

import service.KnightManager; // Імпорт менеджера, щоб отримувати дані про активного лицаря

/**
 * Команда, яка виводить повний статус активного лицаря.
 * Використовує метод toString() класу Knight.
 */
public class ShowKnightStatusCommand implements Command { // Клас реалізує інтерфейс Command
    private KnightManager km; // Поле для зберігання посилання на менеджер лицарів

    // Конструктор класу. Отримуємо менеджер через конструктор, щоб знати, кого показувати
    public ShowKnightStatusCommand(KnightManager km) {
        this.km = km; // Ініціалізуємо поле класу отриманим об'єктом менеджера
    }

    @Override // Вказуємо, що цей метод перевизначає метод з інтерфейсу
    public void execute() { // Метод, який виконується при виборі пункту меню "Статус"

        // Перевіряємо, чи взагалі обрано лицаря (чи не дорівнює activeKnight null)
        if (km.getActiveKnight() != null) {
            System.out.println("\n--- СТАТУС ЛИЦАРЯ ---"); // Виводимо заголовок

            // Виводимо об'єкт лицаря. Java автоматично викликає метод toString() у класу Knight,
            // який повертає гарно відформатований рядок з ім'ям, здоров'ям, силою тощо.
            System.out.println(km.getActiveKnight());

            // Додатково виводимо заголовок для списку речей
            System.out.println("--- Список спорядження ---");

            // Перевіряємо, чи список екіпіровки порожній
            if (km.getActiveKnight().getEquipment().isEmpty()) {
                System.out.println("(Порожньо)"); // Якщо речей немає, пишемо про це
            } else {
                // Якщо речі є, використовуємо forEach для проходу по списку
                // System.out::println - це скорочений запис, який друкує кожен елемент списку (викликаючи у нього toString())
                km.getActiveKnight().getEquipment().forEach(System.out::println);
            }
        } else {
            // Якщо активного лицаря немає (змінна activeKnight == null)
            System.out.println("ПОМИЛКА: Не обрано активного лицаря. Скористайтеся пунктом 2.");
        }
    }
}