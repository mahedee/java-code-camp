import java.io.IOException;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class EmailEngine {
    public static void main(String[] args) {
        System.out.println("Starting app ... ");
        EmailEngine emailEngine = new EmailEngine();
        emailEngine.readEmail();
        System.out.println("App finished.");
    }

    private void readEmail() {
        final String username = "arisha.hasan17@outlook.com";

        //your email password
        final String passwd = "mypassword";

        
        Properties props = new Properties();
        props.put("mail.host", "outlook.office365.com");
        props.put("mail.store.protocol", "pop3s");
        props.put("mail.pop3s.auth", "true");
        props.put("mail.pop3s.port", "995");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, passwd);
            }
        });

        try {
            Store store = session.getStore("pop3s");
            store.connect();
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
            }

            // // close the store and folder objects
            emailFolder.close(false);
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
