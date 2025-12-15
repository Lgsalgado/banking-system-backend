package com.bank.accounts.controller;

import com.bank.accounts.controller.api.AccountApi;
import com.bank.accounts.domain.service.AccountService;
import com.bank.accounts.model.dto.AccountRequest;
import com.bank.accounts.model.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAllAccounts(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(accountService.getAllAccounts()));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountById(Long id, ServerWebExchange exchange) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
        return accountRequest
                .flatMap(accountService::createAccount)
                .map(account -> ResponseEntity.status(HttpStatus.CREATED).body(account));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(Long id, Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
        return accountRequest
                .flatMap(request -> accountService.updateAccount(id, request))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(Long id, ServerWebExchange exchange) {
        return accountService.deleteAccount(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
