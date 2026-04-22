package com.piedrazul.notifications.controller;

import com.piedrazul.notifications.dto.NotificacionRequest;
import com.piedrazul.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * ESCENARIO 2: Endpoint REST para notificaciones síncronas
     * citas-service llama a este endpoint después de crear una cita
     */
    @PostMapping("/cita-agendada")
    public ResponseEntity<Void> notificarCitaAgendada(@Valid @RequestBody NotificacionRequest request) {
        log.info("========================================");
        log.info("📨 SOLICITUD REST RECIBIDA (ESCENARIO 2 - SÍNCRONO)");
        log.info("   Cita ID: {}", request.getCitaId());
        log.info("   Destinatario: {}", request.getDestinatario());
        log.info("   Tipo: {}", request.getTipo());
        log.info("========================================");

        notificationService.enviarNotificacionSincrona(request);

        return ResponseEntity.ok().build();
    }
}