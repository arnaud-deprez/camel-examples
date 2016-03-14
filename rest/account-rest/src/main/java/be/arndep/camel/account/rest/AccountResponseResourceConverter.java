package be.arndep.camel.account.rest;

import be.arndep.camel.account.api.AccountResponse;
import be.arndep.camel.shared.rest.Link;
import be.arndep.camel.shared.rest.Resource;
import be.arndep.camel.shared.rest.Resources;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by arnaud on 02/07/15.
 */
public class AccountResponseResourceConverter {
	public Resource<AccountResponse> toResource(AccountResponse a) {
		Collection<Link> links = new ArrayList<>();

		links.add(
			Link.builder()
						.href("/bankaccounts/{0}", a.getId())
						.rel(Link.SELF_REL)
						.types(MediaType.APPLICATION_JSON)
						.methods("GET", "PUT", "DELETE")
						.build());
		links.add(
			Link.builder()
						.href("/bankaccounts/{0}/deposit", a.getId())
						.rel("deposit")
						.types(MediaType.APPLICATION_JSON)
						.methods("POST")
						.build());

		if (a.isWithdrawAble()) {
			links.add(
				Link.builder()
							.href("/bankaccounts/{0}/withdraw", a.getId())
							.rel("withdraw")
							.types(MediaType.APPLICATION_JSON)
							.methods("POST")
							.build());
			links.add(
				Link.builder()
							.href("/bankaccounts/{0}/transfer/'{'to'}'", a.getId())
							.rel("transfer")
							.types(MediaType.APPLICATION_JSON)
							.methods("POST")
							.templated(true)
							.build());
		}

		links.add(
			Link.builder()
						.href("/bankaccounts")
						.rel("collection")
						.types(MediaType.APPLICATION_JSON)
						.methods("GET", "POST")
						.build());

		return Resource.of(a, links);
	}

	public Resources<Resource<AccountResponse>> toResources(Iterable<AccountResponse> readAccounts) {
		List<Resource<AccountResponse>> accountsResource = StreamSupport.stream(readAccounts.spliterator(), false)
				.map(this::toResource)
				.collect(Collectors.toList());

		Resources<Resource<AccountResponse>> resources = Resources.of(accountsResource, Link.builder()
				.href("/bankaccounts")
				.rel(Link.SELF_REL)
				.types(MediaType.APPLICATION_JSON)
				.methods("GET", "POST")
				.build());

		return resources;
	}
}
