package org.wlad031.money.transfer.integration;

import io.javalin.Javalin;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Response;
import org.json.JSONObject;
import org.junit.*;
import org.mockito.Matchers;
import org.wlad031.money.transfer.command.CommandImpl;
import org.wlad031.money.transfer.config.Router;
import org.wlad031.money.transfer.controller.AccountController;
import org.wlad031.money.transfer.controller.TransactionController;
import org.wlad031.money.transfer.dao.SimpleInMemoryAccountDao;
import org.wlad031.money.transfer.dao.SimpleInMemoryDataSource;
import org.wlad031.money.transfer.dao.SimpleInMemoryTransactionDao;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.query.QueryImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.internal.common.assertion.AssertParameter.notNull;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ITAccountOperations {

    private static SimpleInMemoryDataSource dataSource;
    private static Javalin javalin;

    @BeforeClass
    public static void setUpClass() {
        dataSource = new SimpleInMemoryDataSource();
        final var accountDao = new SimpleInMemoryAccountDao(dataSource);
        final var transactionDao = new SimpleInMemoryTransactionDao(dataSource);
        final var query = new QueryImpl(accountDao, transactionDao);
        final var command = new CommandImpl(accountDao, transactionDao);
        final var accountController = new AccountController(query, command);
        final var transactionController = new TransactionController(query, command);
        javalin = createJavalin();
        final var router = new Router(accountController, transactionController);
        router.bindRoutes(javalin);
        javalin.start(8080);
    }

    @After
    public void tearDown() {
        dataSource.getAccounts().clear();
        dataSource.getTransactions().clear();
    }

    @AfterClass
    public static void tearDownClass() {
        javalin.stop();
    }

    @Test
    public void accountCreate_ValidRequest() throws IOException {
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("name", "accountName")
                        .put("currency", "EUR")).
        when().
                post("/account").
        then().
                statusCode(Response.SC_CREATED).
                body("$", hasKey("id"));
        assertThat(dataSource.getAccounts().entrySet(), hasSize(1));
        assertEquals("accountName", new ArrayList<>(dataSource.getAccounts().values()).get(0).getName());
        assertEquals("EUR", new ArrayList<>(dataSource.getAccounts().values()).get(0).getCurrency().toString());
    }

    @Test
    public void accountCreate_NullName() throws IOException {
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("name", (String) null)
                        .put("currency", "EUR")).
        when().
                post("/account").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message"));
        assertThat(dataSource.getAccounts().entrySet(), hasSize(0));
    }

    @Test
    public void accountCreate_NullCurrency() throws IOException {
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("name", "accountName")
                        .put("currency", (String) null)).
        when().
                post("/account").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message"));
        assertThat(dataSource.getAccounts().entrySet(), hasSize(0));
    }

    @Test
    public void accountCreate_InvalidCurrency() throws IOException {
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("name", "accountName")
                        .put("currency", "LOL")).
        when().
                post("/account").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message"));
        assertThat(dataSource.getAccounts().entrySet(), hasSize(0));
    }

    @Test
    public void getAccountDetails_ExistingAccount() {
        final var accountId = UUID.randomUUID();
        dataSource.getAccounts().put(accountId, new Account(accountId, "accountName", Currency.getInstance("EUR")));
        given().
                accept(ContentType.JSON).
        when().
                get("/account/" + accountId.toString()).
        then().
                statusCode(Response.SC_OK).
                body("name", equalTo("accountName")).
                body("currency", equalTo("EUR"));
    }

    @Test
    public void getAccountDetails_NonExistingAccount() {
        final var accountId = UUID.randomUUID();
        given().
                accept(ContentType.JSON).
        when().
                get("/account/" + accountId.toString()).
        then().
                statusCode(Response.SC_NOT_FOUND).
                body("$", hasKey("message"));
    }

    private static Javalin createJavalin() {
        return Javalin
                .create(config -> {
                    config.showJavalinBanner = false;
                    config.defaultContentType = "application/json";
                });
    }
}
