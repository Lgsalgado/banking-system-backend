package com.bank.accounts.domain.service;

import com.bank.accounts.domain.model.CustomerReplica;
import com.bank.accounts.domain.repository.CustomerReplicaRepository;
import com.bank.accounts.model.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerReplicaRepository repository;

    @RabbitListener(queues = "customer.events.queue")
    public void handleCustomerEvent(CustomerResponse customer) {
        System.out.println("Received customer event: " + customer.getCustomerId());
        repository.save(new CustomerReplica(customer.getCustomerId(), customer.getName()));
    }
}
