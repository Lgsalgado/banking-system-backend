package com.bank.clients.domain.service;

import com.bank.clients.config.RabbitMQConfig;
import com.bank.clients.domain.model.Customer;
import com.bank.clients.domain.repository.CustomerRepository;
import com.bank.clients.model.dto.CustomerRequest;
import com.bank.clients.model.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerResponse> getAllCustomers() {
        return Mono.fromCallable(customerRepository::findAll)
                .flatMapMany(Flux::fromIterable)
                .map(this::convertToResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CustomerResponse> getCustomerById(Long id) {
        return Mono.fromCallable(() -> customerRepository.findById(id)
                        .map(this::convertToResponse)
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<CustomerResponse> createCustomer(CustomerRequest customerRequest) {
        return Mono.fromCallable(() -> {
                    Customer customer = convertToEntity(customerRequest);
                    Customer savedCustomer = customerRepository.save(customer);
                    
                    // Enviar evento
                    rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EVENTS_QUEUE, convertToResponse(savedCustomer));
                    
                    return convertToResponse(savedCustomer);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<CustomerResponse> updateCustomer(Long id, CustomerRequest customerRequest) {
        return Mono.fromCallable(() -> customerRepository.findById(id)
                        .map(existingCustomer -> {
                            updateCustomerFromRequest(existingCustomer, customerRequest);
                            Customer updatedCustomer = customerRepository.save(existingCustomer);
                            
                            // Enviar evento
                            rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EVENTS_QUEUE, convertToResponse(updatedCustomer));
                            
                            return convertToResponse(updatedCustomer);
                        })
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteCustomer(Long id) {
        return Mono.fromRunnable(() -> {
                    if (customerRepository.existsById(id)) {
                        customerRepository.deleteById(id);
                    } else {
                        throw new RuntimeException("Customer not found with id: " + id);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private CustomerResponse convertToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(customer.getPersonId());
        response.setName(customer.getName());
        response.setGender(customer.getGender());
        response.setIdentification(customer.getIdentification());
        response.setAddress(customer.getAddress());
        response.setPhone(customer.getPhone());
        response.setStatus(customer.isStatus());
        return response;
    }

    private Customer convertToEntity(CustomerRequest request) {
        Customer customer = new Customer();
        updateCustomerFromRequest(customer, request);
        return customer;
    }

    private void updateCustomerFromRequest(Customer customer, CustomerRequest request) {
        customer.setName(request.getName());
        customer.setGender(request.getGender());
        customer.setIdentification(request.getIdentification());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setPassword(request.getPassword());
        customer.setStatus(request.getStatus());
    }
}
