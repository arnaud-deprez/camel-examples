package be.arndep.camel.rest.account.api.impl;

import be.arndep.camel.rest.account.api.ReadAccount;
import be.arndep.camel.rest.account.api.ReadAccounts;
import be.arndep.camel.shared.rest.Link;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 03/02/15.
 */
public class ReadAccountsImpl implements ReadAccounts {
	private Collection<ReadAccount> content;
	private Collection<Link> links;

	public ReadAccountsImpl(final Collection<ReadAccount> content,
							final Link... links) {
		this.content = content;
		this.links = Arrays.stream(links).collect(Collectors.toList());
	}

	@Override
	public Collection<ReadAccount> getContent() {
		return content;
	}

	@Override
	public Collection<Link> getLinks() {
		return links;
	}
}
