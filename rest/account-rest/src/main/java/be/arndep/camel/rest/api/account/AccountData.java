package be.arndep.camel.rest.api.account;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Created by arnaud on 16/01/15.
 */
@ApiModel(subTypes = {CreateAccount.class, ReadAccount.class, UpdateAccount.class})
public interface AccountData {
	Double getBalance();
}
