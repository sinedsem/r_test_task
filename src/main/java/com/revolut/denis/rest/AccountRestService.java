package com.revolut.denis.rest;

import com.revolut.denis.domain.Account;
import com.revolut.denis.exception.OperationException;
import com.revolut.denis.exception.Reason;
import com.revolut.denis.service.AccountService;
import io.javalin.Javalin;

import java.util.Optional;

public class AccountRestService {

    public AccountRestService(Javalin app, AccountService accountService) {

        app.get("/account", ctx -> ctx.json(Response.success(accountService.findAll())));

        app.get("/account/transfer", ctx -> {
            Long to = ctx.validatedQueryParam("to").asClass(Long.class).getOrThrow();
            Long from = ctx.validatedQueryParam("from").asClass(Long.class).getOrThrow();
            Long amount = ctx.validatedQueryParam("amount").asClass(Long.class).getOrThrow();
            try {
                accountService.transferMoney(from, to, amount);
            } catch (OperationException e) {
                ctx.json(Response.failed(e.getReason()));
                return;
            }
            ctx.json(Response.success(null));
        });

        app.get("/account/:id", ctx -> {
            Long id = ctx.validatedPathParam("id").asClass(Long.class).getOrThrow();

            Optional<Account> account = accountService.getById(id);
            if (account.isPresent()) {
                ctx.json(Response.success(account.get()));
            } else {
                ctx.json(Response.failed(Reason.ACCOUNT_NOT_FOUND));
            }
        });

        app.post("/account", ctx -> {
            Account account = ctx.validatedBodyAsClass(Account.class).getOrThrow();
            account = accountService.createAccount(account);
            ctx.json(Response.success(account));
        });

        app.put("/account", ctx -> {
            Account account = ctx.validatedBodyAsClass(Account.class).getOrThrow();
            if (account.getId() == null) {
                ctx.status(400);
                return;
            }

            Optional<Account> updated = accountService.updateAccount(account);
            if (updated.isPresent()) {
                ctx.json(Response.success(updated.get()));
            } else {
                ctx.json(Response.failed(Reason.ACCOUNT_NOT_FOUND));
            }
        });

        app.delete("/account/:id", ctx -> {
            Long id = ctx.validatedPathParam("id").asClass(Long.class).getOrThrow();

            int deletedCount = accountService.deleteAccount(id);
            if (deletedCount == 1) {
                ctx.json(Response.success(null));
            } else {
                ctx.json(Response.failed(Reason.ACCOUNT_NOT_FOUND));
            }
        });
    }

}
