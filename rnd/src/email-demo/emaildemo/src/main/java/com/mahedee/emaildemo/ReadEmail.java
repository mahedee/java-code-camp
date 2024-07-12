package com.mahedee.emaildemo;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

//import java.util.Properties;
//import javax.mail.*;

public class ReadEmail {
    public static void main(String[] args) {

        System.out.println("Starting app ... ");
  
        // Mail server configuration
        String host = "outlook.office365.com";
        String username = "youremail@hotmail.com"; // replace with your email
        String password = "youremail"; // replace with your password

        // Properties for the mail session
        Properties properties = new Properties();

        

        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.trust", host);
        properties.put("mail.imap.auth", "true");



        // Get the session
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });




        try {
            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            // Open the inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Get messages
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Sent Date: " + message.getSentDate());
                System.out.println("Content: " + message.getContent().toString());
                System.out.println("---------------------------------");
            }

            // Close the inbox folder and store
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("App finished.");
    }
}
