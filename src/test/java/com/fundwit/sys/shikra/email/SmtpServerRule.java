package com.fundwit.sys.shikra.email;

import org.junit.rules.ExternalResource;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class SmtpServerRule extends ExternalResource {
    private String hostname;
    private int port;

    private SMTPServer smtpServer;
    private List<RawEmailMessage> receivedMails;

    public SmtpServerRule() {
        this.hostname = "127.0.0.1";
        try {
            this.port = new ServerSocket(0).getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("failed to get a free local port", e);
        }
    }
    public SmtpServerRule(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    protected void before() throws Throwable {
        this.stopServer();

        receivedMails = new ArrayList<>();
        smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(new InMemoryStoreMessageListener(receivedMails)));
        smtpServer.setHostName(hostname);
        smtpServer.setPort(port);
        smtpServer.start();
        System.out.println("smtp server started");
    }

    @Override
    protected void after() {
        this.stopServer();
    }
    private void stopServer() {
        if(smtpServer!=null && smtpServer.isRunning()) {
            smtpServer.stop();
            smtpServer = null;
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SMTPServer getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(SMTPServer smtpServer) {
        this.smtpServer = smtpServer;
    }

    public List<RawEmailMessage> getReceivedMails() {
        return receivedMails;
    }

    public void setReceivedMails(List<RawEmailMessage> receivedMails) {
        this.receivedMails = receivedMails;
    }
}
