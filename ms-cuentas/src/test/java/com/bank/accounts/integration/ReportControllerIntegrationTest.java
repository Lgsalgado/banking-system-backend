package com.bank.accounts.integration;

import com.bank.accounts.controller.ReportController;
import com.bank.accounts.domain.service.ReportService;
import com.bank.accounts.exception.GlobalExceptionHandler;
import com.bank.accounts.model.dto.AccountStatementReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ReportController.class)
@ContextConfiguration(classes = {ReportController.class, GlobalExceptionHandler.class})
class ReportControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReportService reportService;

    @Test
    void getAccountStatement_shouldReturnReport() {
        AccountStatementReport report = new AccountStatementReport();
        report.setClientId(1L);
        report.setClientName("Jose Lema");
        report.setAccounts(Collections.emptyList());

        when(reportService.getAccountStatement(any(), any(), any())).thenReturn(Mono.just(report));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/reports")
                        .queryParam("clientId", 1L)
                        .queryParam("startDate", "2024-01-01")
                        .queryParam("endDate", "2024-12-31")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.clientName").isEqualTo("Jose Lema");
    }

    @Test
    void getAccountStatement_shouldReturnNotFound_whenClientNotFound() {
        when(reportService.getAccountStatement(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Client not found")));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/reports")
                        .queryParam("clientId", 99L)
                        .queryParam("startDate", "2024-01-01")
                        .queryParam("endDate", "2024-12-31")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }
}
