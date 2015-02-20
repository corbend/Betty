package main.java.managers;

import main.java.managers.users.UserEJB;
import main.java.models.users.User;

import javax.annotation.PostConstruct;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

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

    @EJB
    private UserEJB userEJB;

    private static final String superAdminName = "supervisor";

//    private void createDefaultAdmin() {
//
//        User superAdmin = userEJB.getUserByName(superAdminName);
//
//        if (superAdmin == null) {
//            userEJB.addNewUser(superAdminName, "admin", "domain@well.com", "75555555555");
//        }
//    }

    @PostConstruct
    public void initialize() {
        //createDefaultAdmin();
    }
}
