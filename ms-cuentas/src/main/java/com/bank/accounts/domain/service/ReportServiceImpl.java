package com.bank.accounts.domain.service;

import com.bank.accounts.domain.model.Account;
import com.bank.accounts.domain.model.CustomerReplica;
import com.bank.accounts.domain.repository.AccountRepository;
import com.bank.accounts.domain.repository.CustomerReplicaRepository;
import com.bank.accounts.domain.repository.MovementRepository;
import com.bank.accounts.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final CustomerReplicaRepository customerReplicaRepository;

    @Override
    public Mono<AccountStatementReport> getAccountStatement(Long clientId, LocalDate startDate, LocalDate endDate) {
        return Mono.fromCallable(() -> {
                    CustomerReplica customer = customerReplicaRepository.findById(clientId)
                            .orElseThrow(() -> new RuntimeException("Customer replica not found"));

                    AccountStatementReport report = new AccountStatementReport();
                    report.setClientId(customer.getCustomerId());
                    report.setClientName(customer.getName());

                    var accounts = accountRepository.findByClientId(clientId);
                    if (accounts.isEmpty()) {
                        throw new RuntimeException("Client has no accounts");
                    }

                    var accountDetails = accounts.stream()
                            .map(account -> toAccountReportDetails(account, startDate, endDate))
                            .collect(Collectors.toList());

                    report.setAccounts(accountDetails);
                    return report;
                }).subscribeOn(Schedulers.boundedElastic());
    }

    private AccountReportDetails toAccountReportDetails(Account account, LocalDate startDate, LocalDate endDate) {
        AccountReportDetails details = new AccountReportDetails();
        details.setAccountNumber(account.getAccountNumber());
        details.setAccountType(account.getAccountType());
        details.setInitialBalance(account.getInitialBalance());
        details.setStatus(account.isStatus());

        var movements = movementRepository.findByAccountIdAndDateBetween(
                account.getAccountId(),
                LocalDateTime.of(startDate, LocalTime.MIN),
                LocalDateTime.of(endDate, LocalTime.MAX)
        );

        var movementDetails = movements.stream()
                .map(this::toMovementReportDetails)
                .collect(Collectors.toList());

        details.setMovements(movementDetails);
        return details;
    }

    private MovementReportDetails toMovementReportDetails(com.bank.accounts.domain.model.Movement movement) {
        MovementReportDetails details = new MovementReportDetails();
        details.setDate(movement.getDate().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        details.setMovementType(movement.getMovementType());
        details.setValue(movement.getValue());
        details.setBalance(movement.getBalance());
        return details;
    }
}
