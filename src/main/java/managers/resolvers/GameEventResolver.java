package main.java.managers.resolvers;

import main.java.managers.bets.LiveBetsManager;
import main.java.managers.games.GameManager;
import main.java.managers.games.GameSheduleManager;
import main.java.managers.loggers.interfaces.SystemEventLogger;
import main.java.managers.messages.GameEventMessage;
import main.java.managers.resolvers.interfaces.BetResolveProvider;
import main.java.managers.service.MemoryObject;
import main.java.managers.service.MemoryPoolManager;
import main.java.managers.service.RedisManager;
import main.java.models.bets.BetType;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;
import main.java.models.games.BetResult;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.*;
import javax.jms.Queue;
import java.io.Serializable;
import java.util.*;

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
@Stateless
public class GameEventResolver implements MessageListener {
/**
 * примитивный резолвер, который проверяет окончание игры и рассылает сообщения через JMS об окончании игры и результаты
 * 1) опрашиваем REDIS каждую минуту и берем список игр, запланированных на определенное время
 * 2)
 */

    @EJB
    private LiveBetsManager liveBetsManager;

    @EJB
    private GameSheduleManager gameEventManager;

    @Inject
    private SystemEventLogger systemEventLogger;

    @EJB
    private GameManager gameManager;

    private RedisManager<GameEvent> gameEventPoolManager;

    @Inject
    private BetResolveProvider betResolveProvider;

    @Inject
    @JMSConnectionFactory("jms/javaee7/ConnectionFactory")
    private JMSContext context;
    @Resource(lookup = "jms/javaee7/EventsActionQueue")
    private Queue eventsQueue;

    private Date curDate;
    private Date lastCheck;

    private void extractToPool() {
        //вначале каждого дня запускаем опрос расписания
        if (lastCheck.getDate() != curDate.getDate()) {
            for (Game game : gameManager.getAllActiveGames()) {

                gameEventManager.getAvailableShedules(game, curDate.getYear(), curDate.getMonth(), curDate.getDay());

            }
        } else {
            lastCheck = curDate;
        }
    }

    //@Schedule(minute="*")
    public void checkAllEndedEvents() throws JMSException {

        //проверяем завершенность всех матчей
        curDate = new Date();
        curDate.setSeconds(0);

        List<GameEvent> gameEvents = gameEventPoolManager.getRange(Long.toString(curDate.getTime()), 0, -1);

        if (gameEvents != null) {
            for (GameEvent event: gameEvents) {
                ObjectMessage msg = context.createObjectMessage();

                //TODO - сделать правильный чек результата
                int score1 = new Random(1L).nextInt();
                int score2 = new Random(1L).nextInt();
                msg.setObject(new GameEventMessage(event, score1, score2));
                context.createProducer().send(eventsQueue, msg);
            }
        }

        extractToPool();
    }

    private void setBetsToResolve(GameEvent gameEvent, GameEventMessage msg) {

        List<LiveBet> liveBetsForEvent = liveBetsManager.getBetsForEvent(gameEvent);
        List<UserBet> userBets = new ArrayList<>();
        for (LiveBet liveBet: liveBetsForEvent) {
            userBets.addAll(liveBet.getUserBets());

            HashMap<BetResult, Boolean> scoreTable = new HashMap<>();
            int handicap = msg.getScore1() - msg.getScore2();
            int score1 = msg.getScore1();
            int score2 = msg.getScore2();

            //определяем исходы по базовым ставкам
            if (score1 > score2) {
                scoreTable.put(new BetResult(BetType.OW1.toString(), score1), true);
                scoreTable.put(new BetResult(BetType.OW2.toString(), score2), false);
                scoreTable.put(new BetResult(BetType.W1.toString(), score1), true);
                scoreTable.put(new BetResult(BetType.W2.toString(), score2), false);
                //распределяем форы
                scoreTable.put(new BetResult(BetType.F1.toString(), handicap), true);
                scoreTable.put(new BetResult(BetType.F2.toString(), handicap), false);
                scoreTable.put(new BetResult(BetType.G1.toString(), handicap), true);
                scoreTable.put(new BetResult(BetType.G2.toString(), handicap), false);
            } else if (score1 < score2) {
                scoreTable.put(new BetResult(BetType.OW1.toString(), score1), true);
                scoreTable.put(new BetResult(BetType.OW2.toString(), score2), false);
                scoreTable.put(new BetResult(BetType.W1.toString(), score1), true);
                scoreTable.put(new BetResult(BetType.W2.toString(), score2), false);
                //распределяем форы
                scoreTable.put(new BetResult(BetType.F1.toString(), handicap), false);
                scoreTable.put(new BetResult(BetType.F2.toString(), handicap), true);
                scoreTable.put(new BetResult(BetType.G1.toString(), handicap), false);
                scoreTable.put(new BetResult(BetType.G2.toString(), handicap), true);
            } else {
                scoreTable.put(new BetResult(BetType.D.toString(), 0), true);
                scoreTable.put(new BetResult(BetType.W1W2.toString(), 0), true);
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
        }
    }

}
