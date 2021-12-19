package com.mainproject.outlinevisionv2.entity;

import java.util.Date;

public class ChatMessage {
    private MessageType messageType;
    private String content;
    private String sender;
    private String time;
    private String clientId;

    //constructor


    public ChatMessage(MessageType messageType, String content, String sender, String time) {
        this.messageType = messageType;
        this.content = content;
        this.sender = sender;
        this.time = time;
    }

    public ChatMessage(){}

    public enum MessageType{
        RECEIVED,
        DELIVERED
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
