package com.gestion.automange.controller;

import com.gestion.automange.model.Estadisticas;
import com.gestion.automange.repository.EstadisticasRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final EstadisticasRepository estadisticasRepository;

 
    public WebSocketController(SimpMessagingTemplate messagingTemplate, EstadisticasRepository estadisticasRepository) {
        this.messagingTemplate = messagingTemplate;
        this.estadisticasRepository = estadisticasRepository;
    }

    // Enviar estadísticas cada 5 segundos con datos REALES (sin inventar datos)
    @Scheduled(fixedRate = 5000)
    public void enviarEstadisticas() {
        try {
            // 🔍 Validar si el repositorio es nulo
            if (estadisticasRepository == null) {
                logger.error("❌ Error crítico: El repositorio de estadísticas es nulo.");
                return;
            }

            // 🟢 Obtener estadísticas reales desde la BD
            Optional<Estadisticas> estadisticasOpt = estadisticasRepository.findFirstByOrderByIdDesc();

            if (estadisticasOpt.isPresent()) {
                messagingTemplate.convertAndSend("/topic/estadisticas", estadisticasOpt.get());
                logger.info("📊 Estadísticas enviadas con éxito: {}", estadisticasOpt.get());
            } else {
                logger.warn("⚠ No hay estadísticas en la base de datos. No se enviarán datos.");
            }

        } catch (Exception e) {
           logger.error("❌ Error en WebSocket: ", e);
        }
    }
}
