package be.arndep.camel.shared.jackson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author arnaud.deprez
 * @since 4/04/16
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTestDataObject.class)
@JsonDeserialize(as = ImmutableTestDataObject.class)
public interface TestDataObject {
	String getString();
	LocalDateTime getDate();
	Optional<String> getOptString();
}
