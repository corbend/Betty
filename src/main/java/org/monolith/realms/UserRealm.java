package main.java.org.monolith.realms;

//import com.sun.appserv.security.AppservRealm;
//import com.sun.enterprise.security.auth.realm.BadRealmException;
//import com.sun.enterprise.security.auth.realm.InvalidOperationException;
//import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
//import com.sun.enterprise.security.auth.realm.NoSuchUserException;
//import main.java.managers.users.SecurityManager;
import main.java.stores.SecurityJAASStore;

import java.util.Enumeration;
import java.util.Properties;

//public class UserRealm extends AppservRealm {
//
//    private String jaasCtxName;
//    private String dataSource;
//
//    private static final String REALM_NAME = "userRealm";
//    private static final String REALM_DATASOURCE = "jdbc/Betty";
//    /**
//     * Init realm from properties
//     *
//     * @param props
//     * @throws BadRealmException
//     * @throws NoSuchRealmException
//     */
//    @Override
//    protected void init(Properties props) throws BadRealmException, NoSuchRealmException {
//        _logger.fine("init()");
//        jaasCtxName = props.getProperty("jaas-context", REALM_NAME);
//        dataSource = props.getProperty("dataSource", REALM_DATASOURCE);
//    }
//
//    @Override
//    public String getJAASContext() {
//        return jaasCtxName;
//    }
//
//    @Override
//    public String getAuthType() {
//        return "High Security UserRealm";
//    }
//
//    /**
//     * Authenticates a user against GlassFish
//     *
//     * @param uid The User ID
//     * @param givenPwd The Password to check
//     * @return String[] of the groups a user belongs to.
//     * @throws Exception
//     */
//    public String[] authenticate(String name, String givenPwd) throws Exception {
//
//        SecurityJAASStore store = new SecurityJAASStore(dataSource);
//
//        String salt = store.getSaltForUser(name);
//
//        String[] result = null;
//        String checkPwd = SecurityManager.cryptPassword(givenPwd, salt).get(0);
//
//        if (checkPwd == givenPwd) {
//            result[0] = "Valid User";
//        }
//
//        return result;
//    }
//
//    /**
//     * {@inheritDoc }
//     */
//    @Override
//    public Enumeration getGroupNames(String string) throws InvalidOperationException, NoSuchUserException {
//        //never called. Only here to make compiler happy.
//        return null;
//    }
//}

