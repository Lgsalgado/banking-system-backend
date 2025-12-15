package com.bank.accounts.controller;

import com.bank.accounts.controller.api.ReportsApi;
import com.bank.accounts.domain.service.ReportService;
import com.bank.accounts.model.dto.AccountStatementReport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportsApi {

    private final ReportService reportService;

    @Override
    public Mono<ResponseEntity<AccountStatementReport>> getAccountStatement(Long clientId, LocalDate startDate, LocalDate endDate, ServerWebExchange exchange) {
        return reportService.getAccountStatement(clientId, startDate, endDate)
                .map(ResponseEntity::ok);
    }
}
