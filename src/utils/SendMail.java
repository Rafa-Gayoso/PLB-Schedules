package utils;



import model.Empleado;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

    public static void sendCompilationEmail(String name, String month){
        ArrayList<String> recipients = new ArrayList<>();
        // Mention the Recipient's email address
        recipients.add("iamalfi@medibiofarma.com");
        recipients.add("jrojo@aplobiofarma.com");

        // Mention the Sender's email address
        String from = "rgayoso@medibiofarma.com";
        // Mention the SMTP server address. Below Gmail's SMTP server is being used to send email
        String host = "smtp.office365.com";
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("rgayoso@medibiofarma.com", "Rafa1997.");
            }
        });
        // Used to debug SMTP issues
        session.setDebug(true);
        try {
            for(String to: recipients) {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);
                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));
                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                // Set Subject: header field
                message.setSubject("Control Horario de "+name+" para el mes de "+month);
                // Now set the actual message
                message.setText(name+ "ha rellanado su horario laboral correspondiente al mes de "+month+".");
                System.out.println("sending...");
                // Send message
                Transport.send(message);
                System.out.println("Sent message successfully....");
            }
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void sendWarningEmail(Empleado employee, String month){
        String to = employee.getEmail();
        // Mention the Sender's email address
        String from = "rgayoso@medibiofarma.com";
        // Mention the SMTP server address. Below Gmail's SMTP server is being used to send email
        String host = "smtp.office365.com";
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("rgayoso@medibiofarma.com", "Rafa1997.");
            }
        });
        // Used to debug SMTP issues
        session.setDebug(true);
        try {

                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);
                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));
                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                // Set Subject: header field
                message.setSubject("Control Horarios");
                // Now set the actual message
                message.setText("Hola "+employee.getNombre()+". Le recordamos que debe rellenar la hoja de control" +
                        "horario correspondiente al mes de "+month+". \nSaludos cordiales. " +
                        "\nDepartamento de RRHH.");
                System.out.println("sending...");
                // Send message
                Transport.send(message);
                System.out.println("Sent message successfully....");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
