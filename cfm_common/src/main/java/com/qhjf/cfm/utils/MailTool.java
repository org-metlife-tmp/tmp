//package com.qhjf.cfm.utils;
//
//import com.jfinal.plugin.activerecord.Record;
//import com.sun.mail.util.MailSSLSocketFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//
//public class MailTool{
//    private static Logger log = LoggerFactory.getLogger(MailTool.class);
//
//
//    public void sendEmail(Record mail){
//        Properties emailProperty = new Properties();
//        try {
//            InputStream resourceAsStream = MailTool.class.getClassLoader().getResourceAsStream("application.properties");
//            emailProperty.load(new BufferedInputStream(resourceAsStream));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Properties sendProperty = new Properties();
//        sendProperty.setProperty("mail.transport.protocol",emailProperty.getProperty("mail.transport.protocol"));// 使用的协议（JavaMail规范要求）
//        sendProperty.setProperty("mail.smtp.host",emailProperty.getProperty("mail.smtp.host"));// 发件人的邮箱的 SMTP 服务器地址
//        sendProperty.setProperty("mail.smtp.auth",emailProperty.getProperty("mail.smtp.auth"));
//        // 开启SSL加密，否则会失败
//
//
//
//        try {
//            MailSSLSocketFactory sf =new MailSSLSocketFactory();
//            sf.setTrustAllHosts(true);
//            sendProperty.put("mail.smtp.ssl.enable", "true");
//            sendProperty.put("mail.smtp.ssl.socketFactory", sf);
//
//            Session session = Session.getDefaultInstance(sendProperty);
//            session.setDebug(true);  //设置为debug模式看日志
////        sendMailByAsynchronousMode(session,mail);
//
//            sendMailBySynchronizationMode(session,mail);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendMailByAsynchronousMode(Session session,MailVO mail){
//
//        taskExecutor.execute(new Runnable(){
//            @Override
//            public void run(){
//                try {
//
//                    sendMailBySynchronizationMode(session,mail);
//                } catch (Exception e) {
//                    log.info(e);
//                }
//            }
//        });
//    }
//
//    private void sendMailBySynchronizationMode(Session session,Record mail) throws IOException, MessagingException {
//
//
//        MimeMessage message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(mail.getMailAccount(),mail.getKey()));
//        List<String> recipients = mail.getReceiveAddressArray();
//
//        final int num = recipients.size();
//        InternetAddress[] addresses = new InternetAddress[num];
//        for (int i = 0; i < num; i++) {
//            addresses[i] = new InternetAddress(recipients.get(i));
//        }
//        message.setRecipients(Message.RecipientType.TO, addresses);
//
//        message.setSubject(mail.getSubject(), "UTF-8");
//        message.setContent(mail.getContent(),"text/html;charset=UTF-8");
//        message.setSentDate(new Date());
//        message.saveChanges();
//
//        Transport transport = session.getTransport();
//        transport.connect(mail.getMailAccount(),mail.getMailPassword());
//
//
//        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
//        transport.sendMessage(message, message.getAllRecipients());
//        // 7. 关闭连接
//        transport.close();
//
//
//    }
//}
