package com.fundwit.sys.shikra.email;

import org.springframework.util.StreamUtils;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamUtils.copy(data, outputStream);
        message.setData(outputStream.toByteArray());

        String messageContent = new String(message.getData());

        receivedMails.add(message);
    }
}