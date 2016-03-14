package be.arndep.camel.shared.builder;

/**
 * Created by arnaud on 22/03/15.
 */
public interface FluentBuilder<R> {
	R build();

	interface Self<B extends FluentBuilder<?>> {
		B self();
	}

	interface Copy<F, B extends FluentBuilder<F>> {
		B from(F from);
	}
}
