package com.bank.accounts.unit;

import com.bank.accounts.domain.model.Account;
import com.bank.accounts.domain.repository.AccountRepository;
import com.bank.accounts.domain.service.AccountServiceImpl;
import com.bank.accounts.model.dto.AccountRequest;
import com.bank.accounts.model.dto.AccountResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount_shouldReturnAccountResponse() {
        AccountRequest request = new AccountRequest();
        request.setAccountNumber("12345");
        request.setAccountType(AccountRequest.AccountTypeEnum.AHORRO);
        request.setInitialBalance(1000.0);
        request.setStatus(true);
        request.setClientId(1L);

        Account savedAccount = new Account();
        savedAccount.setAccountId(1L);
        savedAccount.setAccountNumber("12345");
        savedAccount.setAccountType("Ahorro");
        savedAccount.setInitialBalance(1000.0);
        savedAccount.setStatus(true);
        savedAccount.setClientId(1L);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Mono<AccountResponse> result = accountService.createAccount(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getAccountNumber().equals("12345"))
                .verifyComplete();
    }

    @Test
    void getAllAccounts_shouldReturnAllAccounts() {
        Account account1 = new Account();
        account1.setAccountId(1L);
        account1.setAccountNumber("111");
        account1.setAccountType("Ahorro");

        Account account2 = new Account();
        account2.setAccountId(2L);
        account2.setAccountNumber("222");
        account2.setAccountType("Corriente");

        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        Flux<AccountResponse> result = accountService.getAllAccounts();

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void deleteAccount_shouldComplete_whenAccountExists() {
        when(accountRepository.existsById(1L)).thenReturn(true);
        doNothing().when(accountRepository).deleteById(1L);

        Mono<Void> result = accountService.deleteAccount(1L);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAccountById_shouldFail_whenNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        Mono<AccountResponse> result = accountService.getAccountById(99L);

        StepVerifier.create(result)
                .expectErrorMessage("Account not found with id: 99")
                .verify();
    }
}
