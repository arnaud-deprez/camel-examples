package be.arndep.camel.rest.account.internal.route;

import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.rest.account.internal.domain.AccountRepository;
import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.JsonPath;
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
				.assertThat("$.content", is(iterableWithSize(2)))

						//Check element 1
				.assertThat("$.content[0].owner", is(equalTo("Arnaud")))
				.assertThat("$.content[0].balance", is(equalTo(100000D)))
				.assertThat("$.content[0].links", is(iterableWithSize(5)))
						//Check self link
				.assertThat("$.content[0].links[?(@.rel == self)][0].href", is(equalTo("/accounts/1")))
				.assertThat("$.content[0].links[?(@.rel == self)][0].methods", is(containsInAnyOrder("GET", "PUT", "DELETE")))
				.assertThat("$.content[0].links[?(@.rel == self)][0].methods", is(not(containsInAnyOrder("POST"))))
				.assertThat("$.content[0].links[?(@.rel == self)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check deposit link
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].href", is(equalTo("/accounts/1/deposit")))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check withdraw link
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].href", is(equalTo("/accounts/1/withdraw")))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check transfer link
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].href", is(equalTo("/accounts/1/transfer")))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check collection link
				.assertThat("$.content[0].links[?(@.rel == collection)][0].href", is(equalTo("/accounts")))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))

						//Check element 2
				.assertThat("$.content[1].owner", is(equalTo("Paul")))
				.assertThat("$.content[1].balance", is(equalTo(2082.74D)))
				.assertThat("$.content[1].links", is(iterableWithSize(5)))
						//Check self link
				.assertThat("$.content[1].links[?(@.rel == self)][0].href", is(equalTo("/accounts/2")))
				.assertThat("$.content[1].links[?(@.rel == self)][0].methods", is(containsInAnyOrder("GET", "PUT", "DELETE")))
				.assertThat("$.content[1].links[?(@.rel == self)][0].methods", is(not(containsInAnyOrder("POST"))))
				.assertThat("$.content[1].links[?(@.rel == self)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check deposit link
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].href", is(equalTo("/accounts/2/deposit")))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check withdraw link
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].href", is(equalTo("/accounts/2/withdraw")))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check transfer link
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].href", is(equalTo("/accounts/2/transfer")))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check collection link
				.assertThat("$.content[1].links[?(@.rel == collection)][0].href", is(equalTo("/accounts")))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))

						//Check collection links
				.assertThat("$.links", is(iterableWithSize(1)))
				.assertThat("$.links[0].rel", is(equalTo("self")))
				.assertThat("$.links[0].href", is(equalTo("/accounts")))
				.assertThat("$.links[0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.links[0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.links[0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
		;
	}

	@Test
	public void testCreateAccount() {
		String createAccount = "{\"balance\": 0, \"owner\": \"Raoul\"}";
		String out = template.requestBody("seda:post-accounts", createAccount, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Raoul")))
						.assertThat("$.balance", is(equalTo(0D))),
				3L,
				false);

		assertThat(accountRepository.readAll(Optional.<Long>empty(), Optional.<Long>empty()), hasSize(3));
	}

	@Test
	public void testGetAccountById() {
		String out = template.requestBodyAndHeader("seda:get-accounts-{id}", null, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(100000D)))
				, 1L,
				true);
	}

	@Test
	public void testUpdateAccount() {
		String updateAccount = "{\"owner\": \"James\"}";
		String out = template.requestBodyAndHeader("seda:put-accounts-{id}", updateAccount, "id", 2L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("James")))
						.assertThat("$.balance", is(equalTo(2082.74D))),
				2L,
				true
		);

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

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(100500D))),
				1L,
				true);

		Optional<Account> account = accountRepository.read(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(100500D)));
	}

	@Test
	public void testAccountWithdraw() {
		String out = template.requestBodyAndHeader("seda:post-accounts-{id}-withdraw", 500D, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(99500D))),
				1L,
				true);

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

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(99500D))),
				1L,
				true);

		Optional<Account> account = accountRepository.read(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(99500D)));

		account = accountRepository.read(2L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(2582.74D)));
	}

	private void assertJsonAccount(JsonAsserter jsonAssert, Long accountId, boolean withDrawable) {
		jsonAssert.assertThat("$.links", is(iterableWithSize(withDrawable ? 5 : 3)))
				//Check self link
				.assertThat("$.links[?(@.rel == self)][0].href", is(equalTo("/accounts/" + accountId)))
				.assertThat("$.links[?(@.rel == self)][0].methods", is(containsInAnyOrder("GET", "PUT", "DELETE")))
				.assertThat("$.links[?(@.rel == self)][0].methods", is(not(containsInAnyOrder("POST"))))
				.assertThat("$.links[?(@.rel == self)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check deposit link
				.assertThat("$.links[?(@.rel == deposit)][0].href", is(equalTo("/accounts/" + accountId + "/deposit")))
				.assertThat("$.links[?(@.rel == deposit)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.links[?(@.rel == deposit)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.links[?(@.rel == deposit)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
						//Check collection link
				.assertThat("$.links[?(@.rel == collection)][0].href", is(equalTo("/accounts")))
				.assertThat("$.links[?(@.rel == collection)][0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.links[?(@.rel == collection)][0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.links[?(@.rel == collection)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)));

		if (withDrawable) {
			jsonAssert
					//Check withdraw link
					.assertThat("$.links[?(@.rel == withdraw)][0].href", is(equalTo("/accounts/" + accountId + "/withdraw")))
					.assertThat("$.links[?(@.rel == withdraw)][0].methods", is(containsInAnyOrder("POST")))
					.assertThat("$.links[?(@.rel == withdraw)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
					.assertThat("$.links[?(@.rel == withdraw)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
							//Check transfer link
					.assertThat("$.links[?(@.rel == transfer)][0].href", is(equalTo("/accounts/" + accountId + "/transfer")))
					.assertThat("$.links[?(@.rel == transfer)][0].methods", is(containsInAnyOrder("POST")))
					.assertThat("$.links[?(@.rel == transfer)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
					.assertThat("$.links[?(@.rel == transfer)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)));
		}
		else {
			jsonAssert
					//Check withdraw link
					.assertThat("$.links[?(@.rel == withdraw)]", is(emptyIterable()))
							//Check transfer link
					.assertThat("$.links[?(@.rel == transfer)]", is(emptyIterable()));
		}
	}

}