package be.arndep.camel.account.core.services;

import be.arndep.camel.account.api.AccountType;
import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.api.ReadAccount;
import be.arndep.camel.account.core.api.impl.CreateAccountBuilderFactoryImpl;
import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.account.core.domain.BankAccountRepository;
import be.arndep.camel.account.core.repositories.impl.BankAccountMemoryRepositoryImpl;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Arnaud on 01-07-15.
 */
@RunWith(MockitoJUnitRunner.class)
public class BankAccountServiceRouteTest extends CamelTestSupport {

    private BankAccountRepository bankAccountRepository;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        bankAccountRepository = spy(new BankAccountMemoryRepositoryImpl());

        registry.bind("bankAccountRepository", bankAccountRepository);
        BankAccountService bankAccountService = new BankAccountService(bankAccountRepository);
        registry.bind("bankAccountService", bankAccountService);

        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new BankAccountServiceRoute();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    public void testFindAllAccounts() {
        Collection<ReadAccount> readAccounts = template.requestBody(BankAccountServiceRoute.ACCOUNT_FIND_ALL, null, Collection.class);
        assertThat(readAccounts, is(allOf(notNullValue(), hasSize(2))));
        verify(bankAccountRepository, times(1)).findAll(eq(Optional.empty()), eq(Optional.empty()));

        Map<String, Object> headers = new HashMap<>();
        headers.put("page", 0L);
        headers.put("limit", 2L);
        readAccounts = template.requestBodyAndHeaders(BankAccountServiceRoute.ACCOUNT_FIND_ALL, null, headers, Collection.class);
        assertThat(readAccounts, is(allOf(notNullValue(), hasSize(2))));
        verify(bankAccountRepository, times(1)).findAll(eq(Optional.of(0L)), eq(Optional.of(2L)));
    }

    @Test
    public void testCreateAccount() {
        CreateAccount createAccount = new CreateAccountBuilderFactoryImpl().newCreateAccountBuilder()
                .accountType(AccountType.GOLD)
                .owner("james")
                .build();
        ReadAccount readAccount = template.requestBody(BankAccountServiceRoute.ACCOUNT_CREATE, createAccount, ReadAccount.class);
        verify(bankAccountRepository, times(1)).create(Mockito.any(BankAccount.class));
        assertThat(readAccount, notNullValue());
        assertThat(readAccount.getId(), equalTo(3L));
        assertThat(readAccount.getOpenedDate(), notNullValue());
        assertThat(readAccount.getClosedDate(), nullValue());
        assertThat(readAccount.getBalance(), equalTo(0D));
        assertThat(readAccount.isWithdrawAble(), equalTo(true));
        assertThat(readAccount.getAccountType(), equalTo(createAccount.getAccountType()));
        assertThat(readAccount.getOwner(), equalTo(createAccount.getOwner()));
    }

    @Test
    public void testCreateAccountNotValid() {
        CreateAccount createAccount = new CreateAccountBuilderFactoryImpl().newCreateAccountBuilder().build();
        try {
            template.requestBody(BankAccountServiceRoute.ACCOUNT_CREATE, createAccount, ReadAccount.class);
            fail("Should throw a CamelExecutionException");
        }
        catch (CamelExecutionException e) {
            log.error("CreateAccount is not valid!", e);
        }
    }

    @Test
    public void testFindAccountById() {
        ReadAccount readAccount = template.requestBodyAndHeader(BankAccountServiceRoute.ACCOUNT_FIND_ID, null, "id", 1L, ReadAccount.class);
        assertThat(readAccount, notNullValue());
        verify(bankAccountRepository, times(1)).find(1L);
    }

    @Test
    public void testFindAccountByIdNotFound() {
        try {
            template.requestBodyAndHeader(BankAccountServiceRoute.ACCOUNT_FIND_ID, null, "id", 0L, ReadAccount.class);
            fail("Should throw a CamelExecutionException");
        }
        catch (CamelExecutionException e) {
            log.error("Account Not Found!", e);
            verify(bankAccountRepository, times(1)).find(0L);
        }
    }

    @Test
    public void testCloseAccount() {
        ReadAccount readAccount = template.requestBodyAndHeader(BankAccountServiceRoute.ACCOUNT_CLOSE, null, "id", 1L, ReadAccount.class);
        assertThat(readAccount, notNullValue());
        assertThat(readAccount.getClosedDate(), notNullValue());
        verify(bankAccountRepository, times(1)).find(eq(1L));
        verify(bankAccountRepository, times(1)).update(Mockito.any(BankAccount.class));

        try {
            template.requestBodyAndHeader(BankAccountServiceRoute.ACCOUNT_CLOSE, null, "id", 1L, ReadAccount.class);
            fail("Should throw an exception");
        }
        catch (CamelExecutionException e) {
            verify(bankAccountRepository, times(2)).find(eq(1L));
            verify(bankAccountRepository, times(1)).update(Mockito.any(BankAccount.class));
        }
    }

    @Test
    public void testDeposit() {
        ReadAccount readAccount = template.requestBodyAndHeader(BankAccountServiceRoute.ACCOUNT_DEPOSIT, 1000D, "id", 1L, ReadAccount.class);
        assertThat(readAccount, notNullValue());
        assertThat(readAccount.getBalance(), equalTo(101_000D));
        verify(bankAccountRepository, times(1)).find(eq(1L));
        verify(bankAccountRepository, times(1)).update(Mockito.any(BankAccount.class));
    }

    @Test
    public void testWithdraw() {
        ReadAccount readAccount = template.requestBodyAndHeader(BankAccountServiceRoute.ACCOUNT_WITHDRAW, 1000D, "id", 1L, ReadAccount.class);
        assertThat(readAccount, notNullValue());
        assertThat(readAccount.getBalance(), equalTo(99_000D));
        verify(bankAccountRepository, times(1)).find(eq(1L));
        verify(bankAccountRepository, times(1)).update(Mockito.any(BankAccount.class));
    }

    @Test
    public void testTransfer() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("from", 1L);
        headers.put("to", 2L);
        ReadAccount readAccount = template.requestBodyAndHeaders(BankAccountServiceRoute.ACCOUNT_TRANSFER, 1000D, headers, ReadAccount.class);
        assertThat(readAccount, notNullValue());
        assertThat(readAccount.getBalance(), equalTo(99_000D));
        verify(bankAccountRepository, times(1)).find(eq(1L));
        verify(bankAccountRepository, times(1)).find(eq(2L));
        verify(bankAccountRepository, times(2)).update(Mockito.any(BankAccount.class));
    }
}