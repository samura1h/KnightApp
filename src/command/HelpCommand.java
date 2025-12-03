package command;

public class HelpCommand implements Command {
    @Override
    public void execute() {
        System.out.println("\n--- ДОВІДКА ---");
        System.out.println("1. Створити лицаря - Додати нового воїна (ім'я, орден, ранг).");
        System.out.println("2. Видалити лицаря - Видалити воїна за ID.");
        System.out.println("3. Обрати активного - Вибрати, з ким працювати.");
        System.out.println("4. Екіпірувати - Купити/взяти річ зі списку амуніції.");
        System.out.println("5. Статус - Показати параметри та речі героя.");
        System.out.println("6. Вартість - Порахувати ціну всього одягненого.");
        System.out.println("7. Сортувати - Показати речі від найлегших до найважчих.");
        System.out.println("8. Знайти за ціною - Фільтр речей героя по бюджету.");
        System.out.println("9. Перезавантажити - Скинути стан до файлового.");
        System.out.println("10. Довідка - Цей список.");
        System.out.println("11. Вихід - Зберегти дані та вийти.");
    }
}