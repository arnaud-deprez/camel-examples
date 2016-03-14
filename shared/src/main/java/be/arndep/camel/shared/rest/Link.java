package be.arndep.camel.shared.rest;

import be.arndep.camel.shared.builder.FluentBuilder;
import be.arndep.camel.shared.immutables.HideImplementation;
import org.immutables.value.Value;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by arnaud on 15/02/15.
 */
@Value.Immutable
@HideImplementation
public abstract class Link {
	public static final String SELF_REL = "self";

	@Value.Parameter
	public abstract String getRel();
	@Value.Parameter
	public abstract String getHref();

	@Value.Default
	public Boolean getTemplated() {
		return false;
	}
	@Value.Default
	public List<String> getTypes() {
		return Collections.emptyList();
	}
	@Value.Default
	public List<String> getMethods() {
		return Collections.emptyList();
	}

	public static Link of(String rel, String href) {
		return ImmutableLink.of(rel, href);
	}

	public static Builder builder() {
		return ImmutableLink.builder();
	}

	public interface Builder extends FluentBuilder<Link>, FluentBuilder.Copy<Link, Builder> {
		Builder rel(String rel);
		Builder href(String href);

		default Builder href(final String href, final Object... objects) {
			Objects.requireNonNull(objects);
			if (objects.length <= 0) {
				throw new IllegalArgumentException("objects must contain at least one element");
			}
			return this.href(MessageFormat.format(href, objects));
		}

		Builder templated(Boolean templated);
		Builder types(List<String> types);
		Builder methods(List<String> methods);

		default Builder types(String... types) {
			return types(Arrays.asList(types));
		}

		default Builder methods(String... methods) {
			return methods(Arrays.asList(methods));
		}
	}
}
