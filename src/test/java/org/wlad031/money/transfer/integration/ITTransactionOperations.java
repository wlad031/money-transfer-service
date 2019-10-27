package org.wlad031.money.transfer.integration;

import io.javalin.Javalin;
import io.restassured.http.ContentType;
import org.eclipse.jetty.server.Response;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wlad031.money.transfer.AbstractTest;
import org.wlad031.money.transfer.command.CommandImpl;
import org.wlad031.money.transfer.config.Router;
import org.wlad031.money.transfer.controller.AccountController;
import org.wlad031.money.transfer.controller.TransactionController;
import org.wlad031.money.transfer.dao.SimpleInMemoryAccountDao;
import org.wlad031.money.transfer.dao.SimpleInMemoryDataSource;
import org.wlad031.money.transfer.dao.SimpleInMemoryTransactionDao;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;
import org.wlad031.money.transfer.query.QueryImpl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ITTransactionOperations extends AbstractIntegrationTest {

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
    public void createTransaction_ValidTransaction() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        final var id =
                given().
                        accept(ContentType.JSON).
                        body(new JSONObject()
                                .put("senderId", sender.getId().toString())
                                .put("receiverId", receiver.getId().toString())
                                .put("amountSent", "50.0")
                                .put("amountReceived", "50.0")).
                when().
                        post("/transaction").
                then().
                        statusCode(Response.SC_CREATED).
                        body("$", hasKey("id")).
                extract().
                        path("id");
        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        final var transaction = dataSource.getTransactions().get(UUID.fromString(String.valueOf(id)));
        assertEquals(UUID.fromString(String.valueOf(id)), transaction.getId());
        assertEquals(new BigDecimal("50.0"), transaction.getAmountSent().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountSent().getCurrency());
        assertEquals(new BigDecimal("50.0"), transaction.getAmountReceived().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountReceived().getCurrency());
        asyncAssert(50, 10, () -> {
            assertEquals(new BigDecimal("50.0"), dataSource.getAccounts().get(sender.getId()).getBalance());
            assertEquals(new BigDecimal("150.0"), dataSource.getAccounts().get(receiver.getId()).getBalance());
            assertEquals(Transaction.Status.COMPLETED, transaction.getStatus());
        });
    }

    @Test
    public void createTransaction_SenderAndReceiverAreNull() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("senderId", (String) null)
                        .put("receiverId", (String) null)
                        .put("amountSent", (String) null)
                        .put("amountReceived", (String) null)).
        when().
                post("/transaction").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("senderId and/or receiverId must be not null"));
        assertThat(dataSource.getTransactions().entrySet(), hasSize(0));
    }

    @Test
    public void createTransaction_SenderIsNullAndAmountSentIsNot() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("senderId", (String) null)
                        .put("receiverId", receiver.getId().toString())
                        .put("amountSent", "50.0")
                        .put("amountReceived", "50.0")).
        when().
                post("/transaction").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("senderId is null, but amountSent is not null"));
        assertThat(dataSource.getTransactions().entrySet(), hasSize(0));
    }

    @Test
    public void createTransaction_SenderIsNotNullAndAmountSentIsNull() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("senderId", sender.getId().toString())
                        .put("receiverId", receiver.getId().toString())
                        .put("amountSent", (String) null)
                        .put("amountReceived", "50.0")).
        when().
                post("/transaction").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("senderId is not null, but amountSent is null"));
        assertThat(dataSource.getTransactions().entrySet(), hasSize(0));
    }

    @Test
    public void createTransaction_ReceiverIsNullAndAmountReceivedIsNot() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("senderId", sender.getId().toString())
                        .put("receiverId", (String) null)
                        .put("amountSent", "50.0")
                        .put("amountReceived", "50.0")).
        when().
                post("/transaction").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("receiverId is null, but amountReceived is not null"));
        assertThat(dataSource.getTransactions().entrySet(), hasSize(0));
    }

    @Test
    public void createTransaction_ReceiverIsNotNullAndAmountReceivedIsNull() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        given().
                accept(ContentType.JSON).
                body(new JSONObject()
                        .put("senderId", sender.getId().toString())
                        .put("receiverId", receiver.getId().toString())
                        .put("amountSent", "50.0")
                        .put("amountReceived", (String) null)).
        when().
                post("/transaction").
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("receiverId is not null, but amountReceived is null"));
        assertThat(dataSource.getTransactions().entrySet(), hasSize(0));
    }

    @Test
    public void createTransaction_NotEnoughBalanceTransaction() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        final var id =
                given().
                        accept(ContentType.JSON).
                        body(new JSONObject()
                                .put("senderId", sender.getId().toString())
                                .put("receiverId", receiver.getId().toString())
                                .put("amountSent", "101.0")
                                .put("amountReceived", "101.0")).
                when().
                        post("/transaction").
                then().
                        statusCode(Response.SC_CREATED).
                        body("$", hasKey("id")).
                extract().
                        path("id");
        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        final var transaction = dataSource.getTransactions().get(UUID.fromString(String.valueOf(id)));
        assertEquals(UUID.fromString(String.valueOf(id)), transaction.getId());
        assertEquals(new BigDecimal("101.0"), transaction.getAmountSent().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountSent().getCurrency());
        assertEquals(new BigDecimal("101.0"), transaction.getAmountReceived().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountReceived().getCurrency());
        asyncAssert(50, 10, () -> {
            assertEquals(new BigDecimal("100.0"), dataSource.getAccounts().get(sender.getId()).getBalance());
            assertEquals(new BigDecimal("100.0"), dataSource.getAccounts().get(receiver.getId()).getBalance());
            assertEquals(Transaction.Status.ABORTED, transaction.getStatus());
        });
    }

    @Test
    public void createTransaction_ValidWithdrawal() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        final var id =
                given().
                        accept(ContentType.JSON).
                        body(new JSONObject()
                                .put("senderId", sender.getId().toString())
                                .put("amountSent", "50.0")).
                when().
                        post("/transaction").
                then().
                        statusCode(Response.SC_CREATED).
                        body("$", hasKey("id")).
                extract().
                        path("id");
        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        final var transaction = dataSource.getTransactions().get(UUID.fromString(String.valueOf(id)));
        assertEquals(UUID.fromString(String.valueOf(id)), transaction.getId());
        assertEquals(new BigDecimal("50.0"), transaction.getAmountSent().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountSent().getCurrency());
        assertNull(transaction.getAmountReceived());
        asyncAssert(50, 10, () -> {
            assertEquals(new BigDecimal("50.0"), dataSource.getAccounts().get(sender.getId()).getBalance());
            assertEquals(Transaction.Status.COMPLETED, transaction.getStatus());
        });
    }

    @Test
    public void createTransaction_NotEnoughBalanceWithdrawal() {
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(sender.getId(), sender);
        final var id =
                given().
                        accept(ContentType.JSON).
                        body(new JSONObject()
                                .put("senderId", sender.getId().toString())
                                .put("amountSent", "101.0")).
                when().
                        post("/transaction").
                then().
                        statusCode(Response.SC_CREATED).
                        body("$", hasKey("id")).
                extract().
                        path("id");
        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        final var transaction = dataSource.getTransactions().get(UUID.fromString(String.valueOf(id)));
        assertEquals(UUID.fromString(String.valueOf(id)), transaction.getId());
        assertEquals(new BigDecimal("101.0"), transaction.getAmountSent().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountSent().getCurrency());
        assertNull(transaction.getAmountReceived());
        asyncAssert(50, 10, () -> {
            assertEquals(new BigDecimal("100.0"), dataSource.getAccounts().get(sender.getId()).getBalance());
            assertEquals(Transaction.Status.ABORTED, transaction.getStatus());
        });
    }

    @Test
    public void createTransaction_ValidDeposit() {
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        dataSource.getAccounts().put(receiver.getId(), receiver);
        final var id =
                given().
                        accept(ContentType.JSON).
                        body(new JSONObject()
                                .put("receiverId", receiver.getId().toString())
                                .put("amountReceived", "50.0")).
                when().
                        post("/transaction").
                then().
                        statusCode(Response.SC_CREATED).
                        body("$", hasKey("id")).
                        extract().
                        path("id");
        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        final var transaction = dataSource.getTransactions().get(UUID.fromString(String.valueOf(id)));
        assertEquals(UUID.fromString(String.valueOf(id)), transaction.getId());
        assertNull(transaction.getAmountSent());
        assertEquals(new BigDecimal("50.0"), transaction.getAmountReceived().getAmount());
        assertEquals(Currency.getInstance("EUR"), transaction.getAmountReceived().getCurrency());
        asyncAssert(50, 10, () -> {
            assertEquals(new BigDecimal("150.0"), dataSource.getAccounts().get(receiver.getId()).getBalance());
            assertEquals(Transaction.Status.COMPLETED, transaction.getStatus());
        });
    }

    @Test
    public void getTransactionById_ExistingTransaction() {
        final var now = ZonedDateTime.now();
        final var sender = new Account(UUID.randomUUID(), "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(UUID.randomUUID(), "receiver", Currency.getInstance("EUR"));
        receiver.setBalance(new BigDecimal("100.0"));
        final var transaction = new Transaction(UUID.randomUUID(), sender.getId(), receiver.getId(),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.01")),
                new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("600.01")),
                now);

        dataSource.getAccounts().put(sender.getId(), sender);
        dataSource.getAccounts().put(receiver.getId(), receiver);
        dataSource.getTransactions().put(transaction.getId(), transaction);

        given().
                accept(ContentType.JSON).
        when().
                get("/transaction/" + transaction.getId().toString()).
        then().
                statusCode(Response.SC_OK).
                body("id", equalTo(transaction.getId().toString())).
                body("senderId", equalTo(transaction.getSenderId().toString())).
                body("receiverId", equalTo(transaction.getReceiverId().toString())).
                body("status", equalTo("PENDING")).
                body("amountSent.currency", equalTo("EUR")).
                body("amountSent.amount", equalTo("10.01")).
                body("amountReceived.currency", equalTo("RUB")).
                body("amountReceived.amount", equalTo("600.01")).
                body("dateTime", equalTo(now.toString()));
    }

    @Test
    public void getTransactionById_NonExistingTransaction() {
        final var s = UUID.randomUUID().toString();
        given().
                accept(ContentType.JSON).
        when().
                get("/transaction/" + s).
        then().
                statusCode(Response.SC_NOT_FOUND).
                body("$", hasKey("message")).
                body("message", equalTo("Transaction ID " + s + " not found"));
    }

    @Test
    public void getTransactionById_InvalidTransactionId() {
        final var s = "hello";
        given().
                accept(ContentType.JSON).
        when().
                get("/transaction/" + s).
        then().
                statusCode(Response.SC_BAD_REQUEST).
                body("$", hasKey("message")).
                body("message", equalTo("Invalid ID=" + s + " for field id"));
    }

    //@formatter:on

}
