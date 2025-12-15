package com.bank.accounts.domain.service;

import com.bank.accounts.model.dto.MovementRequest;
import com.bank.accounts.model.dto.MovementResponse;
import reactor.core.publisher.Mono;

public interface MovementService {
    Mono<MovementResponse> createMovement(MovementRequest movementRequest);
}
