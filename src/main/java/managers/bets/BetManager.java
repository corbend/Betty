package main.java.managers.bets;

import main.java.managers.messages.AccountMessage;
import main.java.managers.users.UserEJB;
import main.java.models.bets.CustomBet;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;
import main.java.models.users.User;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
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

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/AccountActionQueue")
    private Queue accountQueue;

    @EJB
    private UserEJB userEJB;


    public UserBet putBet(String userId, LiveBet liveBet, Double amount) {

        UserBet bet = new UserBet();
        User usr = userEJB.getUser(userId);

        AccountMessage checkAccountMsg = new AccountMessage("ACCOUNT_DEC", usr.getAccountId());
        checkAccountMsg.setAmount(amount);
        checkAccountMsg.setOutputMessage("UserBet:" + bet.getId() + ":hold");
        //создаем новую ставку и ждем подтвреждения списания средств

        bet.setUser(usr);
        bet.setLiveBet(liveBet);
        bet.setStatus(UserBet.Status.PENDING);
        bet.setAmount(amount);
        bet.setCoefficient(liveBet.getCoefficient());

        em.persist(bet);

        JMSProducer prod = context.createProducer();
        prod.send(accountQueue, checkAccountMsg);

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

        LiveBet liveBet = bet.getLiveBet();
        em.merge(liveBet);
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

    public List<UserBet> getUserBetsByLiveBet(LiveBet liveBet) {
        return em.createNamedQuery("UserBet.getByLiveBet", UserBet.class).setParameter("liveBet", liveBet).getResultList();
    }

    public void saveCustomBet(CustomBet cbet) {

        em.persist(cbet);
    }
}
