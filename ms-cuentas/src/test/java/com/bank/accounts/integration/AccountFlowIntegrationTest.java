package com.bank.accounts.integration;

import com.bank.accounts.controller.AccountController;
import com.bank.accounts.controller.MovementController;
import com.bank.accounts.domain.service.AccountService;
import com.bank.accounts.domain.service.MovementService;
import com.bank.accounts.exception.GlobalExceptionHandler;
import com.bank.accounts.model.dto.AccountRequest;
import com.bank.accounts.model.dto.AccountResponse;
import com.bank.accounts.model.dto.MovementRequest;
import com.bank.accounts.model.dto.MovementResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {AccountController.class, MovementController.class})
@ContextConfiguration(classes = {AccountController.class, MovementController.class, GlobalExceptionHandler.class})
class AccountFlowIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    @MockBean
    private MovementService movementService;

    @Test
    void createAccountAndMovements_shouldWork() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountNumber("12345");
        accountRequest.setAccountType(AccountRequest.AccountTypeEnum.AHORRO);
        accountRequest.setInitialBalance(1000.0);
        accountRequest.setStatus(true);
        accountRequest.setClientId(1L);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountId(1L);
        accountResponse.setAccountNumber("12345");
        accountResponse.setInitialBalance(1000.0);

        when(accountService.createAccount(any(AccountRequest.class))).thenReturn(Mono.just(accountResponse));

        webTestClient.post().uri("/api/v1/accounts")
                .body(Mono.just(accountRequest), AccountRequest.class)
                .exchange()
                .expectStatus().isCreated();

        MovementRequest creditRequest = new MovementRequest();
        creditRequest.setAccountNumber("12345");
        creditRequest.setMovementType(MovementRequest.MovementTypeEnum.CREDITO);
        creditRequest.setValue(500.0);

        MovementResponse creditResponse = new MovementResponse();
        creditResponse.setBalance(1500.0);

        when(movementService.createMovement(any(MovementRequest.class))).thenReturn(Mono.just(creditResponse));

        webTestClient.post().uri("/api/v1/movements")
                .body(Mono.just(creditRequest), MovementRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(1500.0);
    }

    @Test
    void createMovement_shouldFail_whenInsufficientFunds() {
        MovementRequest debitRequest = new MovementRequest();
        debitRequest.setAccountNumber("12345");
        debitRequest.setMovementType(MovementRequest.MovementTypeEnum.DEBITO);
        debitRequest.setValue(2000.0);

        when(movementService.createMovement(any(MovementRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Saldo no disponible")));

        webTestClient.post().uri("/api/v1/movements")
                .body(Mono.just(debitRequest), MovementRequest.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createAccount_shouldFail_whenValidationFails() {
        // Enviar request vacío para provocar error de validación
        AccountRequest invalidRequest = new AccountRequest();

        webTestClient.post().uri("/api/v1/accounts")
                .body(Mono.just(invalidRequest), AccountRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation error");
    }

    @Test
    void createAccount_shouldFail_whenInputIsInvalid() {
        // Enviar JSON mal formado
        webTestClient.post().uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"accountNumber\": }") // JSON inválido
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid input");
    }

    @Test
    void createAccount_shouldFail_whenUnexpectedErrorOccurs() {
        AccountRequest request = new AccountRequest();
        request.setAccountNumber("12345");
        request.setAccountType(AccountRequest.AccountTypeEnum.AHORRO);
        request.setInitialBalance(1000.0);
        request.setStatus(true);
        request.setClientId(1L);

        when(accountService.createAccount(any(AccountRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Unexpected database error")));

        webTestClient.post().uri("/api/v1/accounts")
                .body(Mono.just(request), AccountRequest.class)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unexpected database error");
    }
}
