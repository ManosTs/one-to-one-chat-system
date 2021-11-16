package com.mainproject.outlinevisionv2;


import com.mainproject.outlinevisionv2.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public void setMessagingTemplate(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String name = (String) headerAccessor.getSessionAttributes().get("name");
        if(name != null) {
            logger.info("User Disconnected : " + name);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageType(ChatMessage.MessageType.RECEIVED);
            chatMessage.setSender(name);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}