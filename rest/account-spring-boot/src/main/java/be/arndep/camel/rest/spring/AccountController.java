package be.arndep.camel.rest.spring;

import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.api.ReadAccount;
import be.arndep.camel.account.core.domain.BankAccountRepository;
import be.arndep.camel.account.core.rest.converter.ReadAccountResourceConverter;
import be.arndep.camel.account.core.services.BankAccountService;
import be.arndep.camel.account.core.services.mapper.CreateAccountMapper;
import be.arndep.camel.account.core.services.mapper.ReadAccountMapper;
import be.arndep.camel.account.core.services.mapper.ReadAccountsMapper;
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
	private final BankAccountService accountService;
	private final ReadAccountResourceConverter readAccountResourceConverter;

	@Autowired
	public AccountController(final BankAccountRepository accountRepository, final BankAccountService accountService) {
		this.readAccountResourceConverter = new ReadAccountResourceConverter();
		this.accountRepository = accountRepository;
		this.accountService = accountService;
	}

	@ApiOperation(value = "Get all accounts", response = ReadAccount.class, responseContainer = "List")
	@RequestMapping(method = RequestMethod.GET)
	public HttpEntity<Resources<Resource<ReadAccount>>> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") Long page,
											@RequestParam(value = "limit", required = false, defaultValue = "20") Long limit) {
		return new ResponseEntity<>(
				Optional.of(accountRepository.findAll(Optional.of(page), Optional.of(limit)))
								.map(new ReadAccountsMapper())
								.map(readAccountResourceConverter::toResources)
								.get(),
						HttpStatus.OK);
	}

	@ApiOperation(value = "Create an account", response = ReadAccount.class)
	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<Resource<ReadAccount>> create(CreateAccount account) {
		return new ResponseEntity<>(
				Optional.of(accountRepository.create(
						Optional.of(account)
								.map(new CreateAccountMapper())
								.get()))
						.map(new ReadAccountMapper())
						.map(readAccountResourceConverter::toResource)
						.get(),
				HttpStatus.CREATED);
	}

	@ApiOperation(value = "Get an account", response = ReadAccount.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public HttpEntity<Resource<ReadAccount>> get(@PathVariable("id") Long id) {
		return new ResponseEntity<>(
				accountRepository.find(id)
						.map(new ReadAccountMapper())
						.map(readAccountResourceConverter::toResource)
						.get(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "Delete an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Long id) {
		accountRepository.delete(id);
	}
}
