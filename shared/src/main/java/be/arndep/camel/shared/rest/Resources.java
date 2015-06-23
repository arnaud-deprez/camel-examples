package be.arndep.camel.shared.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by arnaud on 02/07/15.
 */
public final class Resources<T> extends ResourceSupport {
	private final Collection<T> content;

	protected Resources() {
		this(new ArrayList<>(), new ArrayList<>());
	}

	public Resources(Iterable<T> content, Iterable<Link> links) {
		super(links);
		this.content = new ArrayList<>();
		content.forEach(t -> this.content.add(t));
	}

	public Resources(Iterable<T> content, Link... links) {
		this(content, Arrays.asList(links));
	}

	public Collection<T> getContent() {
		return Collections.unmodifiableCollection(content);
	}
}
