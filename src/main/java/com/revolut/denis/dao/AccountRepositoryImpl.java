package com.revolut.denis.dao;

import com.revolut.denis.domain.Account;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {

    private final Jdbi jdbi;

    public AccountRepositoryImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public List<Account> findAll() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT ID, BALANCE FROM ACCOUNTS ORDER BY ID")
                .mapToBean(Account.class).list());
    }

    @Override
    public Optional<Account> getById(Long id) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT ID, BALANCE FROM ACCOUNTS WHERE ID = :id")
                .bind("id", id)
                .mapToBean(Account.class).findFirst());
    }

    @Override
    public Account create(Account account) {
        return jdbi.withHandle(handle -> handle.createUpdate("INSERT INTO ACCOUNTS (BALANCE) VALUES (:balance)")
                .bind("balance", account.getBalance())
                .executeAndReturnGeneratedKeys("ID")
                .map((rs, ctx) -> new Account(rs.getLong("ID"), account.getBalance())).findOnly());
    }

    @Override
    public Optional<Account> update(Account account) {
        jdbi.useHandle(handle -> handle.createUpdate("UPDATE ACCOUNTS SET BALANCE = :balance WHERE ID = :id")
                .bindBean(account)
                .execute());
        return getById(account.getId());
    }

    @Override
    public int delete(Long id) {
        return jdbi.withHandle(handle -> handle.createUpdate("DELETE FROM ACCOUNTS WHERE ID = :id")
                .bind("id", id)
                .execute());
    }

    @Override
    public void addAmount(Long id, long amount) {
        jdbi.withHandle(handle -> handle.createUpdate("UPDATE ACCOUNTS SET BALANCE = BALANCE + :amount WHERE ID = :id")
                .bind("amount", amount)
                .bind("id", id)
                .execute());
    }
}
