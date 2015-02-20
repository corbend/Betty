package main.java.billing.managers;

import main.java.billing.models.Account;

import java.util.List;

public interface AccountLocal {

    public List<Account> getAccount();
    public void incrementBalance();
    public void decrementBalance();

}
