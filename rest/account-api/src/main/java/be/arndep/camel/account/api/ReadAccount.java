package be.arndep.camel.account.api;

import java.time.LocalDateTime;

/**
 * Created by arnaud on 23/06/15.
 */
public interface ReadAccount extends AccountData {
	Long getId();
	LocalDateTime getOpenedDate();
	LocalDateTime getClosedDate();
	Double getBalance();
	Boolean isWithdrawAble();
}
