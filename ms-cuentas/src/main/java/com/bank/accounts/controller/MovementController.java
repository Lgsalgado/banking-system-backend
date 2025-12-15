package com.bank.accounts.controller;

import com.bank.accounts.controller.api.MovementApi;
import com.bank.accounts.domain.service.MovementService;
import com.bank.accounts.model.dto.MovementRequest;
import com.bank.accounts.model.dto.MovementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class MovementController implements MovementApi {

    private final MovementService movementService;

    @Override
    public Mono<ResponseEntity<MovementResponse>> createMovement(Mono<MovementRequest> movementRequest, ServerWebExchange exchange) {
        return movementRequest
                .flatMap(movementService::createMovement)
                .map(movement -> ResponseEntity.status(HttpStatus.CREATED).body(movement));
    }
}
