package com.revolut.denis;

import com.revolut.denis.dao.AccountRepositoryImpl;
import com.revolut.denis.service.AccountService;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Before;

public class AbstractTestWithDB {

    private Jdbi jdbi;
    private Flyway flyway;

    protected AccountService accountService;
    protected AccountRepositoryImpl accountRepository;


    {
        String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        jdbi = Jdbi.create(jdbcUrl);
        flyway = new Flyway();
        flyway.setDataSource(jdbcUrl, "", "");
        accountRepository = new AccountRepositoryImpl(jdbi);
        accountService = new AccountService(accountRepository);
    }

    @Before
    public void setUp() {
        flyway.migrate();
    }

    @After
    public void clear() {
        jdbi.useHandle(handle -> handle.execute("DROP ALL OBJECTS"));
    }
}
