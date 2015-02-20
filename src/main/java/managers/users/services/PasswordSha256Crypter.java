package main.java.managers.users.services;

import main.java.managers.users.services.annotations.Sha256Crypt;
import main.java.managers.users.services.interfaces.Crypter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Sha256Crypt
public class PasswordSha256Crypter implements Crypter {

    public List<String> crypt(String password, String salt) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<byteData.length;i++) {
            String hex=Integer.toHexString(0xff & byteData[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }

        List<String> securities = new ArrayList(2);
        securities.add(0, hexString.toString());
        securities.add(1, "");

        return securities;
    }
}
