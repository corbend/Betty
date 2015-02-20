package main.java.managers.users;

import main.java.managers.users.services.annotations.Sha256Crypt;
import main.java.managers.users.services.interfaces.Crypter;
import main.java.managers.users.services.interfaces.SecurityManager;
import main.java.models.users.User;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;

@Default
public class DefaultSecurityManager implements SecurityManager {

    @Inject @Sha256Crypt
    private Crypter crypter;

    public boolean checkPassword(String password, User usr) throws NoSuchAlgorithmException {

        boolean result = false;
        String passwordHash = usr.getPasswordHash();
        String passwordSalt = usr.getPasswordSalt();

        String hash = crypter.crypt(password, passwordSalt).get(0);

        if (hash == passwordHash) {
            result = true;
        }

        return result;
    }

}
