package com.revolut.denis.dao;

import com.revolut.denis.AbstractTestWithDB;
import com.revolut.denis.domain.Account;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AccountRepositoryImplTest extends AbstractTestWithDB {

    @Test
    public void testCreate() {
        Account account = accountRepository.create(new Account(100L));

        assertEquals(6L, account.getId().longValue());
        assertEquals(100L, account.getBalance().longValue());
    }

    @Test(expected = UnableToExecuteStatementException.class)
    public void testCreateWithNegativeAmount() {
        accountRepository.create(new Account(-25L));
    }

    @Test
    public void testGetById() {
        Optional<Account> byId = accountRepository.getById(1L);

        assertTrue(byId.isPresent());
        Account account = byId.get();
        assertEquals(1L, account.getId().longValue());
        assertEquals(100L, account.getBalance().longValue());
    }

    @Test
    public void testUpdate() {
        Optional<Account> result = accountRepository.update(new Account(1L, 25L));

        assertTrue(result.isPresent());
        Account account = result.get();
        assertEquals(1L, account.getId().longValue());
        assertEquals(25L, account.getBalance().longValue());
    }

    @Test
    public void testUpdateNotExists() {
        Optional<Account> result = accountRepository.update(new Account(7L, 25L));

        assertFalse(result.isPresent());
    }

    @Test(expected = UnableToExecuteStatementException.class)
    public void testUpdateWithNegativeAmount() {
        accountRepository.update(new Account(1L, -25L));
    }

    @Test
    public void testDelete() {
        int deletedCount = accountRepository.delete(1L);
        Optional<Account> byId = accountRepository.getById(1L);

        assertEquals(1, deletedCount);
        assertFalse(byId.isPresent());
    }

    @Test
    public void testDeleteNotExists() {
        int deletedCount = accountRepository.delete(7L);

        assertEquals(0, deletedCount);
    }

    @Test
    public void testFindAll() {
        List<Account> all = accountRepository.findAll();
        assertEquals(5, all.size());
        assertEquals(1L, all.get(0).getId().longValue());
        assertEquals(100L, all.get(0).getBalance().longValue());
        assertEquals(4L, all.get(3).getId().longValue());
        assertEquals(400L, all.get(3).getBalance().longValue());
    }

    @Test
    public void testAddAmount() {
        accountRepository.addAmount(1L, 25L);
        Optional<Account> byId = accountRepository.getById(1L);

        assertTrue(byId.isPresent());
        Account account = byId.get();
        assertEquals(1L, account.getId().longValue());
        assertEquals(125L, account.getBalance().longValue());
    }

    @Test
    public void testAddNegativeAmount() {
        accountRepository.addAmount(1L, -25L);
        Optional<Account> byId = accountRepository.getById(1L);

        assertTrue(byId.isPresent());
        Account account = byId.get();
        assertEquals(1L, account.getId().longValue());
        assertEquals(75L, account.getBalance().longValue());
    }

}