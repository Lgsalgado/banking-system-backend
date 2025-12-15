package com.bank.accounts.domain.service;

import com.bank.accounts.domain.model.Account;
import com.bank.accounts.domain.model.Movement;
import com.bank.accounts.domain.repository.AccountRepository;
import com.bank.accounts.domain.repository.MovementRepository;
import com.bank.accounts.model.dto.MovementRequest;
import com.bank.accounts.model.dto.MovementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Mono<MovementResponse> createMovement(MovementRequest movementRequest) {
        return Mono.fromCallable(() -> 
            transactionTemplate.execute(status -> {
                Account account = accountRepository.findByAccountNumber(movementRequest.getAccountNumber())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                if (movementRequest.getValue() <= 0) {
                    throw new RuntimeException("Movement value must be greater than zero");
                }

                double newBalance = calculateNewBalance(account.getInitialBalance(), movementRequest.getValue(), movementRequest.getMovementType().getValue());

                if (newBalance < 0) {
                    throw new RuntimeException("Saldo no disponible");
                }

                account.setInitialBalance(newBalance);
                accountRepository.save(account);

                Movement movement = new Movement();
                movement.setAccountId(account.getAccountId());
                movement.setDate(LocalDateTime.now());
                movement.setMovementType(movementRequest.getMovementType().getValue());
                movement.setValue(movementRequest.getValue());
                movement.setBalance(newBalance);

                return convertToResponse(movementRepository.save(movement));
            })
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private double calculateNewBalance(double currentBalance, double value, String movementType) {
        if ("Debito".equalsIgnoreCase(movementType)) {
            return currentBalance - value;
        } else if ("Credito".equalsIgnoreCase(movementType)) {
            return currentBalance + value;
        }
        throw new IllegalArgumentException("Invalid movement type: " + movementType);
    }

    private MovementResponse convertToResponse(Movement movement) {
        MovementResponse response = new MovementResponse();
        response.setMovementId(movement.getMovementId());
        response.setDate(movement.getDate().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        response.setMovementType(movement.getMovementType());
        response.setValue(movement.getValue());
        response.setBalance(movement.getBalance());
        return response;
    }
}
