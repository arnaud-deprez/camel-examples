package be.arndep.camel.account.impl.services

import be.arndep.camel.account.api.AccountQueryService
import be.arndep.camel.account.api.AccountResponse
import be.arndep.camel.account.api.AccountType
import be.arndep.camel.account.impl.domain.BankAccountRepository
import be.arndep.camel.account.api.exceptions.BankAccountException
import spock.lang.Specification
import spock.lang.Unroll

import static be.arndep.camel.account.impl.BankAccountUtils.*
/**
 * Created by arnaud.deprez on 14/03/16.
 */
class BankAccountQueryServiceTest extends Specification {
    BankAccountRepository repository
    AccountQueryService service

    def setup() {
        repository = Mock()
        service = new BankAccountServiceImpl(repository)
    }

    @Unroll
    def "Call to findAll should return the list of accounts"() {
        given: "A mock result"
        def page = Optional.empty()
        def mockResult = createRandomListOfBankAccount(limit)

        when: "We call findAll"
        def result = service.findAll(page, limit)

        then: "A call to findAll with the same $page and $limit is done on the $repository"
        1 * repository.findAll(page, limit) >> mockResult
        0 * _
        and: "The $result should be based on the $mockResult"
        assert result.size() == limit.orElse(20).toInteger()
        assert result.size() == mockResult.size()
        result.eachWithIndex { AccountResponse r, int i ->
            assert r.id == mockResult[i].id
            assert r.owner == mockResult[i].owner
            assert r.accountType == mockResult[i].accountType
            assert r.balance == mockResult[i].balance
            assert r.openedDate == mockResult[i].openedDate
            assert r.closedDate == mockResult[i].closedDate
        }

        where:
        limit << [Optional.empty(), Optional.of(5)]
    }

    @Unroll
    def "Call to find with a known id should"() {
        given:
        def id = 1L
        def mockResult = createBankAccount(id, "owner$id", id, AccountType.BLUE)

        when: "We call findAll"
        def result = service.find(id)

        then: "A call to find with the same $id is done on the $repository"
        1 * repository.find(id) >> Optional.of(mockResult)
        0 * _
        and: "The $result should be based on the $mockResult"
        assert result.id == mockResult.id
        assert result.owner == mockResult.owner
        assert result.accountType == mockResult.accountType
        assert result.balance == mockResult.balance
        assert result.openedDate == mockResult.openedDate
        assert result.closedDate == mockResult.closedDate
    }

    @Unroll
    def "Call to find with an unknown id should"() {
        given:
        def id = 1L

        when: "We call find with $id"
        service.find(id)

        then: "A call to find with the same $id is done on the $repository"
        1 * repository.find(id) >> Optional.empty()
        0 * _
        and: "A BankAccountNotFoundException is thrown"
        thrown(BankAccountException)
    }
}