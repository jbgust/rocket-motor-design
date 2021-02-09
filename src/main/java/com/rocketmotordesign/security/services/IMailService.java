package com.rocketmotordesign.security.services;

import javax.mail.MessagingException;

public interface IMailService {

    void sendMessage(String sujet, String texte, String destinataires) throws MessagingException;

    void sendHtmlMessage(String sujet, String texte, String destinataires) throws MessagingException;
}
