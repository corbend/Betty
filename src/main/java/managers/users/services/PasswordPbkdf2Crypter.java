package main.java.managers.users.services;

import main.java.managers.users.services.annotations.Pbkdf2Crypt;
import main.java.managers.users.services.interfaces.Crypter;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.lang.model.type.ArrayType;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Pbkdf2Crypt
public class PasswordPbkdf2Crypter implements Crypter {

    private static final int ITERATIONS = 20000;
    private static final int KEY_LENGTH = 128;

    private static byte[] generateSalt() throws NoSuchAlgorithmException {

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }

    public List<String> crypt(String password, String salt) throws NoSuchAlgorithmException {

        List<String> securities = new ArrayList<>(2);

        char[] passwordChars = password.toCharArray();
        byte[] saltBytes;

        if (salt == null || salt.equals("")) {
            saltBytes = generateSalt();
        } else {
            saltBytes = salt.getBytes();
        }

        PBEKeySpec spec = new PBEKeySpec(
                passwordChars,
                saltBytes,
                ITERATIONS,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPassword = key.generateSecret(spec).getEncoded();
            String hexString = String.format("%x", new BigInteger(hashedPassword));

            securities.add(hexString);
            securities.add(String.format("%x", new BigInteger(saltBytes)));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return securities;
    }
}
