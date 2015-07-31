#Camel REST examples with HATEOAS

//TODO: create bundle account-repository

##account-rest

This is an OSGi bundle that can be installed on Karaf.

###Installation on Karaf 2

```sh
features:chooseUrl camel 2.15.2
features:addurl mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
features:install -v account-rest
```

###Installation on Karaf 3 & 4

Go the subshell feature:

```sh
feature
```

And then, run the following commands:

```sh
repo-add camel 2.15.2
repo-add mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
install -v account-rest
```

If your are not using the subshell, you can prefix all the previous commands with 'feature:'.

###Play

Once it's installed, you can go to [http://localhost:8181/bankaccounts](http://localhost:8181/bankaccounts) with your favourite browser.

##account-spring-boot

This is a spring-boot application that runs the account-rest example.

###Run the application

```sh
mvn spring-boot:run
```

Then go to [http://localhost:8080](http://localhost:8080) with your favourite browser to get the Swagger doc !