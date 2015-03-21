package main.java.managers.resolvers.concrete;

import com.cedarsoftware.util.io.JsonWriter;
import main.java.billing.managers.AccountEJB;
import main.java.managers.messages.AccountMessage;
import main.java.managers.resolvers.interfaces.BetResolveProvider;
import main.java.managers.service.RedisManager;
import main.java.models.bets.UserBet;
import main.java.models.games.BetResult;
import main.java.models.games.GameEvent;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class SimpleBetResolver implements BetResolveProvider {
    //TODO - implement batch-processing
    @PersistenceContext
    private EntityManager em;

    private static Logger log = Logger.getLogger(SimpleBetResolver.class.getName());
    public static final String GAME_RESOLVE_KEY = "GameResult:";

    @EJB
    private AccountEJB accountEJB;

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/AccountActionQueue")
    private Queue accountQueue;

    @Inject
    private RedisManager<UserBet> redisManager;

    public void resolveAll(List<UserBet> betList, GameEvent gameEvent, HashMap<String, BetResult> scoreTable, List<Integer> scores) {

        List<UserBet> listOfBets = new ArrayList<>();

        for (UserBet bet: betList) {
            log.log(Level.INFO, "RESULT CHECK=" + bet.getStatus() + " must be " + UserBet.Status.ACTIVE);
            if (bet.getStatus() == UserBet.Status.ACTIVE) {
                String betType = bet.getLiveBet().getType();
                BetResult betResult = scoreTable.get(betType);

                log.log(Level.INFO, "Game result to bet=" + betResult.getResult() +
                        ", betType=" + betType + ", scoreTable=" + JsonWriter.objectToJson(scoreTable));

                UserBet betToRedis = new UserBet();
                bet.setResult(betResult.getResult());
                bet.setStatus(UserBet.Status.PRERESOLVE);

                betToRedis.setId(bet.getId());
                betToRedis.setAmount(bet.getAmount());
                betToRedis.setResult(bet.getResult());
                betToRedis.setStatus(bet.getStatus());
                betToRedis.setUserName(bet.getUserName());

                listOfBets.add(betToRedis);
                em.persist(bet);
            }

        }

        redisManager.addList(GAME_RESOLVE_KEY + gameEvent.getId(), listOfBets);

        for (UserBet h: redisManager.getRange(GAME_RESOLVE_KEY + gameEvent.getId(), 0, -1)) {
            log.log(Level.INFO, "RESOLVE-> USER BET=" + h.toString());

            ObjectMessage msg = context.createObjectMessage();

            AccountMessage body = new AccountMessage();
            body.setAmount(h.getAmount());
            body.setUsername(h.getRawUserName());

            try {
                //msg.setObject(body);
                //context.createProducer().send(accountQueue, msg);
                if (h.getResult()) {
                    body.setAction("ACCOUNT_INC");
                    accountEJB.incrementBalance(h.getRawUserName(), h.getAmount());
                } else {
                    body.setAction("ACCOUNT_DEC");
                    accountEJB.decrementBalance(h.getRawUserName(), h.getAmount());
                }

            } catch (JMSException | AccountEJB.NotEnoughFundsException e) {
                e.printStackTrace();
                log.log(Level.SEVERE, "User Bet Resolved, but not processed because of error=" + e.getMessage());
            }

        };
    }
}
