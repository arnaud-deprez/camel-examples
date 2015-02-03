package be.arndep.camel.rest.config;

import be.arndep.camel.rest.internal.service.AccountService;
import be.arndep.camel.rest.internal.data.AccountMemoryRepositoryImpl;
import be.arndep.camel.rest.internal.domain.account.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by arnaud on 31.12.14.
 */
@Configuration
public class BeanConfiguration {
    
    @Bean
    public AccountService accountService() {
        return new AccountService(accountRepository());
    }
    
    @Bean
    public AccountRepository accountRepository() {
        return new AccountMemoryRepositoryImpl();
    }
}
