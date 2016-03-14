package be.arndep.camel.account.rest;

import be.arndep.camel.account.api.*;
import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonassert.JsonAsserter;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * Created by arnaud on 01/07/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class BankAccountRestServiceRouteTest extends CamelBlueprintTestSupport {

	@Mock
	private AccountCommandService accountCommandService;
	@Mock
	private AccountQueryService accountQueryService;

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/blueprint.xml," +
			"OSGI-INF/blueprint/blueprint-test.xml";
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		verifyNoMoreInteractions(accountCommandService, accountQueryService);
	}

	@Override
	protected void addServicesOnStartup(Map<String, KeyValueHolder<Object, Dictionary>> services) {
		super.addServicesOnStartup(services);
		services.put("accountCommandService", asService(accountCommandService, "osgi.service.blueprint.compname", "accountCommandService"));
		services.put("accountQueryService", asService(accountQueryService, "osgi.service.blueprint.compname", "accountQueryService"));
	}

	private AccountResponse.Builder defaultResponseBuilder() {
		return AccountResponse.builder()
			.id(1L)
			.owner("Arnaud")
			.accountType(AccountType.BLUE)
			.balance(100000D)
			.openedDate(LocalDateTime.of(2016, 3, 31, 15, 02))
			.withdrawAble(true);
	}

	@Test
	public void testGetAllAccounts() {
		//Given
		when(accountQueryService.findAll(Mockito.any(Optional.class), Mockito.any(Optional.class))).thenReturn(
			Arrays.asList(
				defaultResponseBuilder()
					.id(1L)
					.owner("Arnaud")
					.accountType(AccountType.BLUE)
					.balance(100000D)
					.openedDate(LocalDateTime.of(2016, 3, 31, 15, 02))
					.build(),
				defaultResponseBuilder()
					.id(2L)
					.owner("Paul")
					.accountType(AccountType.GOLD)
					.balance(2082.74D)
					.openedDate(LocalDateTime.of(2010, 8, 31, 16, 17))
					.build()
			));

		//When
		String out = template.requestBody("seda://get-bankaccounts", null, String.class);

		//Then
		verify(accountQueryService, times(1)).findAll(Mockito.any(Optional.class), Mockito.any(Optional.class));

		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		JsonAssert.with(out)
			.assertThat("$.content", is(iterableWithSize(2)))

			//Check element 1
			.assertEquals("$.content[0].id", 1)
			.assertEquals("$.content[0].accountType", AccountType.BLUE.name())
			.assertEquals("$.content[0].owner", "Arnaud")
			.assertEquals("$.content[0].balance", 100000D)
			.assertThat("$.content[0].links", is(iterableWithSize(5)))
			.assertEquals("$.content[0].withdrawAble", true)
			//Check self link
			.assertThat("$.content[0].links[?(@.rel == 'self')].href", is(Matchers.contains("/bankaccounts/1")))
			.assertThat("$.content[0].links[?(@.rel == 'self')].methods", is(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE"))))
			.assertThat("$.content[0].links[?(@.rel == 'self')].methods", is(not(Matchers.contains(Arrays.asList("POST")))))
			.assertThat("$.content[0].links[?(@.rel == 'self')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[0].links[?(@.rel == 'self')].templated", is(Matchers.contains(false)))
			//Check deposit link
			.assertThat("$.content[0].links[?(@.rel == 'deposit')].href", is(Matchers.contains("/bankaccounts/1/deposit")))
			.assertThat("$.content[0].links[?(@.rel == 'deposit')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.content[0].links[?(@.rel == 'deposit')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.content[0].links[?(@.rel == 'deposit')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[0].links[?(@.rel == 'deposit')].templated", is(Matchers.contains(false)))
			//Check withdraw link
			.assertThat("$.content[0].links[?(@.rel == 'withdraw')].href", is(Matchers.contains("/bankaccounts/1/withdraw")))
			.assertThat("$.content[0].links[?(@.rel == 'withdraw')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.content[0].links[?(@.rel == 'withdraw')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.content[0].links[?(@.rel == 'withdraw')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[0].links[?(@.rel == 'withdraw')].templated", is(Matchers.contains(false)))
			//Check transfer link
			.assertThat("$.content[0].links[?(@.rel == 'transfer')].href", is(Matchers.contains("/bankaccounts/1/transfer/{to}")))
			.assertThat("$.content[0].links[?(@.rel == 'transfer')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.content[0].links[?(@.rel == 'transfer')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.content[0].links[?(@.rel == 'transfer')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[0].links[?(@.rel == 'transfer')].templated", is(Matchers.contains(true)))
			//Check collection link
			.assertThat("$.content[0].links[?(@.rel == 'collection')].href", is(Matchers.contains("/bankaccounts")))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].methods", is(Matchers.contains(Arrays.asList("GET", "POST"))))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].methods", is(not(Matchers.contains(Arrays.asList("PUT", "DELETE")))))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].templated", is(Matchers.contains(false)))

			//Check element 2
			.assertThat("$.content[1].id", is(equalTo(2)))
			.assertThat("$.content[1].accountType", is(equalTo(AccountType.GOLD.name())))
			.assertThat("$.content[1].owner", is(equalTo("Paul")))
			.assertThat("$.content[1].balance", is(equalTo(2082.74D)))
			.assertThat("$.content[1].links", is(iterableWithSize(5)))
			.assertThat("$.content[1].withdrawAble", is(equalTo(true)))
			//Check self link
			.assertThat("$.content[1].links[?(@.rel == 'self')].href", is(Matchers.contains("/bankaccounts/2")))
			.assertThat("$.content[1].links[?(@.rel == 'self')].methods", is(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE"))))
			.assertThat("$.content[1].links[?(@.rel == 'self')].methods", is(not(Matchers.contains(Arrays.asList("POST")))))
			.assertThat("$.content[1].links[?(@.rel == 'self')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[1].links[?(@.rel == 'self')].templated", is(Matchers.contains(false)))
			//Check deposit link
			.assertThat("$.content[1].links[?(@.rel == 'deposit')].href", is(Matchers.contains("/bankaccounts/2/deposit")))
			.assertThat("$.content[1].links[?(@.rel == 'deposit')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.content[1].links[?(@.rel == 'deposit')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.content[1].links[?(@.rel == 'deposit')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[1].links[?(@.rel == 'deposit')].templated", is(Matchers.contains(false)))
			//Check withdraw link
			.assertThat("$.content[1].links[?(@.rel == 'withdraw')].href", is(Matchers.contains("/bankaccounts/2/withdraw")))
			.assertThat("$.content[1].links[?(@.rel == 'withdraw')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.content[1].links[?(@.rel == 'withdraw')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.content[1].links[?(@.rel == 'withdraw')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[1].links[?(@.rel == 'withdraw')].templated", is(Matchers.contains(false)))
			//Check transfer link
			.assertThat("$.content[1].links[?(@.rel == 'transfer')].href", is(Matchers.contains("/bankaccounts/2/transfer/{to}")))
			.assertThat("$.content[1].links[?(@.rel == 'transfer')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.content[1].links[?(@.rel == 'transfer')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.content[1].links[?(@.rel == 'transfer')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[1].links[?(@.rel == 'transfer')].templated", is(Matchers.contains(true)))
			//Check collection link
			.assertThat("$.content[0].links[?(@.rel == 'collection')].href", is(Matchers.contains("/bankaccounts")))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].methods", is(Matchers.contains(Arrays.asList("GET", "POST"))))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].methods", is(not(Matchers.contains(Arrays.asList("PUT", "DELETE")))))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.content[0].links[?(@.rel == 'collection')].templated", is(Matchers.contains(false)))

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
		//Given
		String createAccount = "{\"accountType\": \"RED\", \"owner\": \"Raoul\"}";
		LocalDateTime openedDate = LocalDateTime.now();
		when(accountCommandService.create(any(CreateAccount.class))).thenReturn(
			defaultResponseBuilder()
				.id(1L)
				.owner("Raoul")
				.accountType(AccountType.RED)
				.balance(0D)
				.openedDate(openedDate)
				.build());

		//When
		String out = template.requestBody("seda:post-bankaccounts", createAccount, String.class);

		//Then
		verify(accountCommandService, times(1))
			.create(any(CreateAccount.class));

		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		assertJsonAccount(
			JsonAssert.with(out)
				.assertEquals("$.owner", "Raoul")
				.assertEquals("$.accountType", AccountType.RED.name())
				.assertEquals("$.balance", 0D),
//				.assertEquals("$.openedDate", openedDate.toString()),
			1L,
			true);
	}

	@Test
	public void testGetAccountById() {
		//Given
		Long id = 1L;
		when(accountQueryService.find(anyLong())).thenReturn(
			defaultResponseBuilder()
				.id(id)
				.owner("Arnaud")
				.accountType(AccountType.BLUE)
				.balance(100000D)
				.openedDate(LocalDateTime.now())
				.build());

		//When
		String out = template.requestBodyAndHeader("seda:get-bankaccounts-{id}", null, "id", id, String.class);

		//Then
		verify(accountQueryService, times(1)).find(id);
		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		assertJsonAccount(
			JsonAssert.with(out)
				.assertEquals("$.owner", "Arnaud")
				.assertEquals("$.accountType", AccountType.BLUE.name())
				.assertEquals("$.balance", 100000D),
			id,
			true);
	}

	@Test
	public void testCloseAccount() {
		//Given
		Long id = 1L;
		LocalDateTime closedDate = LocalDateTime.now();
		when(accountCommandService.close(anyLong())).thenReturn(
			defaultResponseBuilder()
				.id(id)
				.owner("Arnaud")
				.accountType(AccountType.BLUE)
				.balance(100000D)
				.openedDate(LocalDateTime.now())
				.closedDate(closedDate)
				.build());

		//When
		String out = template.requestBodyAndHeader("seda:put-bankaccounts-{id}-close", null, "id", 1L, String.class);

		//Then
		verify(accountCommandService, times(1)).close(eq(id));
		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		assertJsonAccount(
			JsonAssert.with(out)
				.assertEquals("$.owner", "Arnaud")
				.assertEquals("$.accountType", AccountType.BLUE.name())
				.assertEquals("$.balance", 100000D),
//				.assertEquals("$.closedDate", closedDate.toString()),
			1L,
			true);
	}

	@Test
	public void testAccountDeposit() {
		//Given
		Long id = 1L;
		Double amount = 500D;
		when(accountCommandService.deposit(anyLong(), anyDouble())).thenReturn(
			defaultResponseBuilder()
				.id(id)
				.owner("Arnaud")
				.accountType(AccountType.BLUE)
				.balance(100500D)
				.openedDate(LocalDateTime.now())
				.build());

		//When
		String out = template.requestBodyAndHeader("seda:post-bankaccounts-{id}-deposit", amount, "id", 1L, String.class);

		//Then
		verify(accountCommandService, times(1)).deposit(eq(id), eq(amount));
		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		assertJsonAccount(
			JsonAssert.with(out)
				.assertEquals("$.owner", "Arnaud")
				.assertEquals("$.accountType", AccountType.BLUE.name())
				.assertEquals("$.balance", 100500D),
			1L,
			true);
	}

	@Test
	public void testAccountWithdraw() {
		//Given
		Long id = 1L;
		Double amount = 500D;
		when(accountCommandService.withdraw(anyLong(), anyDouble())).thenReturn(
			defaultResponseBuilder()
				.id(id)
				.owner("Arnaud")
				.accountType(AccountType.BLUE)
				.balance(99500D)
				.openedDate(LocalDateTime.now())
				.build());

		//When
		String out = template.requestBodyAndHeader("seda:post-bankaccounts-{id}-withdraw", amount, "id", 1L, String.class);

		//Then
		verify(accountCommandService, times(1)).withdraw(eq(id), eq(amount));
		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		assertJsonAccount(
			JsonAssert.with(out)
				.assertEquals("$.owner", "Arnaud")
				.assertEquals("$.accountType", AccountType.BLUE.name())
				.assertEquals("$.balance", 99500D),
			1L,
			true);
	}

	@Test
	public void testAccountTransfer() {
		//Given
		Long from = 1L;
		Long to = 2L;
		Double amount = 500D;
		when(accountCommandService.transfer(anyLong(), anyLong(), anyDouble())).thenReturn(
			Arrays.asList(
				defaultResponseBuilder()
					.id(from)
					.owner("Arnaud")
					.accountType(AccountType.BLUE)
					.balance(99500D)
					.openedDate(LocalDateTime.now())
					.build(),
				defaultResponseBuilder()
					.id(to)
					.owner("Paul")
					.accountType(AccountType.GOLD)
					.balance(2582.74D)
					.openedDate(LocalDateTime.now())
					.build()));

		//When
		Map<String, Object> headers = new HashMap<>();
		headers.put("from", from);
		headers.put("to", to);
		String out = template.requestBodyAndHeaders("seda:post-bankaccounts-{from}-transfer-{to}", amount, headers, String.class);

		//Then
		verify(accountCommandService, times(1)).transfer(eq(from), eq(to), eq(amount));
		Assertions.assertThat(out)
			.isNotNull()
			.isNotEmpty();
		System.out.println("out = " + out);

		/*assertJsonAccount(
				JsonAssert.with(out)
						.assertThat("$.owner", is(equalTo("Arnaud")))
						.assertThat("$.balance", is(equalTo(99500D))),
				1L,
				true);*/
	}

	private void assertJsonAccount(JsonAsserter jsonAssert, Long accountId, boolean withDrawable) {
		jsonAssert.assertEquals("$.id", accountId.intValue())
			.assertEquals("$.withdrawAble", withDrawable)
			.assertThat("$.links", is(iterableWithSize(withDrawable ? 5 : 3)))
			//Check self link
			.assertThat("$.links[?(@.rel == 'self')].href", is(Matchers.contains("/bankaccounts/" + accountId)))
			.assertThat("$.links[?(@.rel == 'self')].methods", is(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE"))))
			.assertThat("$.links[?(@.rel == 'self')].methods", is(not(Matchers.contains(Arrays.asList("POST")))))
			.assertThat("$.links[?(@.rel == 'self')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.links[?(@.rel == 'self')].templated", is(Matchers.contains(false)))
			//Check deposit link
			.assertThat("$.links[?(@.rel == 'deposit')].href", is(Matchers.contains("/bankaccounts/" + accountId + "/deposit")))
			.assertThat("$.links[?(@.rel == 'deposit')].methods", is(Matchers.contains(Arrays.asList("POST"))))
			.assertThat("$.links[?(@.rel == 'deposit')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
			.assertThat("$.links[?(@.rel == 'deposit')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.links[?(@.rel == 'deposit')].templated", is(Matchers.contains(false)))
			//Check collection link
			.assertThat("$.links[?(@.rel == 'collection')].href", is(Matchers.contains("/bankaccounts")))
			.assertThat("$.links[?(@.rel == 'collection')].methods", is(Matchers.contains(Arrays.asList("GET", "POST"))))
			.assertThat("$.links[?(@.rel == 'collection')].methods", is(not(Matchers.contains(Arrays.asList("PUT", "DELETE")))))
			.assertThat("$.links[?(@.rel == 'collection')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
			.assertThat("$.links[?(@.rel == 'collection')].templated", is(Matchers.contains(false)));

		if (withDrawable) {
			jsonAssert
				//Check withdraw link
				.assertThat("$.links[?(@.rel == 'withdraw')].href", is(Matchers.contains("/bankaccounts/" + accountId + "/withdraw")))
				.assertThat("$.links[?(@.rel == 'withdraw')].methods", is(Matchers.contains(Arrays.asList("POST"))))
				.assertThat("$.links[?(@.rel == 'withdraw')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
				.assertThat("$.links[?(@.rel == 'withdraw')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
				.assertThat("$.links[?(@.rel == 'withdraw')].templated", is(Matchers.contains(false)))
				//Check transfer link
				.assertThat("$.links[?(@.rel == 'transfer')].href", is(Matchers.contains("/bankaccounts/" + accountId + "/transfer/{to}")))
				.assertThat("$.links[?(@.rel == 'transfer')].methods", is(Matchers.contains(Arrays.asList("POST"))))
				.assertThat("$.links[?(@.rel == 'transfer')].methods", is(not(Matchers.contains(Arrays.asList("GET", "PUT", "DELETE")))))
				.assertThat("$.links[?(@.rel == 'transfer')].types", is(Matchers.contains(Arrays.asList(MediaType.APPLICATION_JSON))))
				.assertThat("$.links[?(@.rel == 'transfer')].templated", is(Matchers.contains(true)));
		}
		else {
			jsonAssert
				//Check withdraw link
				.assertThat("$.links[?(@.rel == 'withdraw')]", is(emptyIterable()))
				//Check transfer link
				.assertThat("$.links[?(@.rel == 'transfer')]", is(emptyIterable()));
		}
	}
}