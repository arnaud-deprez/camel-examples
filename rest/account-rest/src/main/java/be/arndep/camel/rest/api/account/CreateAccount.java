package be.arndep.camel.rest.api.account;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Created by arnaud on 16/01/15.
 */
@ApiModel(parent = AccountData.class)
public interface CreateAccount extends AccountData {
	//Inheritance does not work with swagger
	Double getBalance();
}
