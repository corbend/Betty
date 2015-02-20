package main.java.managers.users;

import main.java.managers.users.services.annotations.Sha256Crypt;
import main.java.managers.users.services.interfaces.*;
import main.java.managers.users.services.interfaces.SecurityManager;
import main.java.models.users.User;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.inject.InjectionException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class UserEJB {

    private static Logger logger = Logger.getLogger(UserEJB.class.getName());

    @Inject @Sha256Crypt
    private Crypter crypter;

    @Inject
    private SecurityManager secureMgr;

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext context;

    public User getUser(Long id) {
        return em.find(User.class, id);
    }
    public User getUserByName(String name) {
        User usr = null;
        try {
            usr = em.createNamedQuery("User.findByLogin", User.class).setParameter("login", name).getSingleResult();
        } catch(NoResultException e) {
            e.printStackTrace();
        }

        return usr;
    }

    public User addNewUser(String login, String password, String email, String telephone) {
        logger.log(Level.FINE, "SAVE NEW USER->" + login + ", " + password + ", " + email + ", " + telephone);
        User usr = new User(login, email, telephone);

        if (crypter == null) {
            throw new InjectionException("Crypter injection error!");
        }

        if (password == null) {
            password = "admin";
        }

        try {
            List<String> securities = crypter.crypt(password, "");

            usr.setPasswordHash(securities.get(0));
            usr.setPasswordSalt(securities.get(1));
            em.persist(usr);
        } catch (NoSuchAlgorithmException e) {
            context.setRollbackOnly();
        }

        return usr;
    }

    public void authenticate(String login, String password) {

        User usr = em.createNamedQuery("User.findByLogin", User.class).setParameter("login", login).getSingleResult();

        try {
            secureMgr.checkPassword(password, usr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteUser(Long userId) {
        User usr = getUser(userId);
        em.remove(usr);

        return true;
    }
}
