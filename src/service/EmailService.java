package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // ===== ДАНІ АККАУНТА GMAIL =====
    // Логін Gmail, з якого надсилаються листи
    private static final String USERNAME = "bazgoten@gmail.com";

    // Спеціальний "App Password", який створюється в Google Account
    // (звичайний пароль Gmail тут НЕ працює!)
    private static final String PASSWORD = "aeyanpsvvkioqwff";

    // =====================================================================
    // Метод для АСИНХРОННОЇ відправки email
    // Він запускає відправку в окремому потоці, щоб не гальмувати програму
    // =====================================================================
    public static void sendAsync(String subject, String text) {
        // Створюємо новий потік -> всередині викликаємо send()
        new Thread(() -> send(subject, text)).start();
    }
    // =====================================================================
    // Основний метод для ВІДПРАВКИ EMAIL
    // Версія СИНХРОННА — використовується у ShutdownHook при виході з програми
    // =====================================================================
    public static void send(String subject, String text) {

        System.out.println(">>> Sending email: " + subject);

        // Налаштування параметрів SMTP сервера Gmail
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");     // Хост Gmail SMTP
        prop.put("mail.smtp.port", "587");                // Порт для TLS
        prop.put("mail.smtp.auth", "true");               // Потрібна авторизація
        prop.put("mail.smtp.starttls.enable", "true");    // Увімкнути шифрування TLS
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");   // Новий протокол безпеки

        // Створюємо поштову сесію з авторизацією
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Повертаємо логін та App Password для входу в Gmail SMTP
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        try {
            // Створюємо email-повідомлення
            Message message = new MimeMessage(session);

            // Вказуємо адресу від кого відправляється лист
            message.setFrom(new InternetAddress(USERNAME));

            // Вказуємо адресу кому надсилається лист (тут відправляємо самому собі)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));

            // Тема листа
            message.setSubject(subject);

            // Текстовий вміст листа
            message.setText(text);

            // ВІДПРАВКА ЛИСТА через Gmail SMTP
            Transport.send(message);

        } catch (MessagingException e) {
            // Якщо сталася помилка — виводимо її в консоль
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
