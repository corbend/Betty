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
import java.util.Date;
import java.util.List;


@Stateless
//@Local(AccountLocal.class)
public class AccountEJB {

    private class NotEnoughFundsException extends Exception {

    }

    @PersistenceContext
    protected EntityManager em;

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/AccountActionQueue")
    private Queue accountQueue;

    @EJB
    private TransactionEJB transactionEJB;

    public List<Account> getAccounts() {
        return em.createNamedQuery("FIND_ALL", Account.class).getResultList();
    }

    public Account getAccount(Long accountId) {
        return em.find(Account.class, accountId);
    }

    public void incrementBalance(Long accountId, Double amount) throws JMSException {

        Account acc = em.find(Account.class, accountId);

        Double oldAmount = acc.getTotalAmount();
        Double newAmount = oldAmount + amount;

        acc.getTotalAmount(newAmount);

        transactionEJB.transferToService(acc, amount);
        //в контексте, посылаем сообщение для возможности создавать ставки
        ObjectMessage msg = context.createObjectMessage(AccountMessage.class);
        msg.setStringProperty("action", "ACCOUNT_INC_OK");
        context.createProducer().send(accountQueue, msg);

        em.merge(acc);
    }

    public void decrementBalance(Long accountId, Double amount) throws NotEnoughFundsException, JMSException {

        Account acc = em.find(Account.class, accountId);

        Double oldAmount = acc.getTotalAmount();

        Double newAmount = oldAmount - amount;

        if (newAmount < 0) {
            throw new NotEnoughFundsException();
        }

        acc.getTotalAmount(newAmount);

        transactionEJB.transferToService(acc, amount);
        //в контексте, посылаем сообщение для возможности создавать ставки
        ObjectMessage msg = context.createObjectMessage(AccountMessage.class);
        msg.setStringProperty("action", "ACCOUNT_DEC_OK");
        context.createProducer().send(accountQueue, msg);

        em.merge(acc);
    }

    public void createAccount(Person p) {

        Account acc = new Account();
        acc.setStatusString("active");
        acc.setPerson(p);
        acc.setCreatedDate(new Date());
        em.persist(acc);
    }

    public void createDefaultAccount(String userEntityId) {

        Person person = new Person();

        person.setExternalId(userEntityId);
        Account acc = new Account();
        acc.setPerson(person);

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

    public void payrollFromAccount() {
        //вывод средств
    }
}