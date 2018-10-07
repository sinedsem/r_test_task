package com.revolut.denis.service;

import com.google.common.util.concurrent.Striped;
import com.revolut.denis.dao.AccountRepository;
import com.revolut.denis.domain.Account;
import com.revolut.denis.exception.OperationException;
import com.revolut.denis.exception.Reason;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@SuppressWarnings("UnstableApiUsage")
public class AccountService {

    private final Striped<ReadWriteLock> locks = Striped.readWriteLock(1024);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> getById(Long id) {
        Lock lock = locks.get(id).readLock();
        try {
            lock.lock();
            return accountRepository.getById(id);
        } finally {
            lock.unlock();
        }
    }

    public Account createAccount(Account account) {
        return accountRepository.create(account);
    }

    public Optional<Account> updateAccount(Account account) {
        Lock lock = locks.get(account.getId()).writeLock();
        try {
            lock.lock();
            return accountRepository.update(account);
        } finally {
            lock.unlock();
        }
    }

    public int deleteAccount(Long id) {
        Lock lock = locks.get(id).writeLock();
        try {
            lock.lock();
            return accountRepository.delete(id);
        } finally {
            lock.unlock();
        }
    }

    public void transferMoney(Long from, Long to, long amount) {
        if (amount <= 0) {
            throw new OperationException(Reason.INVALID_AMOUNT);
        }
        if (from == null || to == null) {
            throw new OperationException(Reason.ACCOUNT_NOT_FOUND);
        }

        Lock firstLock = locks.get(from < to ? from : to).writeLock();
        Lock secondLock = locks.get(from < to ? to : from).writeLock();
        try {
            firstLock.lock();
            secondLock.lock();

            Optional<Account> creditAccountOptional = accountRepository.getById(to);
            if (!creditAccountOptional.isPresent()) {
                throw new OperationException(Reason.ACCOUNT_NOT_FOUND);
            }

            Optional<Account> debitAccountOptional = accountRepository.getById(from);
            if (!debitAccountOptional.isPresent()) {
                throw new OperationException(Reason.ACCOUNT_NOT_FOUND);
            }

            Account debitAccount = debitAccountOptional.get();
            if (debitAccount.getBalance() < amount) {
                throw new OperationException(Reason.NOT_ENOUGH_MONEY);
            }

            accountRepository.addAmount(from, -amount);
            accountRepository.addAmount(to, amount);

        } finally {
            firstLock.unlock();
            secondLock.unlock();
        }

    }

}
