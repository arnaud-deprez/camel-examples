package be.arndep.camel.shared.immutables;

import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by arnaud.deprez on 11/03/16.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(
	get = { "get*", "is*" },
	visibility = Value.Style.ImplementationVisibility.PACKAGE,
	builderVisibility = Value.Style.BuilderVisibility.PACKAGE,
	defaultAsDefault = true
)
public @interface HideImplementation {
}
