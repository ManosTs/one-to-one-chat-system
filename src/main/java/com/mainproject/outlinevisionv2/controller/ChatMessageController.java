package com.mainproject.outlinevisionv2.controller;

import com.mainproject.outlinevisionv2.entity.ChatMessage;
import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ChatMessageController {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }



    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        String time = new SimpleDateFormat("HH:mm a").format(new Date());
        return new ChatMessage(chatMessage.getMessageType(),chatMessage.getContent(),chatMessage.getSender(),time);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("name", chatMessage.getSender());
        return chatMessage;
    }
}
