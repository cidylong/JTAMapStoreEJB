# Payara-6 EJB module with Hazelcast Customer MapStore and JTA PU

## What is it?

This project provides a simple EJB application for Jakarta EE developers to start a Jakarta EE 10 project with Hazelcast cache mechanism.

It contains a collection of simple examples to demonstrate the Jakarta EE specifications, Hazelcast caching and backend database example configuration for continuous integration and continuous deployment purpose.

## Docs

Read the online docs: 

## Development guide

1. Create database table by run [ozssc_event_table.sql] under sql directory in resources.
2. Load PostgrSQL jdbc driver as Lib into Payara-6 domain.
3. Create datasource to be accessed over jdbc on payara-6.2023.10 domain server:
   1. create connection pool by run [$ADMIN_COMMAND --passwordfile $PW_FILE create-jdbc-connection-pool --datasourceclassname $XA_DS --restype javax.sql.XADataSource --property url="jdbc\:postgresql\://$HOST\:$PORT/ozssc":user="tomcat":password="******":host="$HOST":port=$PORT:database="ozssc" --steadypoolsize 64 --maxpoolsize 128 $OZSSC_CP] 
   2. create JDBC datasource by run [$ADMIN_COMMAND --passwordfile $PW_FILE create-jdbc-resource --connectionpoolid $OZSSC_CP --enabled true $OZSSC_JDBC_RSC]
   3. create resource reference by run [$ADMIN_COMMAND --passwordfile $PW_FILE create-resource-ref --target server $OZSSC_JDBC_RSC]
3. Verify it from Admin_UI (http://rocky9thss1:4848), found jndi name as: jdbc/PG15_ozssc_150
4. Create persistence.xml under resources/META-INF to configure JTA Persistence Unit
5. Create entity class ([Event.Java]) to store and to be accessed by frontage, create DataSerializableFactory [EventDataSerializableFactory.java] to allow payara to process serialize and deserialize,create customer MapStore [EventMapStore.java] under module [JTALibJar] to let Hazelcast to load and store entity to backend database.
6. Create a simple Singleton Enterprise java bean (Scheduler.java) under module JTASchedulerEJB to hold a scheduler to access Hazelcast map
7. Compile and deploy it to Payara Domain
   1. Deploy JTALibJar as common lib by add-library CLI
   2. Deploy JTASchedulerEJB as ejb by using deploy in CLI
8. Re-configure hazelcast-config.xml by add event map definition and serializable section
9. upload hazelcast-config.xml to $PAYARA_DOMAIN/config directory and restart domain
10. Check console log and database to verify it

## R&D environments

1. Operating system: Rocky Linux 9.2
2. Oracle JDK 17.0.7
3. PostgreSQL 15
4. Payara Community 6.2023.10 (jakarta EE 10 and Hazelcast 5.3)

## Reference

* [Jakarta EE 10 Sandbox](https://github.com/hantsy/jakartaee10-sandbox)
* [Jakarta EE 10 Specifications](https://jakarta.ee/specifications/platform/10/)