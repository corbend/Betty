package main.java.billing.managers;

import main.java.billing.models.Account;
import main.java.billing.models.Transaction;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TransactionEJB {

    @PersistenceContext
    EntityManager em;

    public boolean transfer(Account src, Account dest) {
        return true;
    }

    public boolean transferToService(Account src, Double amount) {
        //передача на сервисный аккаунт
        if (!src.isLocked()) {

            Transaction tr = new Transaction();
            tr.setAmount(amount);

            em.persist(tr);
        }

        return true;
    }

    public boolean approve(Long id) {
        return true;
    }


}
