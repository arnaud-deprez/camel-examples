package be.arndep.camel.shared.rest;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by arnaud on 02/07/15.
 */
public final class Resource<T> extends ResourceSupport {
	@JsonUnwrapped
	private final T content;

	private Resource() {
		this(null, new ArrayList<>());
	}

	private Resource(T content, Iterable<Link> links) {
		super(links);
		this.content = content;
	}

	private Resource(T content, Link... links) {
		this(content, Arrays.asList(links));
	}

	public T getContent() {
		return content;
	}

	public static <T> Resource<T> of(T content, Iterable<Link> links) {
		return new Resource<>(content, links);
	}

	public static <T> Resource<T> of(T content, Link... links) {
		return new Resource<>(content, links);
	}
}