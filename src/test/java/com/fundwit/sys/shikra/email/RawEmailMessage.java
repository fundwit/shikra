package com.fundwit.sys.shikra.email;

import javax.mail.MessagingException;
import javax.mail.Part;
import java.io.IOException;

public class RawEmailMessage {
    private String from;
    private String recipient;
    private String subject;

    private Part body;

    public String getTextContent() throws IOException, MessagingException {
        return new PlainTextRender().render(body);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Part getBody() {
        return body;
    }

    public void setBody(Part body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}