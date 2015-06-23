package be.arndep.camel.shared.rest;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by arnaud on 02/07/15.
 */
public final class Resource<T> extends ResourceSupport {
	@JsonUnwrapped
	private final T content;

	protected Resource() {
		this(null, new ArrayList<>());
	}

	public Resource(T content, Iterable<Link> links) {
		super(links);
		this.content = content;
	}

	public Resource(T content, Link... links) {
		this(content, Arrays.asList(links));
	}

	public T getContent() {
		return content;
	}
}
