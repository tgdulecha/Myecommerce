package org.course.paymentservice.repository;

import org.course.paymentservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Read-only in this service - see Account for why this duplicates auth-service's repository. */
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByEmail(String email);
}
