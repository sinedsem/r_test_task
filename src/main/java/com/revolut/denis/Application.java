package com.revolut.denis;

import com.revolut.denis.dao.AccountRepository;
import com.revolut.denis.dao.AccountRepositoryImpl;
import com.revolut.denis.rest.AccountRestService;
import com.revolut.denis.service.AccountService;
import io.javalin.Javalin;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

public class Application {

    private static final String JDBC_URL = "jdbc:h2:mem:accounts;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) {
        Application application = new Application();
        application.start();
    }

    private void start() {
        Jdbi jdbi = Jdbi.create(JDBC_URL);

        Flyway flyway = new Flyway();
        flyway.setDataSource(JDBC_URL, "", "");
        flyway.migrate();

        AccountRepository accountRepository = new AccountRepositoryImpl(jdbi);
        AccountService accountService = new AccountService(accountRepository);
        Javalin app = Javalin.create().start(7000);
        new AccountRestService(app, accountService);
    }

}
