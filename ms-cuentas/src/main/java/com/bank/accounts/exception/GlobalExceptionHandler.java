package com.bank.accounts.exception;

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
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        if (message != null) {
            if (message.contains("Saldo no disponible") || message.contains("must be greater than zero")) {
                status = HttpStatus.BAD_REQUEST;
            } else if (message.contains("Account not found") || 
                       message.contains("Client has no accounts") || 
                       message.contains("Client not found") ||
                       message.contains("Customer replica not found")) {
                status = HttpStatus.NOT_FOUND;
            }
        }
        
        return Mono.just(
                ResponseEntity.status(status)
                        .body(Map.of("error", message != null ? message : "Unknown error"))
        );
    }
}
