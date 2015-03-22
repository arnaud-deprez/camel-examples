package be.arndep.camel.rest.account.api.impl;

import be.arndep.camel.rest.account.api.Transfer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by arnaud on 22/03/15.
 */
public class TransferImpl implements Transfer {
	private final Long to;
	private final Double amount;

	@JsonCreator
	public TransferImpl(
			@JsonProperty("to") final Long to,
			@JsonProperty("amount") final Double amount) {
		this.to = to;
		this.amount = amount;
	}

	@Override
	public Long getTo() {
		return to;
	}

	@Override
	public Double getAmount() {
		return amount;
	}
}
