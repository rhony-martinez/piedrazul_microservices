package com.piedrazul.personas.interfaces.rest.exception;

import com.piedrazul.personas.application.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonaNoEncontradaException.class)
    public ResponseEntity<?> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
            PersonaYaExisteException.class,
            PacienteYaRegistradoException.class,
            MedicoYaRegistradoException.class,
            ReglaDeNegocioException.class
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DisponibilidadNoEncontradaException.class)
    public ResponseEntity<?> handleDisponibilidadNoEncontrada(DisponibilidadNoEncontradaException ex) {
        return ResponseEntity.status(404)
                .body(Map.of("error", ex.getMessage()));
    }
}