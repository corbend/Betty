package main.java.stores;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityJAASStore {

    private Connection con;
    private final static Logger LOGGER = Logger.getLogger(SecurityJAASStore.class.getName());
    //private final static String ADD_USER = "INSERT INTO users VALUES(?,?,?);";
    private final static String SALT_FOR_USER = "SELECT passwordSalt FROM users u WHERE login = ?;";
    private final static String VERIFY_USER = "SELECT login FROM users u WHERE login = ? AND password = ?;";
    /**
     * Public constructor for use with Java EE App-servers or Clients which have
     * access to an InitialContext. In this case a javax.sql.DataSource is
     * looked up with the Context.
     *
     * @param dataSource
     */
    public SecurityJAASStore(String dataSource) {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            DataSource ds = (javax.sql.DataSource) ctx.lookup(dataSource);
            con = ds.getConnection();
        } catch (NamingException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting connection!", e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOGGER.log(Level.SEVERE, "Error closing context!", e);
                }
            }
        }
    }

    public String getSaltForUser(String name) {
        String salt = null;
        try {
            PreparedStatement pstm = con.prepareStatement(SALT_FOR_USER);
            pstm.setString(1, name);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                salt = rs.getString(1);
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User not found!", ex);
        }

        return salt;
    }

    public boolean validateUser(String name, String password) {

        try {
            PreparedStatement pstm = con.prepareStatement(VERIFY_USER);
            pstm.setString(1, name);
            pstm.setString(2, password);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User validation failed!", ex);
        }
        return false;
    }
}
