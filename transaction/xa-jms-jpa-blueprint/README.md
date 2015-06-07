xa-jms-sql-blueprint
====================

//TODO: make tests

##Installation

###Installation on Karaf 2

```sh
features:addurl mvn:org.ops4j.pax.jdbc/pax-jdbc-features/0.5.0/xml/features
features:chooseUrl camel 2.15.2
features:chooseUrl activemq 5.11.1
features:addurl mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
features:install -v xa-jms-jpa-blueprint
```

####Hawtio

```sh
features:chooseurl hawtio <version>
features:install hawtio
```

###Installation on Karaf 3

Go the subshell feature:

```sh
feature
```

And then, run the following commands: 

```sh
repo-add mvn:org.ops4j.pax.jdbc/pax-jdbc-features/0.5.0/xml/features
repo-add camel 2.15.2
repo-add activemq 5.11.1
repo-add mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
install -v xa-jms-jpa-blueprint
```

If your are not using the subshell, you can prefix all the previous commands with 'feature:'.

####Hawtio

In the subshell feature, execute the following commands:

```sh
repo-add hawtio <version>
install hawtio
```

##Play with the sample (Karaf 2 & 3)

Then you can connect to the ActiveMQ web console [http://localhost:8181/activemqweb](http://localhost:8181/activemqweb) 
or the hawtio console [http://localhost:8181/hawtio](http://localhost:8181/hawtio) with the user karaf and password karaf