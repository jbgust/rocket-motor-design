package com.rocketmotordesign.admin;

public class MailRequest {

    private String subject;
    private String htmlContent;

    public MailRequest(String subject, String htmlContent) {
        this.subject = subject;
        this.htmlContent = htmlContent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
