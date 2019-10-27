package org.wlad031.money.transfer.command;

import org.junit.Before;
import org.junit.Test;
import org.wlad031.money.transfer.dao.AccountDao;
import org.wlad031.money.transfer.dao.TransactionDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class CommandImplTest {

    private Command command;
    private AccountDao accountDao;
    private TransactionDao transactionDao;

    @Before
    public void setUp() throws Exception {
        accountDao = mock(AccountDao.class);
        transactionDao = mock(TransactionDao.class);
        command = new CommandImpl(accountDao, transactionDao);
    }

    @Test
    public void createNewAccount() {
    }

    @Test
    public void createNewTransaction() {
    }

    @Test
    public void withdraw() {
    }

    @Test
    public void deposit() {
    }
}