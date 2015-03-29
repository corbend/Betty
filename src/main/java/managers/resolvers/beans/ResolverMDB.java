package main.java.managers.resolvers.beans;

import main.java.managers.bets.BetManager;
import main.java.managers.bets.LiveBetsManager;
import main.java.managers.messages.GameEventMessage;
import main.java.managers.resolvers.interfaces.BetResolveProvider;
import main.java.models.bets.BetType;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;
import main.java.models.games.BetResult;
import main.java.models.games.GameEvent;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(
        mappedName="jms/javaee7/EventsActionQueue",
        activationConfig =
                {
                        @ActivationConfigProperty(propertyName = "destinationType",
                                propertyValue = "javax.jms.Queue"),
                        @ActivationConfigProperty(propertyName = "destination",
                                propertyValue = "EventsActionQueue")
                }
)
public class ResolverMDB implements MessageListener {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private BetManager betManager;

    @EJB
    private LiveBetsManager liveBetsManager;

    private Logger log = Logger.getAnonymousLogger();

    @Inject
    private BetResolveProvider betResolveProvider;

    private void setBetsToResolve(GameEvent gameEvent, GameEventMessage msg) {

        List<LiveBet> liveBetsForEvent = liveBetsManager.getBetsForEvent(gameEvent);
        List<UserBet> userBets = new ArrayList<>();

        log.log(Level.INFO, "Proccess live bets resolving=>" + liveBetsForEvent.size() +
                ", event=" + "(" + gameEvent.getId() + ")" + msg.getEvent() +
                ", msg=" + msg.toString());

        for (LiveBet liveBet: liveBetsForEvent) {

            userBets.addAll(betManager.getUserBetsByLiveBet(liveBet));
            log.log(Level.INFO, "Proccess user bets resolving=>"
                    + ", event=" + "(" + gameEvent.getId() + ")"
                    + ", size=" + userBets.size());

            HashMap<String, BetResult> scoreTable = new HashMap<>();
            int handicap = msg.getScore1() - msg.getScore2();
            int score1 = msg.getScore1();
            int score2 = msg.getScore2();

            //определяем исходы по базовым ставкам
            if (score1 > score2) {
                log.log(Level.INFO, "Game Result=>team1 win," + score1 + "," + score2);
                scoreTable.put(BetType.OW1.toString(), new BetResult(BetType.OW1.toString(), score1, true));
                scoreTable.put(BetType.OW2.toString(), new BetResult(BetType.OW2.toString(), score2, false));
                scoreTable.put(BetType.W1.toString(), new BetResult(BetType.W1.toString(), score1, true));
                scoreTable.put(BetType.W2.toString(), new BetResult(BetType.W2.toString(), score2, false));
                //распределяем форы
                scoreTable.put(BetType.F1.toString(), new BetResult(BetType.F1.toString(), handicap, true));
                scoreTable.put(BetType.F2.toString(), new BetResult(BetType.F2.toString(), handicap, false));
                scoreTable.put(BetType.G1.toString(), new BetResult(BetType.G1.toString(), handicap, true));
                scoreTable.put(BetType.G2.toString(), new BetResult(BetType.G2.toString(), handicap, false));
            } else if (score1 < score2) {
                log.log(Level.INFO, "Game Result=>team2 win, " + score1 + "," + score2);
                scoreTable.put(BetType.OW1.toString(), new BetResult(BetType.OW1.toString(), score1, false));
                scoreTable.put(BetType.OW2.toString(), new BetResult(BetType.OW2.toString(), score2, true));
                scoreTable.put(BetType.W1.toString(), new BetResult(BetType.W1.toString(), score1, false));
                scoreTable.put(BetType.W2.toString(), new BetResult(BetType.W2.toString(), score2, true));
                //распределяем форы
                scoreTable.put(BetType.F1.toString(), new BetResult(BetType.F1.toString(), handicap, false));
                scoreTable.put(BetType.F2.toString(), new BetResult(BetType.F2.toString(), handicap, true));
                scoreTable.put(BetType.G1.toString(), new BetResult(BetType.G1.toString(), handicap, false));
                scoreTable.put(BetType.G2.toString(), new BetResult(BetType.G2.toString(), handicap, true));
            } else {
                log.log(Level.INFO, "Game Result=>draw, " + score1 + "," + score2);
                scoreTable.put(BetType.D.toString(), new BetResult(BetType.D.toString(), 0, true));
                scoreTable.put(BetType.W1W2.toString(), new BetResult(BetType.W1W2.toString(), 0, true));
            }

            //определяем выигрыши по базовым ставкам
            List<Integer> scores = new ArrayList<>();
            scores.add(score1);
            scores.add(score2);
            betResolveProvider.resolveAll(userBets, gameEvent, scoreTable, scores);
        }
    }

    @Override
    public void onMessage(Message msg) {

        ObjectMessage oMsg = (ObjectMessage) msg;
        try {
            GameEventMessage gMsg = (GameEventMessage) oMsg.getObject();
            setBetsToResolve(gMsg.getEvent(), gMsg);
        } catch (JMSException e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage() + "\r\n" + e.getStackTrace().toString());
        }
    }
}
