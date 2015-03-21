package main.java.billing.managers;

import main.java.billing.models.Account;
import main.java.billing.models.Person;
import main.java.billing.models.Transaction;
import main.java.managers.messages.AccountMessage;
import main.java.models.users.User;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Stateless
//@Local(AccountLocal.class)
public class AccountEJB {

    public class NotEnoughFundsException extends Exception {}
    public class ServiceAccountNotFound extends Exception {}

    private static Logger log = Logger.getLogger(AccountEJB.class.getName());
    @PersistenceContext
    protected EntityManager em;

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    @JMSSessionMode(JMSContext.CLIENT_ACKNOWLEDGE)
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

    public Account getServiceAccount() throws ServiceAccountNotFound {

        Account acc = new Account();

        try {
            em.createNamedQuery("Account.getService").getSingleResult();
        } catch (NoResultException e) {
            acc = null;
            throw new ServiceAccountNotFound();
        }

        return acc;
    }

    public Person getPersonByUser(User user) {
        return em.createNamedQuery("Person.findByExternalId", Person.class)
                .setParameter("externalId", user.getLogin()).getSingleResult();
    }

    public Account getDefaultAccount(String username) {
        try {
            User user = em.createNamedQuery("User.findByLogin", User.class).setParameter("login", username).getSingleResult();
            Person person = getPersonByUser(user);
            return person.getAccount();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void incrementBalance(Long accountId, Double amount) throws JMSException {

        Account acc = em.find(Account.class, accountId);

        Double oldAmount = acc.getTotalAmount();
        Double newAmount = oldAmount + amount;

        acc.setTotalAmount(newAmount);

        ObjectMessage msg = context.createObjectMessage();
        AccountMessage accMsg = new AccountMessage();
        accMsg.setAction("ACCOUNT_INC");
        accMsg.setAmount(amount);
        accMsg.setAccountId(accountId);
        msg.setObject(accMsg);

        log.log(Level.INFO, "SEND MESSAGE->" + accMsg.getAction());
        JMSConsumer consumer = context.createConsumer(accountQueue);

        context.createProducer().setDeliveryMode(DeliveryMode.PERSISTENT).send(accountQueue, msg);

        while (true) {
            ObjectMessage inMsg = (ObjectMessage) consumer.receive();
            AccountMessage inAccMsg = (AccountMessage) inMsg.getObject();
            if (inAccMsg.getAction().equals("ACCOUNT_INC") && inAccMsg.getStatus().equals("OK")) {
                inMsg.acknowledge();
                log.log(Level.INFO, "GET ACK MESSAGE->" + inAccMsg.getAction());
                em.persist(acc);
                return;
            }
        }
    }

    //используем дефолтный аккаунт
    public void incrementBalance(String username, Double amount) throws JMSException {
        Account defAccount = getDefaultAccount(username);
        incrementBalance(defAccount.getId(), amount);
    }

    //используем дефолтный аккаунт
    public void decrementBalance(String username, Double amount) throws JMSException, NotEnoughFundsException {
        Account defAccount = getDefaultAccount(username);
        decrementBalance(defAccount.getId(), amount);
    }

    public void decrementBalance(Long accountId, Double amount) throws NotEnoughFundsException, JMSException {

        Account acc = em.find(Account.class, accountId);

        Double oldAmount = acc.getTotalAmount();

        Double newAmount = oldAmount - amount;

        if (newAmount < 0) {
            throw new NotEnoughFundsException();
        }

        acc.setTotalAmount(newAmount);

        ObjectMessage msg = context.createObjectMessage();
        AccountMessage accMsg = new AccountMessage();
        accMsg.setAction("ACCOUNT_DEC");
        accMsg.setAmount(amount);
        accMsg.setAccountId(accountId);
        msg.setObject(accMsg);

        log.log(Level.INFO, "SEND MESSAGE->" + accMsg.getAction());
        JMSConsumer consumer = context.createConsumer(accountQueue);

        context.createProducer().send(accountQueue, msg);

        while (true) {

            ObjectMessage inMsg = (ObjectMessage) consumer.receive();
            AccountMessage inAccMsg = (AccountMessage) inMsg.getObject();
            if (inAccMsg.getAction().equals("ACCOUNT_DEC") && inAccMsg.getStatus().equals("OK")) {
                inMsg.acknowledge();
                return;
            }
        }
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