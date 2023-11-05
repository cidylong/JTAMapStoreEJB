package com.longz.thss.test.jtamapstoreejb.store;

import jakarta.annotation.Resource;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Singleton;
import jakarta.enterprise.inject.Produces;

import javax.sql.XADataSource;

@Singleton(name = "thssXADataSourceProducer")
@DataSourceDefinition(
        name = "java:global/jdbc/xaDataSource",
        className = "org.postgresql.xa.PGXADataSource",
        user = "tomcat",
        password = "tacmot",
        serverName = "10.0.1.112",
        portNumber = 5432,
        databaseName = "ozssc",
        minPoolSize = 10,
        maxPoolSize = 50)
public class ThssXADataSourceProducer {
    @Resource(lookup="java:global/jdbc/xaDataSource")
    XADataSource ds;
    @Produces
    public XADataSource getDatasource() {
        return ds;
    }
}
