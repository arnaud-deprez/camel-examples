package be.arndep.camel.rest.spring;

import be.arndep.camel.account.api.AccountResponse;
import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.impl.domain.BankAccountRepository;
import be.arndep.camel.account.impl.services.mapper.AccountResponseMapper;
import be.arndep.camel.account.impl.services.mapper.AccountResponsesMapper;
import be.arndep.camel.account.impl.services.mapper.CreateAccountMapper;
import be.arndep.camel.account.rest.AccountResponseResourceConverter;
import be.arndep.camel.shared.rest.Resource;
import be.arndep.camel.shared.rest.Resources;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.Optional;

/**
 * Created by arnaud on 20/01/15.
 */
@Api(value = "accounts", description = "Accounts in spring")
@RestController
@RequestMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class AccountController {
	private final BankAccountRepository accountRepository;
	private final AccountResponseResourceConverter accountResponseResourceConverter;

	@Autowired
	public AccountController(final BankAccountRepository accountRepository) {
		this.accountResponseResourceConverter = new AccountResponseResourceConverter();
		this.accountRepository = accountRepository;
	}

	@ApiOperation(value = "Get all accounts", response = AccountResponse.class, responseContainer = "List")
	@RequestMapping(method = RequestMethod.GET)
	public HttpEntity<Resources<Resource<AccountResponse>>> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
																	@RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
		return new ResponseEntity<>(
				Optional.of(accountRepository.findAll(Optional.of(page), Optional.of(limit)))
								.map(new AccountResponsesMapper())
								.map(accountResponseResourceConverter::toResources)
								.get(),
						HttpStatus.OK);
	}

	@ApiOperation(value = "Create an account", response = AccountResponse.class)
	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<Resource<AccountResponse>> create(CreateAccount account) {
		return new ResponseEntity<>(
				Optional.of(accountRepository.create(
						Optional.of(account)
								.map(new CreateAccountMapper())
								.get()))
						.map(new AccountResponseMapper())
						.map(accountResponseResourceConverter::toResource)
						.get(),
				HttpStatus.CREATED);
	}

	@ApiOperation(value = "Get an account", response = AccountResponse.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public HttpEntity<Resource<AccountResponse>> get(@PathVariable("id") Long id) {
		return new ResponseEntity<>(
				accountRepository.find(id)
						.map(new AccountResponseMapper())
						.map(accountResponseResourceConverter::toResource)
						.get(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "Delete an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Long id) {
		accountRepository.delete(id);
	}
}
