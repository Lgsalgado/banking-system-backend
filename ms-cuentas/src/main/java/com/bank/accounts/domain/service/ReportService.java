package com.bank.accounts.domain.service;

import com.bank.accounts.model.dto.AccountStatementReport;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReportService {
    Mono<AccountStatementReport> getAccountStatement(Long clientId, LocalDate startDate, LocalDate endDate);
}
