package NLPParser;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ForwardEmail {

    private static ForwardEmail forwardEmail = null;

    public static ForwardEmail getInstance() {
        if (null == forwardEmail) {
            synchronized (ForwardEmail.class) {
                if (null == forwardEmail) {
                    forwardEmail = new ForwardEmail();
                }
            }
        }
        return forwardEmail;
    }

    public void forwardEmail(Message message, String sendToEmail, String _mailId) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        // THIS IS THE DEPARTMENT MAIL
        final String username = "ashish.gupta03@snapdeal.com";
        final String password = "king_kong123";

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            // Get all the information from the message
            InternetAddress address = new InternetAddress();
            address.setAddress("wthServer");
            String from = InternetAddress.toString(message.getFrom());
            if (from != null) {
                System.out.println("From: " + from);
            }

            String subject = message.getSubject();
            if (subject != null) {
                System.out.println("Subject: " + subject);
            }
            Date sent = message.getSentDate();
            if (sent != null) {
                System.out.println("Sent: " + sent);
            }
            System.out.println(message.getContent());

            // compose the message to forward
            Message message2 = new MimeMessage(session);
            message2.setSubject("Fwd: " + message.getSubject());
            message2.setFrom(new InternetAddress("wthServer"));
            // this is the mail id of email server which will in response will
            // redirect to relevant department
            //			message2.setRecipients(Message.RecipientType.TO,
            //					InternetAddress.parse("gupta.ashish@snapdeal.com"));
            message2.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendToEmail));

            // Create your new message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String hyperLink = " <a href='mailto:" + "ashish.gupta03@snapdeal.com" + "?subject=" + _mailId + "'>Link text</a>";
            messageBodyPart.setText(hyperLink, "UTF-8", "html");

            // Create a multi-part to combine the parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Create and fill part for the forwarded content
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(message.getDataHandler());

            // Add part to multi part
            multipart.addBodyPart(messageBodyPart);

            // Associate multi-part with message
            message2.setContent(multipart);

            // Send message
            Transport.send(message2);

            System.out.println("message forwarded ....");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
