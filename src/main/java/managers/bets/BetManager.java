package main.java.managers.bets;

import main.java.managers.messages.AccountMessage;
import main.java.managers.messages.BetPutMessage;
import main.java.managers.users.UserEJB;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;
import main.java.models.games.GameShedule;
import main.java.models.users.User;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Stateless
public class BetManager {

    @PersistenceContext
    private EntityManager em;

//    @Resource(name="jms/javaee7/ConnectionFactory")
//    private ConnectionFactory jmsFactory;
//
//    @Resource(name="jsm/javaee7/AccountQueue")
//    private Queue queue;

    @EJB
    private UserEJB userEJB;

    public UserBet putBet(Long userId, LiveBet liveBet, Double amount) {
        User usr = userEJB.getUser(userId);

        AccountMessage checkAccountMsg = new AccountMessage("ACCOUNT_DECREMENT", "BET_ACTIVATE", usr.getAccountId(), amount);

        //создаем новую ставку и ждем подтвреждения списания средств
        UserBet bet = new UserBet();
        bet.setLiveBet(liveBet);
        bet.setStatus(UserBet.Status.PENDING);
        bet.setAmount(amount);
        em.persist(bet);

//        JMSProducer prod = jmsFactory.createContext().createProducer();
//        prod.send(queue, checkAccountMsg);

        //после подтверждения списания средств активируем ставку
        activateBet(bet.getId());

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
}
