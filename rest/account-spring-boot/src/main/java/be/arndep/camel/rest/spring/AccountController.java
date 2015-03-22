package be.arndep.camel.rest.spring;

import be.arndep.camel.rest.account.api.CreateAccount;
import be.arndep.camel.rest.account.api.ReadAccount;
import be.arndep.camel.rest.account.api.ReadAccounts;
import be.arndep.camel.rest.account.api.UpdateAccount;
import be.arndep.camel.rest.account.internal.domain.AccountRepository;
import be.arndep.camel.rest.account.internal.domain.AccountService;
import be.arndep.camel.rest.account.internal.domain.exception.AccountNotFoundException;
import be.arndep.camel.rest.account.internal.mapper.AccountUpdateDataMapper;
import be.arndep.camel.rest.account.internal.mapper.CreateAccountMapper;
import be.arndep.camel.rest.account.internal.mapper.ReadAccountMapper;
import be.arndep.camel.rest.account.internal.mapper.ReadAccountsMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 20/01/15.
 */
@Api(value = "accounts", description = "Accounts in spring")
@RestController
@RequestMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class AccountController {
	private final AccountRepository accountRepository;
	private final AccountService accountService;

	@Autowired
	public AccountController(final AccountRepository accountRepository, final AccountService accountService) {
		this.accountRepository = accountRepository;
		this.accountService = accountService;
	}

	@ApiOperation(value = "Get all accounts", response = ReadAccount.class, responseContainer = "List")
	@RequestMapping(method = RequestMethod.GET)
	public HttpEntity<ReadAccounts> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") Long page,
											@RequestParam(value = "limit", required = false, defaultValue = "20") Long limit) {
		return new ResponseEntity<>(
				Optional.of(accountService.readAll(page, limit))
						.map(new ReadAccountsMapper())
						.get(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "Create an account")
	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<ReadAccount> create(CreateAccount account) {
		return new ResponseEntity<>(
				Optional.of(accountRepository.create(
						Optional.of(account)
								.map(new CreateAccountMapper())
								.get()))
						.map(new ReadAccountMapper())
						.get(),
				HttpStatus.CREATED);
	}

	@ApiOperation(value = "Get an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public HttpEntity<ReadAccount> get(@PathVariable("id") Long id) {
		return new ResponseEntity<>(
				accountRepository.read(id)
						.map(new ReadAccountMapper())
						.get(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "Update an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public HttpEntity<ReadAccount> update(@PathVariable("id") Long id, UpdateAccount accountData) throws AccountNotFoundException {
		return new ResponseEntity<>(
				Optional.of(
						accountService.update(id,
								Optional.of(accountData)
										.map(new AccountUpdateDataMapper())
										.get()))
						.map(new ReadAccountMapper())
						.get(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "Delete an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Long id) {
		accountRepository.delete(id);
	}
}
