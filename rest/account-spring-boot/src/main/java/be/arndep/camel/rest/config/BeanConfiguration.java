package be.arndep.camel.rest.config;

import be.arndep.camel.account.api.AccountCommandService;
import be.arndep.camel.account.api.AccountQueryService;
import be.arndep.camel.account.impl.domain.BankAccountRepository;
import be.arndep.camel.account.impl.repositories.impl.BankAccountMemoryRepositoryImpl;
import be.arndep.camel.account.impl.services.BankAccountServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by arnaud on 31.12.14.
 */
@Configuration
public class BeanConfiguration {

    private BankAccountRepository accountRepository = new BankAccountMemoryRepositoryImpl();
    private BankAccountServiceImpl accountService = new BankAccountServiceImpl(accountRepository);
    
    @Bean
    public BankAccountRepository bankAccountRepository() {
        return accountRepository;
    }

    @Bean
    public AccountCommandService accountCommandService() {
        return accountService;
    }

    @Bean
    public AccountQueryService accountQueryService() {
        return accountService;
    }
}
