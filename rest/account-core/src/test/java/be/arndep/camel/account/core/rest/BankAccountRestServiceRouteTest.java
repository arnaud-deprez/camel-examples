package be.arndep.camel.account.core.rest;

import be.arndep.camel.account.api.AccountType;
import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.account.core.domain.BankAccountRepository;
import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonassert.JsonAsserter;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by arnaud on 01/07/15.
 */
public class BankAccountRestServiceRouteTest extends CamelBlueprintTestSupport {

	private BankAccountRepository bankAccountRepository;

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/blueprint.xml," +
				"OSGI-INF/blueprint/blueprint-test.xml";
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		bankAccountRepository = context.getRegistry().lookupByNameAndType("bankAccountRepository", BankAccountRepository.class);
		assertThat(bankAccountRepository, is(notNullValue()));
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetAllAccounts() {
		String out = template.requestBody("seda://get-bankaccounts", null, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);
		JsonAssert.with(out)
				.assertThat("$.content", is(iterableWithSize(2)))

						//Check element 1
				.assertThat("$.content[0].id", is(equalTo(1)))
				.assertThat("$.content[0].accountType", is(equalTo(AccountType.BLUE.name())))
				.assertThat("$.content[0].owner", is(equalTo("Arnaud")))
				.assertThat("$.content[0].balance", is(equalTo(100000D)))
				.assertThat("$.content[0].links", is(iterableWithSize(5)))
				.assertThat("$.content[0].withdrawAble", is(equalTo(true)))
						//Check self link
				.assertThat("$.content[0].links[?(@.rel == self)][0].href", is(equalTo("/bankaccounts/1")))
				.assertThat("$.content[0].links[?(@.rel == self)][0].methods", is(containsInAnyOrder("GET", "PUT", "DELETE")))
				.assertThat("$.content[0].links[?(@.rel == self)][0].methods", is(not(containsInAnyOrder("POST"))))
				.assertThat("$.content[0].links[?(@.rel == self)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[0].links[?(@.rel == self)][0].templated", is(equalTo(false)))
						//Check deposit link
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].href", is(equalTo("/bankaccounts/1/deposit")))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[0].links[?(@.rel == deposit)][0].templated", is(equalTo(false)))
						//Check withdraw link
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].href", is(equalTo("/bankaccounts/1/withdraw")))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[0].links[?(@.rel == withdraw)][0].templated", is(equalTo(false)))
						//Check transfer link
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].href", is(equalTo("/bankaccounts/1/transfer/{to}")))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[0].links[?(@.rel == transfer)][0].templated", is(equalTo(true)))
						//Check collection link
				.assertThat("$.content[0].links[?(@.rel == collection)][0].href", is(equalTo("/bankaccounts")))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[0].links[?(@.rel == collection)][0].templated", is(equalTo(false)))

						//Check element 2
				.assertThat("$.content[1].id", is(equalTo(2)))
				.assertThat("$.content[1].accountType", is(equalTo(AccountType.BLUE.name())))
				.assertThat("$.content[1].owner", is(equalTo("Paul")))
				.assertThat("$.content[1].balance", is(equalTo(2082.74D)))
				.assertThat("$.content[1].links", is(iterableWithSize(5)))
				.assertThat("$.content[1].withdrawAble", is(equalTo(true)))
						//Check self link
				.assertThat("$.content[1].links[?(@.rel == self)][0].href", is(equalTo("/bankaccounts/2")))
				.assertThat("$.content[1].links[?(@.rel == self)][0].methods", is(containsInAnyOrder("GET", "PUT", "DELETE")))
				.assertThat("$.content[1].links[?(@.rel == self)][0].methods", is(not(containsInAnyOrder("POST"))))
				.assertThat("$.content[1].links[?(@.rel == self)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[1].links[?(@.rel == self)][0].templated", is(equalTo(false)))
						//Check deposit link
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].href", is(equalTo("/bankaccounts/2/deposit")))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[1].links[?(@.rel == deposit)][0].templated", is(equalTo(false)))
						//Check withdraw link
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].href", is(equalTo("/bankaccounts/2/withdraw")))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[1].links[?(@.rel == withdraw)][0].templated", is(equalTo(false)))
						//Check transfer link
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].href", is(equalTo("/bankaccounts/2/transfer/{to}")))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[1].links[?(@.rel == transfer)][0].templated", is(equalTo(true)))
						//Check collection link
				.assertThat("$.content[1].links[?(@.rel == collection)][0].href", is(equalTo("/bankaccounts")))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.content[1].links[?(@.rel == collection)][0].templated", is(equalTo(false)))

						//Check collection links
				.assertThat("$.links", is(iterableWithSize(1)))
				.assertThat("$.links[0].rel", is(equalTo("self")))
				.assertThat("$.links[0].href", is(equalTo("/bankaccounts")))
				.assertThat("$.links[0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.links[0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.links[0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.links[0].templated", is(equalTo(false)))
		;
	}

	@Test
	public void testCreateAccount() {
		String createAccount = "{\"accountType\": \"RED\", \"owner\": \"Raoul\"}";
		String out = template.requestBody("seda:post-bankaccounts", createAccount, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Raoul")))
						.assertThat("$.accountType", is(equalTo("RED")))
						.assertThat("$.balance", is(equalTo(0D))),
				3L,
				true);

		assertThat(bankAccountRepository.findAll(Optional.<Long>empty(), Optional.<Long>empty()), hasSize(3));
	}

	@Test
	public void testGetAccountById() {
		String out = template.requestBodyAndHeader("seda:get-bankaccounts-{id}", null, "id", 1L, String.class);
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
	public void testCloseAccount() {
		String out = template.requestBodyAndHeader("seda:put-bankaccounts-{id}-close", null, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(100000D))),
				1L,
				true);

		Optional<BankAccount> account = bankAccountRepository.find(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getClosedDate(), notNullValue());
	}

	@Test
	public void testAccountDeposit() {
		String out = template.requestBodyAndHeader("seda:post-bankaccounts-{id}-deposit", 500D, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(100500D))),
				1L,
				true);

		Optional<BankAccount> account = bankAccountRepository.find(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(100500D)));
	}

	@Test
	public void testAccountWithdraw() {
		String out = template.requestBodyAndHeader("seda:post-bankaccounts-{id}-withdraw", 500D, "id", 1L, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(99500D))),
				1L,
				true);

		Optional<BankAccount> account = bankAccountRepository.find(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(99500D)));
	}

	@Test
	public void testAccountTransfer() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("from", 1L);
		headers.put("to", 2L);
		String out = template.requestBodyAndHeaders("seda:post-bankaccounts-{from}-transfer-{to}", 500D, headers, String.class);
		assertThat(out, is(not(isEmptyOrNullString())));
		System.out.println("out = " + out);

		assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(99500D))),
				1L,
				true);

		Optional<BankAccount> account = bankAccountRepository.find(1L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(99500D)));

		account = bankAccountRepository.find(2L);
		assertTrue(account.isPresent());
		assertThat(account.get().getBalance(), is(equalTo(2582.74D)));
	}

	private void assertJsonAccount(JsonAsserter jsonAssert, Long accountId, boolean withDrawable) {
		jsonAssert.assertThat("$.id", is(equalTo(accountId.intValue())))
				.assertThat("$.withdrawAble", is(equalTo(withDrawable)))
				.assertThat("$.links", is(iterableWithSize(withDrawable ? 5 : 3)))
				//Check self link
				.assertThat("$.links[?(@.rel == self)][0].href", is(equalTo("/bankaccounts/" + accountId)))
				.assertThat("$.links[?(@.rel == self)][0].methods", is(containsInAnyOrder("GET", "PUT", "DELETE")))
				.assertThat("$.links[?(@.rel == self)][0].methods", is(not(containsInAnyOrder("POST"))))
				.assertThat("$.links[?(@.rel == self)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.links[?(@.rel == self)][0].templated", is(equalTo(false)))
						//Check deposit link
				.assertThat("$.links[?(@.rel == deposit)][0].href", is(equalTo("/bankaccounts/" + accountId + "/deposit")))
				.assertThat("$.links[?(@.rel == deposit)][0].methods", is(containsInAnyOrder("POST")))
				.assertThat("$.links[?(@.rel == deposit)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
				.assertThat("$.links[?(@.rel == deposit)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.links[?(@.rel == deposit)][0].templated", is(equalTo(false)))
						//Check collection link
				.assertThat("$.links[?(@.rel == collection)][0].href", is(equalTo("/bankaccounts")))
				.assertThat("$.links[?(@.rel == collection)][0].methods", is(containsInAnyOrder("GET", "POST")))
				.assertThat("$.links[?(@.rel == collection)][0].methods", is(not(containsInAnyOrder("PUT", "DELETE"))))
				.assertThat("$.links[?(@.rel == collection)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
				.assertThat("$.links[?(@.rel == collection)][0].templated", is(equalTo(false)));

		if (withDrawable) {
			jsonAssert
					//Check withdraw link
					.assertThat("$.links[?(@.rel == withdraw)][0].href", is(equalTo("/bankaccounts/" + accountId + "/withdraw")))
					.assertThat("$.links[?(@.rel == withdraw)][0].methods", is(containsInAnyOrder("POST")))
					.assertThat("$.links[?(@.rel == withdraw)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
					.assertThat("$.links[?(@.rel == withdraw)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
					.assertThat("$.links[?(@.rel == withdraw)][0].templated", is(equalTo(false)))
							//Check transfer link
					.assertThat("$.links[?(@.rel == transfer)][0].href", is(equalTo("/bankaccounts/" + accountId + "/transfer/{to}")))
					.assertThat("$.links[?(@.rel == transfer)][0].methods", is(containsInAnyOrder("POST")))
					.assertThat("$.links[?(@.rel == transfer)][0].methods", is(not(containsInAnyOrder("GET", "PUT", "DELETE"))))
					.assertThat("$.links[?(@.rel == transfer)][0].types", is(containsInAnyOrder(MediaType.APPLICATION_JSON)))
					.assertThat("$.links[?(@.rel == transfer)][0].templated", is(equalTo(true)));
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