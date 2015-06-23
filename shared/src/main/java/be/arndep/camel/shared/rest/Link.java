package be.arndep.camel.shared.rest;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by arnaud on 15/02/15.
 */
public final class Link {
	public static final String SELF_REL = "self";

	private final String rel;
	private final String href;
	private final boolean templated;
	private final Collection<String> types;
	private final Collection<String> methods;

	private Link(final Builder builder) {
		rel = builder.rel;
		href = builder.href;
		types = builder.types.isEmpty() ? null : builder.types;
		methods = builder.methods.isEmpty() ? null : builder.methods;
		templated = builder.templated;
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

	public boolean isTemplated() {
		return templated;
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
		private boolean templated;

		private Builder() {
			types = new ArrayList<>();
			methods = new ArrayList<>();
			templated = false;
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

		public Builder templated(boolean templated) {
			this.templated = templated;
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
