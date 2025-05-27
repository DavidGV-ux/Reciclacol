package co.edu.uptc.servicio;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import co.edu.uptc.util.VerificationCodeGenerator;

public class EmailService {
    private static EmailService instance;
    private String lastSentCode = "";
    private String lastSentTo = "";

    // Cambia estos datos por los de tu cuenta de envío
    private static final String USERNAME = "reciclarecoveryp@gmail.com";
    private static final String PASSWORD = "wuim fokj tgfx mkyn"; // Usa una contraseña de aplicación de Google
    private static final String FROM = "reciclarecoveryp@gmail.com";

    private EmailService() {}

    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    public String sendVerificationCode(String to) {
        String code = VerificationCodeGenerator.generateVerificationCode();
        lastSentCode = code;
        lastSentTo = to;

        // Configuración de propiedades SMTP para Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Crear sesión autenticada
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Código de verificación para recuperación de contraseña de ReciclaCol");
            message.setText("Tu código de verificación es: " + code);

            Transport.send(message);
            System.out.println("Código enviado a " + to + ": " + code);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo enviar el correo.");
        }

        return code;
    }

    public boolean verifyCode(String email, String code) {
        return email.equals(lastSentTo) && code.equals(lastSentCode);
    }
}
