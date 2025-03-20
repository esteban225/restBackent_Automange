package com.gestion.automange.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.gestion.automange.model.Estadisticas;

@Controller
public class DashboardController {

    private final SimpMessagingTemplate messagingTemplate;

    public DashboardController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/estadisticas")
    public void enviarEstadisticas(Estadisticas estadisticas) {
        messagingTemplate.convertAndSend("/topic/dashboard", estadisticas);
    }
    
    public void actualizarDashboard(Estadisticas estadisticas) {
        messagingTemplate.convertAndSend("/topic/dashboard", estadisticas);
    }

}
