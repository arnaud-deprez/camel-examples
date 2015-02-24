Account REST
============

##Installation

###Installation on Karaf 2

```sh
features:chooseUrl camel 2.14.1
features:install camel-blueprint camel-metrics camel-servlet camel-jackson camel-cxf
features:install spring-web
install -s mvn:com.fasterxml.jackson.module/jackson-module-mrbean/2.4.1
install -s mvn:com.wordnik/swagger-annotations/1.3.11
install -s mvn:be.arndep.camel/shared/1.0.0-SNAPSHOT
install -s mvn:be.arndep.camel/account-rest/1.0.0-SNAPSHOT
```

###Installation on Karaf 3

```sh
feature:repo-add camel 2.14.1
feature:install camel-blueprint camel-metrics camel-servlet camel-jackson camel-cxf
feature:install spring-web
install -s mvn:com.fasterxml.jackson.module/jackson-module-mrbean/2.4.1
install -s mvn:com.wordnik/swagger-annotations/1.3.11
install -s mvn:be.arndep.camel/shared/1.0.0-SNAPSHOT
install -s mvn:be.arndep.camel/account-rest/1.0.0-SNAPSHOT
```

##Play

Once it's installed, you can go to http://localhost:8181/camel/api/accounts with your favourite browser.