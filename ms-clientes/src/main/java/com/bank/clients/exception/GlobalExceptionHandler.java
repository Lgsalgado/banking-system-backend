package com.bank.clients.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleValidationException(WebExchangeBindException ex) {
        System.out.println("Validation Error: " + ex.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Validation error"))
        );
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleInputException(ServerWebInputException ex) {
        System.out.println("Input Error: " + ex.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid input"))
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleRuntimeException(RuntimeException ex) {
        System.out.println("Caught RuntimeException: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();
        
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Unknown error"))
        );
    }
}
