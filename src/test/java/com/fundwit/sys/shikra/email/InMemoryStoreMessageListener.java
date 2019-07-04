package com.fundwit.sys.shikra.email;

import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.fundwit.sys.shikra.email.EmailParser.HEADER_NAME_SUBJECT;

public class InMemoryStoreMessageListener implements SimpleMessageListener {
    private List<RawEmailMessage> receivedMails;

    public InMemoryStoreMessageListener(List<RawEmailMessage> receivedMails) {
        this.receivedMails = receivedMails;
    }

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
        RawEmailMessage message = new RawEmailMessage();
        message.setFrom(from);
        message.setRecipient(recipient);

        try {
            MimeBodyPart bodyPart = new EmailParser().resolve(data);
            String subject = MimeUtility.decodeText(bodyPart.getHeader(HEADER_NAME_SUBJECT, null));
            message.setSubject(subject);
            message.setBody(bodyPart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        receivedMails.add(message);
    }
}