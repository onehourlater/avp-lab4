package com.sample;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.Timer;

import java.util.*;

public class MailMicroService {

    private static boolean timerStarted = false;

    private static HazelcastInstance instance = Hazelcast.newHazelcastInstance();
    private static IList<Map<String, String>> articles;
    private static IList<Map<String, String>> subscribers;

    public static void main(String[] args) {

        Timer t = new Timer(60000, (x) -> {
            if (articles.size() > 0) {

                subscribers = instance.getList("subscribers");

                System.out.println("articles.size() = " + articles.size());
                System.out.println("subscribers.size() = " + subscribers.size());

                articles.forEach((article) -> {
                    subscribers.forEach((subscriber) -> {

                        String recepient = subscriber.get("email");

                        String sender = "no-reply@gmail.com";

                        Properties properties = System.getProperties();

                        properties.put("mail.smtp.host", "smtp.gmail.com");
                        properties.put("mail.smtp.port", "465");
                        properties.put("mail.smtp.ssl.enable", "true");
                        properties.put("mail.smtp.auth", "true");

                        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("fromaddress@gmail.com", "*******");
                            }
                        });

                        try {

                            MimeMessage message = new MimeMessage(session);

                            message.setFrom(new InternetAddress(sender));
                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
                            message.setSubject("Новая новость на нашем сайте!");
                            message.setText(article.get("body"));

                            // Transport.send(message);

                        } catch (MessagingException mex) {
                            mex.printStackTrace();
                        }

                    });
                });

                articles.clear();

                timerStarted = false;

                System.out.println("Task performed on: " + new Date() + "n" +
                        "Thread's name: " + Thread.currentThread().getName());
                System.out.println("articles.size() = " + articles.size());
                System.out.println("subscribers.size() = " + subscribers.size());

            }
        });

        t.start();

        while(true) {

            articles = instance.getList("articles");
            if (articles.size() > 0 && timerStarted == false) {
                timerStarted = true;

                t.setRepeats(true);
                t.start();

            }else{
                t.setRepeats(false);
            }

        }

    }

}

