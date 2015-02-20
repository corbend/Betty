package main.java.managers.users.services.interfaces;

import main.java.models.users.User;

import java.security.NoSuchAlgorithmException;

public interface SecurityManager {
    public boolean checkPassword(String password, User usr) throws NoSuchAlgorithmException;
}
