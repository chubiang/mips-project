package com.mips.domain.account.repository;

import com.mips.domain.account.entity.Account;
import com.mips.domain.account.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>  {

    @Query("""
                select acc
                 from Account acc
                 where acc.user.id = :userId
                   and acc.status = :status
            """)
    Optional<Account> findByUserId(@Param("userId") Long userId, String status);

    @Query(
            value = "SELECT nextval('finance.account_number_seq')",
            nativeQuery = true
    )
    Long getNextAccountNumberSequence();


}
