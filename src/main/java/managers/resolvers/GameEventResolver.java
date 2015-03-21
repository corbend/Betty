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
import java.util.logging.Level;
import java.util.logging.Logger;


@Stateless
public class GameEventResolver {
/**
 * примитивный резолвер, который проверяет окончание игры и рассылает сообщения через JMS об окончании игры и результаты
 * 1) опрашиваем REDIS каждую минуту и берем список игр, запланированных на определенное время
 * 2)
 */
    private Logger log = Logger.getAnonymousLogger();

    @EJB
    private GameSheduleManager gameEventManager;

    @Inject
    private SystemEventLogger systemEventLogger;

    @EJB
    private GameManager gameManager;

    @Inject
    private RedisManager<GameEvent> gameEventPoolManager;

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

    @Schedule(second="*/30", hour="*", minute="*", timezone="Europe/Moscow")
    public void checkAllEndedEvents() throws JMSException {

        //проверяем завершенность всех матчей
        curDate = new Date();
        curDate.setSeconds(0);

        List<GameEvent> gameEvents = gameEventPoolManager.getRange("GameEvent", 0, -1);

        if (gameEvents != null) {
            for (GameEvent event: gameEvents) {
                log.log(Level.WARNING, "Event Check:" + event.getId());
                GameEvent inMemoryGame = gameEventPoolManager.get(event.getId().toString());
                //наличие по данному ключу данных, означает окончание игры
                if (inMemoryGame == null) {
                    log.log(Level.WARNING, "Event:" + event.toString() + "-> progress.");
                } else {
                    ObjectMessage msg = context.createObjectMessage();

                    int score1 = inMemoryGame.getScores1().get(0);
                    int score2 = inMemoryGame.getScores2().get(0);

                    GameEvent clone = new GameEvent();

                    clone.setId(event.getId());
                    clone.setEventLocation(event.getEventLocation());
                    clone.setEventName(event.getEventName());
                    clone.setEventTime(event.getEventTime());
                    clone.setDateStart(event.getDateStart());
                    clone.setDateEnd(event.getDateEnd());
                    clone.setTeam1Name(event.getTeam1Name());
                    clone.setTeam2Name(event.getTeam2Name());

                    GameEventMessage outm = new GameEventMessage(event, score1, score2);
                    msg.setObject(outm);
                    log.log(Level.WARNING, "Event:" + event.toString() + "-> finished.");
                    context.createProducer().send(eventsQueue, msg);

                    //TODO - сделать удаление события после разрешения всех ставок из памяти
                }
            }
        }

        //extractToPool();
    }

}
