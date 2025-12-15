package com.bank.accounts.unit;

import com.bank.accounts.domain.model.Account;
import com.bank.accounts.domain.model.Movement;
import com.bank.accounts.domain.repository.AccountRepository;
import com.bank.accounts.domain.repository.MovementRepository;
import com.bank.accounts.domain.service.MovementServiceImpl;
import com.bank.accounts.model.dto.MovementRequest;
import com.bank.accounts.model.dto.MovementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    private MovementServiceImpl movementService;

    @BeforeEach
    void setUp() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        when(transactionManager.getTransaction(any())).thenReturn(new SimpleTransactionStatus(false));
        movementService = new MovementServiceImpl(movementRepository, accountRepository, transactionTemplate);
    }

    @Test
    void createDebitMovement_shouldSucceed_whenSufficientFunds() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("12345");
        account.setInitialBalance(1000.0);

        MovementRequest request = new MovementRequest();
        request.setAccountNumber("12345");
        request.setMovementType(MovementRequest.MovementTypeEnum.DEBITO);
        request.setValue(100.0);

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Mono<MovementResponse> result = movementService.createMovement(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBalance() == 900.0)
                .verifyComplete();
    }

    @Test
    void createCreditMovement_shouldSucceed() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("12345");
        account.setInitialBalance(1000.0);

        MovementRequest request = new MovementRequest();
        request.setAccountNumber("12345");
        request.setMovementType(MovementRequest.MovementTypeEnum.CREDITO);
        request.setValue(500.0);

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Mono<MovementResponse> result = movementService.createMovement(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBalance() == 1500.0)
                .verifyComplete();
    }

    @Test
    void createMovement_shouldFail_whenInsufficientFunds() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("12345");
        account.setInitialBalance(100.0);

        MovementRequest request = new MovementRequest();
        request.setAccountNumber("12345");
        request.setMovementType(MovementRequest.MovementTypeEnum.DEBITO);
        request.setValue(200.0);

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));

        Mono<MovementResponse> result = movementService.createMovement(request);

        StepVerifier.create(result)
                .expectErrorMessage("Saldo no disponible")
                .verify();
    }

    @Test
    void createMovement_shouldFail_whenValueIsZero() {
        MovementRequest request = new MovementRequest();
        request.setAccountNumber("12345");
        request.setMovementType(MovementRequest.MovementTypeEnum.DEBITO);
        request.setValue(0.0);

        Account account = new Account();
        account.setAccountNumber("12345");
        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));

        Mono<MovementResponse> result = movementService.createMovement(request);

        StepVerifier.create(result)
                .expectErrorMessage("Movement value must be greater than zero")
                .verify();
    }

    @Test
    void createMovement_shouldFail_forInvalidMovementType() {
        MovementRequest request = new MovementRequest();
        request.setAccountNumber("12345");
        request.setValue(100.0);

        Account account = new Account();
        account.setAccountNumber("12345");
        account.setInitialBalance(1000.0);
        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));

        Mono<MovementResponse> result = movementService.createMovement(request);

        StepVerifier.create(result)
                .expectError(NullPointerException.class)                 .verify();
    }
}
