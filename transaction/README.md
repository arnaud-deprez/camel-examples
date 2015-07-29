XA transaction example
======================

##Installation

###Installation on Karaf 2

```sh
features:chooseUrl pax-jdbc 0.6.0
features:chooseUrl camel 2.15.2
features:chooseUrl activemq 5.11.1
features:addurl mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
```

###Example 1: XA transaction jms -> sql

Run an example with ActiveMQ and SQL camel component.

```sh
features:install -v xa-jms-sql-blueprint
```

###Example 2: XA transaction jms -> jpa

Run an example with ActiveMQ and JPA.

```sh
features:install -v xa-jms-jpa-blueprint
```

####Hawtio

```sh
features:chooseurl hawtio <version>
features:install hawtio
```

###Installation on Karaf 3 & 4

Go the subshell feature:

```sh
feature
```

And then, run the following commands: 

```sh
repo-add pax-jdbc 0.6.0
repo-add camel 2.15.2
repo-add activemq 5.11.1
repo-add mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
install -v xa-jms-sql-blueprint
```

If your are not using the subshell, you can prefix all the previous commands with 'feature:'.

####Example 1: XA transaction jms -> sql

Run an example with ActiveMQ and SQL camel component.

```sh
install -v xa-jms-sql-blueprint
```

####Example 2: XA transaction jms -> jpa

Run an example with ActiveMQ and JPA.

```sh
install -v xa-jms-jpa-blueprint
```

####Hawtio

In the subshell feature, execute the following commands:

```sh
repo-add hawtio <version>
install hawtio
```

##Play with the sample (Karaf 2 & 3 & 4)

Then you can connect to the ActiveMQ web console [http://localhost:8181/activemqweb](http://localhost:8181/activemqweb) 
or the hawtio console [http://localhost:8181/hawtio](http://localhost:8181/hawtio) with the user karaf and password karaf