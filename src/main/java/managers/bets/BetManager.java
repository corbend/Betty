package main.java.managers.bets;

import main.java.managers.messages.AccountMessage;
import main.java.managers.service.RedisManager;
import main.java.managers.users.UserEJB;
import main.java.models.bets.CustomBet;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;
import main.java.models.users.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Stateless
public class BetManager {

    private static Logger log = Logger.getLogger(BetManager.class.getName());

    @PersistenceContext
    private EntityManager em;

//    @Resource(name="jms/javaee7/ConnectionFactory")
//    private ConnectionFactory jmsFactory;
//
//    @Resource(name="jsm/javaee7/AccountQueue")
//    private Queue queue;

    @EJB
    private UserEJB userEJB;


    public UserBet putBet(String userId, LiveBet liveBet, Double amount) {

        UserBet bet = new UserBet();
        User usr = userEJB.getUser(userId);

        AccountMessage checkAccountMsg = new AccountMessage("ACCOUNT_DECREMENT", "BET_ACTIVATE", usr.getAccountId(), amount);

        //создаем новую ставку и ждем подтвреждения списания средств

        bet.setUser(usr);
        bet.setLiveBet(liveBet);
        bet.setStatus(UserBet.Status.PENDING);
        bet.setAmount(amount);

        em.persist(bet);

//        JMSProducer prod = jmsFactory.createContext().createProducer();
//        prod.send(queue, checkAccountMsg);

        //после подтверждения списания средств активируем ставку
        bet.setStatus(UserBet.Status.ACTIVE);
        em.merge(bet);

        return bet;
    }

    public UserBet activateBet(Long betId) {
        //перевод активной ставки с состояние активности
        UserBet bet = em.find(UserBet.class, betId);

        bet.setStatus(UserBet.Status.ACTIVE);
        em.merge(bet);

        return bet;
    }

    public void resolveBet(Long betId) {
        //перевод активной ставки с состояние активности
        UserBet bet = em.find(UserBet.class, betId);

        bet.setStatus(UserBet.Status.RESOLVED);
        em.merge(bet);
    }

    public void freezeBet(Long betId) {
        //перевод активной ставки с состояние активности
        UserBet bet = em.find(UserBet.class, betId);

        bet.setStatus(UserBet.Status.FREEZE);
        em.merge(bet);
    }

    public void unfreezeBet(Long betId) {
        //перевод активной ставки с состояние активности
        UserBet bet = em.find(UserBet.class, betId);

        bet.setStatus(UserBet.Status.ACTIVE);
        em.merge(bet);
    }

    public void cancelBet(Long betId) {
        //перевод активной ставки с состояние активности
        UserBet bet = em.find(UserBet.class, betId);

        bet.setStatus(UserBet.Status.CANCELLED);
        em.merge(bet);
    }

    public List<LiveBet> getAllLiveBets() {
        List<LiveBet> bets = em.createNamedQuery("FIND_ALL", LiveBet.class).getResultList();
        return bets;
    }

    public List<UserBet> getActiveBetsByUser(String userId) {

        List<UserBet> bets = em.createNamedQuery("UserBet.getActiveByUser", UserBet.class).getResultList();
        return bets;
    }

    public void saveCustomBet(CustomBet cbet) {

        em.persist(cbet);
    }
}
