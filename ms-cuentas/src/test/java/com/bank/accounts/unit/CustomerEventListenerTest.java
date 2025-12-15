package com.bank.accounts.unit;

import com.bank.accounts.domain.model.CustomerReplica;
import com.bank.accounts.domain.repository.CustomerReplicaRepository;
import com.bank.accounts.domain.service.CustomerEventListener;
import com.bank.accounts.model.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerEventListenerTest {

    @Mock
    private CustomerReplicaRepository repository;

    @InjectMocks
    private CustomerEventListener eventListener;

    @Test
    void handleCustomerEvent_shouldSaveCustomerReplica() {
        CustomerResponse customerEvent = new CustomerResponse();
        customerEvent.setCustomerId(1L);
        customerEvent.setName("Jose Lema");

        eventListener.handleCustomerEvent(customerEvent);

        verify(repository).save(any(CustomerReplica.class));
    }
}
