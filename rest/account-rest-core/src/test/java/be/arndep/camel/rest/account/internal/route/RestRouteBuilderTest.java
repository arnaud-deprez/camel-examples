package be.arndep.camel.rest.account.internal.route;

import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.rest.account.internal.domain.AccountRepository;
import com.jayway.jsonassert.JsonAssert;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.util.Optional;

import static org.hamcrest.Matchers.*;

public class RestRouteBuilderTest extends CamelBlueprintTestSupport {
	private AccountRepository accountRepository;

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/blueprint.xml," +
				"OSGI-INF/blueprint/blueprint-test.xml";
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		accountRepository = context.getRegistry().lookupByNameAndType("accountRepository", AccountRepository.class);
		assertThat(accountRepository, is(notNullValue()));
	}

	@Test
	public void testGetAllAccounts() {
		String out = template.requestBody("seda:get-accounts", null, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.content", JsonAssert.collectionWithSize(equalTo(2)))
				.assertThat("$.content[0].owner", is(equalTo("Arnaud")))
				.assertThat("$.content[0].balance", is(equalTo(100000D)))
				.assertThat("$.content[0].links", JsonAssert.collectionWithSize(equalTo(5)))
//				.assertThat("$.content[0].links[?(@.rel == self)].href", is(equalTo("/accounts/1")))
//				.assertThat("$.content[0].links[?(@.rel == self)].methods", containsInAnyOrder("POST", "PUT", "DELETE"))
//				.assertThat("$.content[0].links[?(@.rel == self)].types", is(equalTo(MediaType.APPLICATION_JSON)))

				.assertThat("$.content[1].owner", is(equalTo("Paul")))
				.assertThat("$.content[1].balance", is(equalTo(2082.74D)))
				.assertThat("$.content[1].links", JsonAssert.collectionWithSize(equalTo(5)))
//				.assertThat("$.content[1].links[?(@.rel == self)].href", is(equalTo("/accounts/1")))
//				.assertThat("$.content[1].links[?(@.rel == self)].methods", containsInAnyOrder("POST", "PUT", "DELETE"))
//				.assertThat("$.content[1].links[?(@.rel == self)].types", is(equalTo(MediaType.APPLICATION_JSON)));
		;
	}

	@Test
	public void testCreateAccount() {
		String createAccount = "{\"balance\": 1500, \"owner\": \"Raoul\"}";
		String out = template.requestBody("seda:post-accounts", createAccount, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.owner", is(equalTo("Raoul")))
				.assertThat("$.balance", is(equalTo(1500D)))
				.assertThat("$.links", JsonAssert.collectionWithSize(equalTo(5)));

		assertThat(accountRepository.readAll(Optional.<Long>empty(), Optional.<Long>empty()), hasSize(3));
	}

	@Test
	public void testGetAccountById() {
		String out = template.requestBodyAndHeader("seda:get-accounts-{id}", null, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.owner", is(equalTo("Arnaud")))
				.assertThat("$.balance", is(equalTo(100000D)))
				.assertThat("$.links", JsonAssert.collectionWithSize(equalTo(5)));
	}

	@Test
	public void testUpdateAccount() {
		String updateAccount = "{\"owner\": \"James\"}";
		String out = template.requestBodyAndHeader("seda:put-accounts-{id}", updateAccount, "id", 2L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.owner", is(equalTo("James")))
				.assertThat("$.balance", is(equalTo(2082.74D)))
				.assertThat("$.links", JsonAssert.collectionWithSize(equalTo(5)));

		Optional<Account> account = accountRepository.read(2L);
		assertTrue(account.isPresent());
		assertThat(account.get().getOwner(), is(equalTo("James")));
	}

	@Test
	public void testDeleteAccount() {
		String out = template.requestBodyAndHeader("seda:delete-accounts-{id}", null, "id", 2L, String.class);
		System.out.println("out = " + out);
		assertThat(out, is(nullValue()));
		assertThat(accountRepository.readAll(Optional.<Long>empty(), Optional.<Long>empty()), hasSize(1));
	}

	@Test
	public void testAccountDeposit() {
		String out = template.requestBodyAndHeader("seda:post-accounts-{id}-deposit", 500D, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.owner", is(equalTo("Arnaud")))
				.assertThat("$.balance", is(equalTo(100500D)))
				.assertThat("$.links", JsonAssert.collectionWithSize(equalTo(5)));

		Optional<Account> account = accountRepository.read(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(100500D)));
	}

	@Test
	public void testAccountWithdraw() {
		String out = template.requestBodyAndHeader("seda:post-accounts-{id}-withdraw", 500D, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.owner", is(equalTo("Arnaud")))
				.assertThat("$.balance", is(equalTo(99500D)))
				.assertThat("$.links", JsonAssert.collectionWithSize(equalTo(5)));

		Optional<Account> account = accountRepository.read(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(99500D)));
	}

	@Test
	public void testAccountTransfer() {
		String transfer = "{\"to\": 2, \"amount\": \"500\"}";
		String out = template.requestBodyAndHeader("seda:post-accounts-{id}-transfer", transfer, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.owner", is(equalTo("Arnaud")))
				.assertThat("$.balance", is(equalTo(99500D)))
				.assertThat("$.links", JsonAssert.collectionWithSize(equalTo(5)));

		Optional<Account> account = accountRepository.read(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(99500D)));

		account = accountRepository.read(2L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(2582.74D)));
	}


}