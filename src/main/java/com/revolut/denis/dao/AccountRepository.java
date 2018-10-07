package com.revolut.denis.dao;

import com.revolut.denis.domain.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    List<Account> findAll();

    Optional<Account> getById(Long id);

    Account create(Account account);

    Optional<Account> update(Account account);

    int delete(Long id);

    void addAmount(Long id, long amount);
}
