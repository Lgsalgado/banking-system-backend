package com.bank.accounts.domain.repository;

import com.bank.accounts.domain.model.CustomerReplica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerReplicaRepository extends JpaRepository<CustomerReplica, Long> {
}
