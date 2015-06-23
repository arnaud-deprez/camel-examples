package be.arndep.camel.account.core.rest.converter;

import be.arndep.camel.account.api.ReadAccount;
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
public class ReadAccountResourceConverter {
	public Resource<ReadAccount> toResource(ReadAccount a) {
		Collection<Link> links = new ArrayList<>();

		links.add(
				Link.newBuilder()
						.href("/bankaccounts/{0}")
						.rel(Link.SELF_REL)
						.withTypes(MediaType.APPLICATION_JSON)
						.withMethods("GET", "PUT", "DELETE")
						.build(a.getId()));
		links.add(
				Link.newBuilder()
						.href("/bankaccounts/{0}/deposit")
						.rel("deposit")
						.withTypes(MediaType.APPLICATION_JSON)
						.withMethods("POST")
						.build(a.getId()));

		if (a.isWithdrawAble()) {
			links.add(
					Link.newBuilder()
							.href("/bankaccounts/{0}/withdraw")
							.rel("withdraw")
							.withTypes(MediaType.APPLICATION_JSON)
							.withMethods("POST")
							.build(a.getId()));
			links.add(
					Link.newBuilder()
							.href("/bankaccounts/{0}/transfer/'{'to'}'")
							.rel("transfer")
							.withTypes(MediaType.APPLICATION_JSON)
							.withMethods("POST")
							.templated(true)
							.build(a.getId()));
		}

		links.add(
				Link.newBuilder()
						.href("/bankaccounts")
						.rel("collection")
						.withTypes(MediaType.APPLICATION_JSON)
						.withMethods("GET", "POST")
						.build(a.getId()));

		return new Resource<>(a, links);
	}

	public Resources<Resource<ReadAccount>> toResources(Iterable<ReadAccount> readAccounts) {
		List<Resource<ReadAccount>> accountsResource = StreamSupport.stream(readAccounts.spliterator(), false)
				.map(this::toResource)
				.collect(Collectors.toList());

		Resources<Resource<ReadAccount>> resources = new Resources<>(accountsResource, Link.newBuilder()
				.href("/bankaccounts")
				.rel(Link.SELF_REL)
				.withTypes(MediaType.APPLICATION_JSON)
				.withMethods("GET", "POST")
				.build());

		return resources;
	}
}
