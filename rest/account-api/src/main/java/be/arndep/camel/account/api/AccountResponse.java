package be.arndep.camel.account.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by arnaud on 23/06/15.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAccountResponse.class)
@JsonDeserialize(as = ImmutableAccountResponse.class)
public interface AccountResponse extends AccountData {
	Long getId();
	LocalDateTime getOpenedDate();
	Optional<LocalDateTime> getClosedDate();
	Double getBalance();
	Boolean isWithdrawAble();

	static Builder builder() {
		return ImmutableAccountResponse.builder();
	}

	interface Builder extends AccountData.Builder<Builder, AccountResponse> {
		Builder id(Long id);
		Builder openedDate(LocalDateTime openedDate);
		Builder closedDate(LocalDateTime closedDate);
		Builder closedDate(Optional<LocalDateTime> closedDate);
		Builder balance(Double balance);
		Builder withdrawAble(Boolean withDrawable);
	}
}
