package be.arndep.camel.rest.account.internal.mapper;

import be.arndep.camel.rest.account.api.ReadAccount;
import be.arndep.camel.rest.account.api.impl.ReadAccountImpl;
import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.shared.mapper.Mapper;
import be.arndep.camel.shared.rest.Link;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by arnaud on 22/03/15.
 */
public class ReadAccountMapper implements Mapper<Account, ReadAccount> {
	@Override
	public ReadAccount map(final Account a) {
		Collection<Link> links = new ArrayList<>();
		links.add(Link.newBuilder().href("/accounts/{0}").rel(Link.SELF_REL).withTypes(MediaType.APPLICATION_JSON).withMethods("GET", "PUT", "DELETE").build(a.getId()));
		links.add(Link.newBuilder().href("/accounts/{0}/deposit").rel("deposit").withTypes(MediaType.APPLICATION_JSON).withMethods("POST").build(a.getId()));
		if (a.isPositive()) {
			links.add(Link.newBuilder().href("/accounts/{0}/withdraw").rel("withdraw").withTypes(MediaType.APPLICATION_JSON).withMethods("POST").build(a.getId()));
			links.add(Link.newBuilder().href("/accounts/{0}/transfer").rel("transfer").withTypes(MediaType.APPLICATION_JSON).withMethods("POST").build(a.getId()));
		}
		links.add(Link.newBuilder().href("/accounts").rel("collection").withTypes(MediaType.APPLICATION_JSON).withMethods("GET", "POST").build(a.getId()));
		return new ReadAccountImpl(a, links);
	}
}
