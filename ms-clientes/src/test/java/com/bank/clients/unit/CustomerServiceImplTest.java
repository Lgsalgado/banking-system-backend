package com.bank.clients.unit;

import com.bank.clients.domain.model.Customer;
import com.bank.clients.domain.repository.CustomerRepository;
import com.bank.clients.domain.service.CustomerServiceImpl;
import com.bank.clients.model.dto.CustomerRequest;
import com.bank.clients.model.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void createCustomer_shouldReturnCustomerResponseAndSendEvent() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Jose Lema");
        request.setPassword("1234");
        request.setStatus(true);
        request.setIdentification("1234567890");

        Customer savedCustomer = new Customer();
        savedCustomer.setPersonId(1L);
        savedCustomer.setName("Jose Lema");
        savedCustomer.setPassword("1234");
        savedCustomer.setStatus(true);
        savedCustomer.setIdentification("1234567890");

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Mono<CustomerResponse> result = customerService.createCustomer(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals("Jose Lema"))
                .verifyComplete();
        
        verify(rabbitTemplate).convertAndSend(eq("customer.events.queue"), any(CustomerResponse.class));
    }

    @Test
    void getAllCustomers_shouldReturnAllCustomers() {
        Customer customer1 = new Customer();
        customer1.setPersonId(1L);
        customer1.setName("Customer 1");

        Customer customer2 = new Customer();
        customer2.setPersonId(2L);
        customer2.setName("Customer 2");

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        Flux<CustomerResponse> result = customerService.getAllCustomers();

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setPersonId(1L);
        customer.setName("Jose Lema");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Mono<CustomerResponse> result = customerService.getCustomerById(1L);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals("Jose Lema"))
                .verifyComplete();
    }

    @Test
    void updateCustomer_shouldUpdateAndReturnCustomer() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Jose Lema Updated");
        request.setPassword("5678");
        request.setStatus(true);

        Customer existingCustomer = new Customer();
        existingCustomer.setPersonId(1L);
        existingCustomer.setName("Jose Lema");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setPersonId(1L);
        updatedCustomer.setName("Jose Lema Updated");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Mono<CustomerResponse> result = customerService.updateCustomer(1L, request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals("Jose Lema Updated"))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend(eq("customer.events.queue"), any(CustomerResponse.class));
    }

    @Test
    void updateCustomer_shouldFail_whenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        Mono<CustomerResponse> result = customerService.updateCustomer(99L, new CustomerRequest());

        StepVerifier.create(result)
                .expectErrorMessage("Customer not found with id: 99")
                .verify();
    }

    @Test
    void deleteCustomer_shouldComplete_whenCustomerExists() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        Mono<Void> result = customerService.deleteCustomer(1L);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void deleteCustomer_shouldFail_whenCustomerNotFound() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        Mono<Void> result = customerService.deleteCustomer(99L);

        StepVerifier.create(result)
                .expectErrorMessage("Customer not found with id: 99")
                .verify();
    }
}
