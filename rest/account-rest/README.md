Account REST
============

##Installation

###Installation on Karaf 2

```sh
features:chooseUrl camel 2.15.2
features:addurl mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
features:install -v -c account-rest
```

###Installation on Karaf 3

Go the subshell feature:

```sh
feature
```

And then, run the following commands:

```sh
repo-add camel 2.15.2
repo-add mvn:be.arndep.camel/features/1.0.0-SNAPSHOT/xml/features
install -v -c account-rest
```

If your are not using the subshell, you can prefix all the previous commands with 'feature:'.

##Play

Once it's installed, you can go to [http://localhost:8181/camel/api/accounts](http://localhost:8181/camel/api/accounts) with your favourite browser.