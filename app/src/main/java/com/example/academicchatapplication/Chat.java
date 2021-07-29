package com.example.academicchatapplication;

public class Chat {
    private String messagetxt;
    private String messageimg;
    private String status;
    private String chatallid;
    private String sender;
    private String receiver;

    public Chat() {
    }

    public Chat(String messagetxt, String messageimg, String status, String chatallid, String sender, String receiver) {
        this.messagetxt = messagetxt;
        this.messageimg = messageimg;
        this.status = status;
        this.chatallid = chatallid;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessagetxt() {
        return messagetxt;
    }

    public void setMessagetxt(String messagetxt) {
        this.messagetxt = messagetxt;
    }

    public String getMessageimg() {
        return messageimg;
    }

    public void setMessageimg(String messageimg) {
        this.messageimg = messageimg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChatallid() {
        return chatallid;
    }

    public void setChatallid(String chatallid) {
        this.chatallid = chatallid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}