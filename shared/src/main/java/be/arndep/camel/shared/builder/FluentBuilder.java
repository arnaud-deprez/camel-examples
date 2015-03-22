package be.arndep.camel.shared.builder;

/**
 * Created by arnaud on 22/03/15.
 */
public interface FluentBuilder<B extends FluentBuilder<B, R>, R> {
	B self();
	R build();
}
