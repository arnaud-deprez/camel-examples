package be.arndep.camel.rest.spring;

import be.arndep.camel.rest.api.account.AccountService;
import be.arndep.camel.rest.api.account.CreateAccount;
import be.arndep.camel.rest.api.account.ReadAccount;
import be.arndep.camel.rest.api.account.UpdateAccount;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Created by arnaud on 20/01/15.
 */
@Api(value = "accounts", description = "Accounts in spring")
@RestController
@RequestMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class AccountController {
	private final AccountService accountService;

	@Autowired
	public AccountController(final AccountService accountService) {
		this.accountService = accountService;
	}

	@ApiOperation(value = "Get all accounts", response = ReadAccount.class, responseContainer = "List")
	@RequestMapping(method = RequestMethod.GET)
	public HttpEntity<Resources<ReadAccount>> findAll(@RequestParam(value = "page", required = false, defaultValue = "0") Long page,
													  @RequestParam(value = "limit", required = false, defaultValue = "20") Long limit) {
		return new ResponseEntity<>(accountService.findAll(page, limit), HttpStatus.OK);
	}

	@ApiOperation(value = "Create an account")
	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<ReadAccount> create(CreateAccount account) {
		return new ResponseEntity<>(accountService.create(account), HttpStatus.CREATED);
	}

	@ApiOperation(value = "Get an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public HttpEntity<ReadAccount> get(@PathVariable("id") Long id) {
		return new ResponseEntity<>(accountService.get(id), HttpStatus.OK);
	}

	@ApiOperation(value = "Update an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public HttpEntity<ReadAccount> update(@PathVariable("id") Long id, UpdateAccount account) {
		return new ResponseEntity<>(accountService.update(id, account), HttpStatus.OK);
	}

	@ApiOperation(value = "Delete an account")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public HttpEntity<ReadAccount> delete(@PathVariable("id") Long id) {
		return new ResponseEntity<>(accountService.delete(id), HttpStatus.OK);
	}
}
