package com.bank.accounts.domain.service;

import com.bank.accounts.domain.model.Account;
import com.bank.accounts.domain.repository.AccountRepository;
import com.bank.accounts.model.dto.AccountRequest;
import com.bank.accounts.model.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<AccountResponse> getAllAccounts() {
        return Mono.fromCallable(accountRepository::findAll)
                .flatMapMany(Flux::fromIterable)
                .map(this::convertToResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AccountResponse> getAccountById(Long id) {
        return Mono.fromCallable(() -> accountRepository.findById(id)
                        .map(this::convertToResponse)
                        .orElseThrow(() -> new RuntimeException("Account not found with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<AccountResponse> createAccount(AccountRequest accountRequest) {
        return Mono.fromCallable(() -> {
                    Account account = convertToEntity(accountRequest);
                    return convertToResponse(accountRepository.save(account));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<AccountResponse> updateAccount(Long id, AccountRequest accountRequest) {
        return Mono.fromCallable(() -> accountRepository.findById(id)
                        .map(existingAccount -> {
                            updateAccountFromRequest(existingAccount, accountRequest);
                            return convertToResponse(accountRepository.save(existingAccount));
                        })
                        .orElseThrow(() -> new RuntimeException("Account not found with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteAccount(Long id) {
        return Mono.fromRunnable(() -> {
                    if (accountRepository.existsById(id)) {
                        accountRepository.deleteById(id);
                    } else {
                        throw new RuntimeException("Account not found with id: " + id);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private AccountResponse convertToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(AccountResponse.AccountTypeEnum.fromValue(account.getAccountType()));
        response.setInitialBalance(account.getInitialBalance());
        response.setStatus(account.isStatus());
        response.setClientId(account.getClientId());
        return response;
    }

    private Account convertToEntity(AccountRequest request) {
        Account account = new Account();
        updateAccountFromRequest(account, request);
        return account;
    }

    private void updateAccountFromRequest(Account account, AccountRequest request) {
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType().getValue());
        account.setInitialBalance(request.getInitialBalance());
        account.setStatus(request.getStatus());
        account.setClientId(request.getClientId());
    }
}
