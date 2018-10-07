package com.revolut.denis.dao;

import com.revolut.denis.AbstractTestWithDB;
import com.revolut.denis.domain.Account;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ConcurrencyTest extends AbstractTestWithDB {


    @Test
    public void testTransferMoney() throws InterruptedException {
        Random random = new Random(1);
        List<Callable<Long>> tasks = new ArrayList<>();
        Account from = accountService.createAccount(new Account(100L));
        Account to = accountService.createAccount(new Account(100L));


        for (int i = 0; i < 50; i++) {
            boolean swap = random.nextBoolean();

            Account finalFrom = swap ? to : from;
            Account finalTo = swap ? from : to;
            long amount = random.nextInt(100);

            tasks.add(() -> {
                accountService.transferMoney(finalFrom.getId(), finalTo.getId(), amount);
                if (finalFrom == from) {
                    return amount;
                } else {
                    return -amount;
                }
            });
        }

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<Long>> futures = executorService.invokeAll(tasks);


        long transferred = 0L;

        for (Future<Long> future : futures) {
            try {
                transferred += future.get();
            } catch (ExecutionException ignored) {
            }
        }

        assertEquals(100L - transferred, accountService.getById(from.getId()).get().getBalance().longValue());
        assertEquals(100L + transferred, accountService.getById(to.getId()).get().getBalance().longValue());
    }
}