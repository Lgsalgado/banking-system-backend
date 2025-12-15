package com.bank.accounts.unit;

import com.bank.accounts.domain.model.Account;
import com.bank.accounts.domain.model.CustomerReplica;
import com.bank.accounts.domain.repository.AccountRepository;
import com.bank.accounts.domain.repository.CustomerReplicaRepository;
import com.bank.accounts.domain.repository.MovementRepository;
import com.bank.accounts.domain.service.ReportServiceImpl;
import com.bank.accounts.model.dto.AccountStatementReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private CustomerReplicaRepository customerReplicaRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void getAccountStatement_shouldReturnReport() {
        CustomerReplica customer = new CustomerReplica(1L, "Jose Lema");
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("12345");

        when(customerReplicaRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByClientId(1L)).thenReturn(List.of(account)); // Devolver una lista con una cuenta
        when(movementRepository.findByAccountIdAndDateBetween(any(), any(), any())).thenReturn(List.of());

        Mono<AccountStatementReport> result = reportService.getAccountStatement(1L, LocalDate.now(), LocalDate.now());

        StepVerifier.create(result)
                .expectNextMatches(report -> report.getClientName().equals("Jose Lema") && !report.getAccounts().isEmpty())
                .verifyComplete();
    }

    @Test
    void getAccountStatement_shouldFail_whenCustomerReplicaNotFound() {
        when(customerReplicaRepository.findById(99L)).thenReturn(Optional.empty());

        Mono<AccountStatementReport> result = reportService.getAccountStatement(99L, LocalDate.now(), LocalDate.now());

        StepVerifier.create(result)
                .expectErrorMessage("Customer replica not found")
                .verify();
    }
}
