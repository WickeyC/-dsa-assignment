package utility;

import java.util.Properties;
import java.util.Date;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.smtp.*;

public class Email {
  private static final String FROM_EMAIL = "ohbs-wp20@student.tarc.edu.my";

  public static boolean sendEmail(String toEmail, String subject, String text) {
    Properties props = System.getProperties();
    props.put("mail.smtps.host", "smtp.mailgun.org");
    props.put("mail.smtps.auth", "true");

    Session session = Session.getInstance(props, null);
    Message msg = new MimeMessage(session);
    try {
      msg.setFrom(new InternetAddress(FROM_EMAIL));
      InternetAddress[] addrs = InternetAddress.parse(toEmail, false);
      msg.setRecipients(Message.RecipientType.TO, addrs);

      msg.setSubject(subject);
      msg.setText(text);
      msg.setSentDate(new Date());

      SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
      t.connect(
          "smtp.mailgun.org",
          "postmaster@mg.boonsuen.com",
          "2eac89665d94e839afb4f3bd895666b7-1b3a03f6-37c327a8");
      t.sendMessage(msg, msg.getAllRecipients());

      int lastReturnCode = t.getLastReturnCode();      
      t.close();      
      return lastReturnCode >= 200 && lastReturnCode <= 299;
    } catch (AddressException e) {
      e.printStackTrace();
      return false;
    } catch (MessagingException e) {
      e.printStackTrace();
      return false;
    }
  }
}