package com.bank.accounts.domain.service;

import com.bank.accounts.model.dto.AccountRequest;
import com.bank.accounts.model.dto.AccountResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Flux<AccountResponse> getAllAccounts();
    Mono<AccountResponse> getAccountById(Long id);
    Mono<AccountResponse> createAccount(AccountRequest accountRequest);
    Mono<AccountResponse> updateAccount(Long id, AccountRequest accountRequest);
    Mono<Void> deleteAccount(Long id);
}
