package command; // Вказуємо, що цей клас знаходиться в пакеті command

import service.KnightManager; // Імпортуємо клас KnightManager для доступу до логіки управління лицарями
import java.util.Scanner; // Імпортуємо Scanner для зчитування вводу користувача з консолі

// Клас реалізує інтерфейс Command і відповідає за видалення лицаря
public class DeleteKnightCommand implements Command {
    private KnightManager manager; // Поле для зберігання посилання на менеджер лицарів
    private Scanner scanner; // Поле для зберігання посилання на сканер

    // Конструктор класу, який приймає залежності (manager та scanner)
    public DeleteKnightCommand(KnightManager manager, Scanner scanner) {
        this.manager = manager; // Ініціалізуємо поле manager переданим об'єктом
        this.scanner = scanner; // Ініціалізуємо поле scanner переданим об'єктом
    }

    @Override // Вказуємо, що переписуємо метод інтерфейсу Command
    public void execute() { // Метод, який запускається при виборі цього пункту меню
        var all = manager.getAllKnights(); // Отримуємо список (Map) усіх лицарів з менеджера

        if (all.isEmpty()) { // Перевіряємо, чи список лицарів порожній
            System.out.println("Список лицарів порожній."); // Виводимо повідомлення, якщо нікого немає
            return; // Завершуємо виконання методу, бо видаляти нікого
        }

        System.out.println("--- Видалення Лицаря ---"); // Виводимо заголовок меню
        // Проходимось по всіх лицарях (values) і виводимо їх ID та Ім'я
        all.values().forEach(k -> System.out.println("ID: " + k.getId() + " | " + k.getName()));

        System.out.print("Введіть номер (ID) лицаря для видалення: "); // Просимо користувача ввести ID

        try { // Починаємо блок try, щоб перехопити можливі помилки вводу (наприклад, літери замість цифр)
            int id = Integer.parseInt(scanner.nextLine()); // Зчитуємо рядок і перетворюємо його в ціле число (int)

            if (all.containsKey(id)) { // Перевіряємо, чи існує лицар з таким ID у списку
                manager.removeKnight(id); // Викликаємо метод менеджера для видалення лицаря за ID
                System.out.println("Лицаря видалено."); // Повідомляємо про успішне видалення
            } else { // Якщо лицаря з таким ID немає
                System.out.println("Лицаря з таким ID не знайдено."); // Виводимо повідомлення про помилку
            }
        } catch (NumberFormatException e) { // Ловимо виключення, якщо користувач ввів не число
            System.out.println("Помилка: введіть число."); // Просимо ввести коректні дані
        }
    }
}