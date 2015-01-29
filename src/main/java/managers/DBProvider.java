package main.java.managers;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
@DataSourceDefinition(
        className = "org.postgresql.ds.PGSimpleDataSource",
        name = "java:global/jdbc/Betty",
        user = "postgres",
        password = "postgres",
        databaseName = "Betty",
        properties = {"connectionAttributes=;create=true"}
)
public class DBProvider {
    //объект для запуска примера не в контейнере
}
