package be.arndep.camel.shared.mapper;

import java.util.function.Function;

/**
 * Created by arnaud on 22/03/15.
 */
@FunctionalInterface
public interface Mapper<T, R> extends Function<T, R> {
	@Override
	default R apply(T t) {
		return map(t);
	}

	R map(T t);
}
