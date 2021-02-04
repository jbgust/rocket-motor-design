package com.rocketmotordesign.admin;

public class MailRequest {

    private String subject;
    private String htmlContent;
    private String receiver;

    //used to limit the number of mail send
    /**
     * with start=10 we send the newsletter from the 10th user
     */
    private int start;

    /**
     *  with send=20 we send the newsletter up to the 19th
     *  the end is included
     */
    private int end;

    public MailRequest() {
    }

    public MailRequest(String subject, String htmlContent, String receiver, int start, int end) {
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.receiver = receiver;
        this.start = start;
        this.end = end;
    }

    public MailRequest(String subject, String htmlContent, int start, int end) {
        this(subject, htmlContent, null, start, end);
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return end - start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
