package com.bank.clients.integration;

import com.bank.clients.controller.CustomerController;
import com.bank.clients.domain.service.CustomerService;
import com.bank.clients.exception.GlobalExceptionHandler;
import com.bank.clients.model.dto.CustomerRequest;
import com.bank.clients.model.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CustomerController.class)
@ContextConfiguration(classes = {CustomerController.class, GlobalExceptionHandler.class})
class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    @Test
    void createCustomer_shouldReturnCreated() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Marianela Montalvo");
        request.setGender("Female");
        request.setIdentification("0987654321");
        request.setAddress("Amazonas y NNUU");
        request.setPhone("097548965");
        request.setPassword("5678");
        request.setStatus(true);

        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(1L);
        response.setName("Marianela Montalvo");

        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/customers")
                .body(Mono.just(request), CustomerRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Marianela Montalvo");
    }

    @Test
    void getCustomerById_shouldReturnNotFound() {
        when(customerService.getCustomerById(99L)).thenReturn(Mono.error(new RuntimeException("Customer not found")));

        webTestClient.get().uri("/api/v1/customers/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateCustomer_shouldReturnUpdatedCustomer() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Jose Lema Updated");
        request.setGender("Male");
        request.setIdentification("1234567890");
        request.setAddress("Otavalo");
        request.setPhone("098254785");
        request.setPassword("1234");
        request.setStatus(true);

        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(1L);
        response.setName("Jose Lema Updated");

        when(customerService.updateCustomer(eq(1L), any(CustomerRequest.class))).thenReturn(Mono.just(response));

        webTestClient.put().uri("/api/v1/customers/1")
                .body(Mono.just(request), CustomerRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Jose Lema Updated");
    }

    @Test
    void deleteCustomer_shouldReturnNoContent() {
        when(customerService.deleteCustomer(1L)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/customers/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
