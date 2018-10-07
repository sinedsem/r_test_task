package com.revolut.denis.domain;

public class Account {
    private Long id;
    private Long balance = 0L;

    public Account() {
    }

    public Account(Long id, Long balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account(Long balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        if (balance != null && balance >= 0) {
            this.balance = balance;
        }
    }
}
