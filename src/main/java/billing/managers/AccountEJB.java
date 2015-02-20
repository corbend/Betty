package main.java.billing.managers;

import main.java.billing.models.Account;
import main.java.billing.models.Person;
import main.java.managers.messages.AccountMessage;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Stateless
@Local(AccountLocal.class)
public class AccountEJB {

    private class NotEnoughFundsException extends Exception {

    }

    @PersistenceContext
    protected EntityManager em;

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    //@JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/AccountTopic")
    private Destination accountQueue;

    @EJB
    private TransactionEJB transactionEJB;

    public List<Account> getAccount() {
        return em.createNamedQuery("FIND_ALL", Account.class).getResultList();
    }

    public void incrementBalance(Long accountId, Double amount) throws JMSException {

        Account acc = em.find(Account.class, accountId);

        Double oldAmount = acc.getAmount();
        Double newAmount = oldAmount + amount;

        acc.setAmount(newAmount);

        transactionEJB.transferToService(acc, amount);
        //в контексте, посылаем сообщение для возможности создавать ставки
        ObjectMessage msg = context.createObjectMessage(AccountMessage.class);
        msg.setStringProperty("action", "ACCOUNT_INC_OK");
        context.createProducer().send(accountQueue, msg);

        em.merge(acc);
    }

    public void decrementBalance(Long accountId, Double amount) throws NotEnoughFundsException, JMSException {

        Account acc = em.find(Account.class, accountId);

        Double oldAmount = acc.getAmount();

        Double newAmount = oldAmount - amount;

        if (newAmount < 0) {
            throw new NotEnoughFundsException();
        }

        acc.setAmount(newAmount);

        transactionEJB.transferToService(acc, amount);
        //в контексте, посылаем сообщение для возможности создавать ставки
        ObjectMessage msg = context.createObjectMessage(AccountMessage.class);
        msg.setStringProperty("action", "ACCOUNT_DEC_OK");
        context.createProducer().send(accountQueue, msg);

        em.merge(acc);
    }

    public void createAccount(Person p) {

        Account acc = new Account();
        acc.setPerson(p);
        em.persist(acc);
    }

    public void deleteAccount(Long id) {
        Account acc = em.find(Account.class, id);
        em.remove(acc);
    }

    public void updateAccount(Long id) {
        Account acc = em.find(Account.class, id);
        em.merge(acc);
    }
}