package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static final String USERNAME = "bazgoten@gmail.com";

    private static final String PASSWORD = "aeyanpsvvkioqwff";

    public static void sendAsync(String subject, String text) {
        
        new Thread(() -> send(subject, text)).start();
    }

    public static void send(String subject, String text) {

        System.out.println(">>> Sending email: " + subject);

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");     
        prop.put("mail.smtp.port", "587");                
        prop.put("mail.smtp.auth", "true");               
        prop.put("mail.smtp.starttls.enable", "true");    
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");   

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        try {
            
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(USERNAME));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));

            message.setSubject(subject);

            message.setText(text);

            Transport.send(message);

        } catch (MessagingException e) {
            
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
