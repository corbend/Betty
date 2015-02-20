package main.java.managers.users.services.interfaces;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Crypter {

    public List<String> crypt(String password, String salt) throws NoSuchAlgorithmException;

}
