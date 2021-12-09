package org.example.gradle.homework.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Submit extends DefaultTask {
    private final Property<String> username = getProject().getObjects().property(String.class);
    private final Property<String> email = getProject().getObjects().property(String.class);
    private final Property<String> password = getProject().getObjects().property(String.class);
    private final RegularFileProperty attachment = getProject().getObjects().fileProperty();
    private String to;
    private static long lastCommitTime;

    @Input
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Input
    public Property<String> getUsername() {
        return username;
    }

    @Input
    public Property<String> getPassword() {
        return password;
    }

    @Input
    public Property<String> getEmail() {
        return email;
    }

    @InputFile
    public RegularFileProperty getAttachment() {
        return attachment;
    }

    private void checkElapsedTime() {
        final long elapsed = System.currentTimeMillis() - lastCommitTime;
        if (elapsed < 0 || elapsed > 5 * 60 * 1000) {
            return;
        }

        final Date date = new Date(lastCommitTime + 5 * 60 * 1000);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA);
        final String next = dateFormat.format(date);
        throw new GradleException("提交的太频繁了，你下次可以提交的时间是：" + next);
    }

    @TaskAction
    public void sendEmail() throws MessagingException, IOException {
        checkElapsedTime();

        final String email = getEmail().get();
        final String password = getPassword().get();

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "mail.apusapps.com");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");


        final Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        final MimeMessage message = new MimeMessage(session);
        final InternetAddress toAddress = new InternetAddress(getTo());

        message.setFrom(email + "@apusapps.com");
        message.addRecipient(Message.RecipientType.TO, toAddress);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        message.setSubject("作业提交 " + getUsername().get() + " " + dateFormat.format(new Date()), "UTF-8");

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("PFA");
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(getAttachment().get().getAsFile());

        final MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(messageBodyPart);
        mimeMultipart.addBodyPart(attachmentPart);

        message.setContent(mimeMultipart);
        try {
            Transport.send(message);
            getLogger().quiet("作业提交成功。如有更新，可以在5分钟后再次提交。");
            lastCommitTime = System.currentTimeMillis();
        } catch (AuthenticationFailedException e) {
            throw new InvalidUserDataException("邮箱认证出错，请检查你的邮箱信息是否正确", e);
        }
    }
}
