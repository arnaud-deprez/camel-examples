package be.arndep.camel.shared.rest;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by arnaud on 15/02/15.
 */
public final class Link {
	private String rel;
	private String href;
	private Collection<String> types;
	private Collection<String> methods;

	private Link(final Builder builder) {
		rel = builder.rel;
		href = builder.href;
		types = builder.types.isEmpty() ? null : builder.types;
		methods = builder.methods.isEmpty() ? null : builder.methods;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getRel() {
		return rel;
	}

	public String getHref() {
		return href;
	}

	public Collection<String> getTypes() {
		return types;
	}

	public Collection<String> getMethods() {
		return methods;
	}


	public static final class Builder {
		private String rel;
		private String href;
		private Collection<String> types;
		private Collection<String> methods;

		private Builder() {
			types = new ArrayList<>();
			methods = new ArrayList<>();
		}

		public Builder rel(final String rel) {
			this.rel = rel;
			return this;
		}

		public Builder href(final String href) {
			this.href = href;
			return this;
		}

		public Builder withTypes(final String... types) {
			this.types.addAll(Arrays.asList(types));
			return this;
		}

		public Builder withMethods(final String... methods) {
			this.methods.addAll(Arrays.asList(methods));
			return this;
		}

		public Link build(final Object... objects) {
			if (objects != null && objects.length > 0) {
				href = MessageFormat.format(href, objects);
			}
			return new Link(this);
		}
	}
}
