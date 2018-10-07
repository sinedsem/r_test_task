package com.revolut.denis.service;

import com.revolut.denis.AbstractTestWithDB;
import com.revolut.denis.domain.Account;
import com.revolut.denis.exception.OperationException;
import com.revolut.denis.exception.Reason;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AccountServiceTest extends AbstractTestWithDB {

    @Test
    public void testTransferMoney() {
        Account from = accountService.createAccount(new Account(100L));
        Account to = accountService.createAccount(new Account(100L));

        accountService.transferMoney(from.getId(), to.getId(), 50L);

        assertEquals(50L, accountService.getById(from.getId()).get().getBalance().longValue());
        assertEquals(150L, accountService.getById(to.getId()).get().getBalance().longValue());
    }

    @Test
    public void testTransferMoneyMoreThanBalance() {
        Account from = accountService.createAccount(new Account(100L));
        Account to = accountService.createAccount(new Account(100L));

        try {
            accountService.transferMoney(from.getId(), to.getId(), 500L);
            fail();
        } catch (OperationException e) {
            assertEquals(Reason.NOT_ENOUGH_MONEY, e.getReason());
        }

        assertEquals(100L, accountService.getById(from.getId()).get().getBalance().longValue());
        assertEquals(100L, accountService.getById(to.getId()).get().getBalance().longValue());
    }
}
