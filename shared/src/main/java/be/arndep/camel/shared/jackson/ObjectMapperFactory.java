package be.arndep.camel.shared.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author arnaud.deprez
 * @since 31/03/16
 */
public final class ObjectMapperFactory {
	public static ObjectMapper newInstance() {
		return new ObjectMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
			.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
			.registerModules(new Jdk8Module(), new JavaTimeModule());
	}
}
