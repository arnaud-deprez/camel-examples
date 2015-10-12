package be.arndep.camel.shared.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by arnaud on 02/07/15.
 */
public abstract class ResourceSupport {
	protected final Collection<Link> links;

	protected ResourceSupport() {
		this(new ArrayList<>());
	}

	protected ResourceSupport(Iterable<Link> links) {
		this.links = new ArrayList<>();
		links.forEach(l -> this.links.add(l));
	}

	protected ResourceSupport(Link... links) {
		this(Arrays.asList(links));
	}

	public Collection<Link> getLinks() {
		return Collections.unmodifiableCollection(links);
	}
}
