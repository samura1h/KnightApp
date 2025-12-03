package command; // Оголошення пакету, в якому знаходиться цей клас

import service.KnightManager; // Імпорт класу KnightManager для доступу до функцій перезавантаження

// Клас реалізує інтерфейс Command і відповідає за повне перезавантаження даних з файлів
public class ReloadSystemCommand implements Command {
    private KnightManager manager; // Поле для зберігання посилання на менеджер лицарів

    // Конструктор класу, який приймає менеджер як залежність
    public ReloadSystemCommand(KnightManager manager) {
        this.manager = manager; // Ініціалізуємо поле manager отриманим об'єктом
    }

    @Override // Вказуємо, що переписуємо метод execute з інтерфейсу Command
    public void execute() { // Метод, який запускається при виборі пункту меню "Перезавантажити"
        System.out.println("!!! УВАГА !!! Всі незбережені зміни будуть втрачені."); // Виводимо попередження користувачу

        // Викликаємо метод менеджера, який очищує списки в пам'яті та читає дані з файлів заново
        manager.reloadSystem();
    }
}