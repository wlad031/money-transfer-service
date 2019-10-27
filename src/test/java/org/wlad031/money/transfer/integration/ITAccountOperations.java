package org.wlad031.money.transfer.integration;

import io.javalin.Javalin;
import io.restassured.http.ContentType;
import org.eclipse.jetty.server.Response;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
import java.util.Currency;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ITAccountOperations extends AbstractIntegrationTest {

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

    //@formatter:off

    @Test
    public void accountCreate_ValidRequest() throws IOException {
        final var id =
                given().
                        accept(ContentType.JSON).
                        body(new JSONObject()
                                .put("name", "accountName")
                                .put("currency", "EUR")).
                when().
                        post("/account").
                then().
                        statusCode(Response.SC_CREATED).
                        body("$", hasKey("id")).
                extract().
                        path("id");
        assertThat(dataSource.getAccounts().entrySet(), hasSize(1));
        assertEquals("accountName", dataSource.getAccounts().get(UUID.fromString(String.valueOf(id))).getName());
        assertEquals("EUR", dataSource.getAccounts().get(UUID.fromString(String.valueOf(id))).getCurrency().toString());
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
                body("$", hasKey("message")).
                body("message", equalTo("Invalid account name: null"));
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
                body("$", hasKey("message")).
                body("message", equalTo("Invalid currency null"));
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
                body("$", hasKey("message")).
                body("message", equalTo("Invalid currency LOL"));
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
        final var accountId = UUID.randomUUID().toString();
        given().
                accept(ContentType.JSON).
        when().
                get("/account/" + accountId).
        then().
                statusCode(Response.SC_NOT_FOUND).
                body("$", hasKey("message")).
                body("message", equalTo("Account ID " + accountId + " not found"));
    }

    @Test
    public void getAccountDetails_InvalidId() {
        final var accountId = "hello";
        given().
                accept(ContentType.JSON).
        when().
                get("/account/" + accountId).
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("Invalid ID=" + accountId + " for field id"));
    }

    @Test
    public void getAllAccountIds_ThereAreAccounts() {
        final var accountId1 = UUID.randomUUID();
        final var accountId2 = UUID.randomUUID();
        final var accountId3 = UUID.randomUUID();
        dataSource.getAccounts().put(accountId1, new Account(accountId1, "accountName1", Currency.getInstance("EUR")));
        dataSource.getAccounts().put(accountId2, new Account(accountId2, "accountName2", Currency.getInstance("EUR")));
        dataSource.getAccounts().put(accountId3, new Account(accountId3, "accountName3", Currency.getInstance("EUR")));
        given().
                accept(ContentType.JSON).
        when().
                get("/account").
        then().
                statusCode(Response.SC_OK).
                body("$", hasKey("accountIds")).
                body("accountIds", hasSize(3)).
                body("accountIds", containsInAnyOrder(
                        accountId1.toString(), accountId2.toString(), accountId3.toString()));
    }

    @Test
    public void getAllAccountIds_ThereAreNoAccounts() {
        given().
                accept(ContentType.JSON).
        when().
                get("/account").
        then().
                statusCode(Response.SC_OK).
                body("$", hasKey("accountIds")).
                body("accountIds", hasSize(0));
    }

    //@formatter:on

}
