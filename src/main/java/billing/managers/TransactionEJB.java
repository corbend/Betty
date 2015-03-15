package main.java.billing.managers;

import main.java.billing.models.Account;
import main.java.billing.models.Transaction;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.auth.login.AccountLockedException;
import java.util.List;

@Stateless
public class TransactionEJB {

    @EJB
    AccountEJB accountEJB;

    @PersistenceContext
    EntityManager em;

    private void manageInputTransaction(Account acc, Transaction transaction) throws AccountLockedException {

        if (!acc.isLocked()) {
            List<Transaction> trInList = acc.getInTransactions();
            trInList.add(transaction);

            em.merge(acc);
        } else {
            throw new AccountLockedException();
        }
    }

    private void manageOutputTransaction(Account acc, Transaction transaction) throws AccountLockedException {

        if (!acc.isLocked()) {
            List<Transaction> trOutList = acc.getOutTransactions();
            trOutList.add(transaction);

            em.merge(acc);
        } else {
            throw new AccountLockedException();
        }
    }

    public boolean createChangeTransaction(Account src, Account dest, Double amount) throws AccountLockedException {

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        if (src != null) {
            manageOutputTransaction(src, transaction);
        }

        if (dest != null) {
            manageInputTransaction(dest, transaction);
        }

        return true;
    }

    public boolean transferToService(Account src, Double amount) throws AccountEJB.ServiceAccountNotFound, AccountLockedException {
        //передача на сервисный аккаунт
        Account serviceAccount = accountEJB.getServiceAccount();
        createChangeTransaction(src, serviceAccount, amount);

        return true;
    }

    public boolean approve(Long id) {
        return true;
    }


}
