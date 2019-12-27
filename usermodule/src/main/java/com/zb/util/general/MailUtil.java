package com.zb.util.general;

import com.sun.mail.util.MailSSLSocketFactory;
import com.zb.util.ftpclient.FtpUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailUtil {

    private static String FROM;
    private static String HOST;
    private static String AUTHOCODE;
    private static Properties p;
    private String email;
    private String code;

    static {
        Properties p = new Properties();
        InputStream inputStream = FtpUtil.class.getClassLoader().getResourceAsStream("config/smtp.properties");
        try {
            p.load(inputStream);
            FROM = p.getProperty("mail.from");
            HOST = p.getProperty("mail.host");
            AUTHOCODE = p.getProperty("mail.licensecode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MailUtil(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public void run() {
        // 1.创建连接对象javax.mail.Session
        // 2.创建邮件对象 javax.mail.Message
        // 3.发送一封激活邮件
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.auth", "true");
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            // 1.获取session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM, AUTHOCODE);
                }
            });
            // 创建邮件对象
            Message message = new MimeMessage(session);
            // 设置发件人
            message.setFrom(new InternetAddress(FROM));
            // 设置接收人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            // 设置邮件主题
            message.setSubject("邮件激活验证码");
            // 设置邮件内容
            String content = "<html><head></head><body><h3>这是一封来自课工场的激活邮件,请在激活页面输入以下验证码,您的验证码是：</h3>" +
                    "<h1>" + code + "</h1></body></html>";
            message.setContent(content, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MailUtil mailUtil = new MailUtil("d5736208@qq.com", "hello根儿");
        mailUtil.run();
    }
}
