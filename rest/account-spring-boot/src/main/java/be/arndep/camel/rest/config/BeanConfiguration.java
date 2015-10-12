package be.arndep.camel.rest.config;

import be.arndep.camel.account.core.domain.BankAccountRepository;
import be.arndep.camel.account.core.repositories.impl.BankAccountMemoryRepositoryImpl;
import be.arndep.camel.account.core.services.BankAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by arnaud on 31.12.14.
 */
@Configuration
public class BeanConfiguration {
    
    @Bean
    public BankAccountService bankAccountService() {
        return new BankAccountService(bankAccountRepository());
    }
    
    @Bean
    public BankAccountRepository bankAccountRepository() {
        return new BankAccountMemoryRepositoryImpl();
    }
}
