package main.java.managers.grabbers.parsers;

import main.java.managers.games.GameManager;
import main.java.managers.grabbers.ScheduleParserFactory;
import main.java.managers.grabbers.parsers.interfaces.ParserFactory;
import main.java.managers.grabbers.parsers.qualifiers.EventSchedule;
import main.java.managers.service.RedisManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class ParserStarter {

    private Logger log = Logger.getAnonymousLogger();

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    @Inject
    private RedisManager<ScheduleParser> redisManager;

    @Inject @EventSchedule
    private ParserFactory<EventParser, ScheduleParser> eventParserFactory;

    @EJB
    private GameManager gameManager;

    @PostConstruct
    public void init() {

        List<ScheduleParser> lst = em.createNamedQuery("ScheduleParser.findAll", ScheduleParser.class).getResultList();
        List<ScheduleParser> detachedList = new ArrayList<>();
        //делаем все парсеры изначально исполнеными, чтобы запустить при первом запуске
//        for (ScheduleParser parser: lst) {
//            parser.setLastCompleteTime(DateTime.now().minusDays(1));
//        }
        //добавляем список парсеров в редис

        for (ScheduleParser l: lst) {
            detachedList.add(l.clone());
        }

        redisManager.addList("Parsers", detachedList);

        List<ScheduleParser> scheduleParsers = redisManager.getRange("Parsers", 0, -1);
        log.log(Level.INFO, "List of parsers=" + scheduleParsers);
        for (ScheduleParser p: scheduleParsers) {
            log.log(Level.INFO, "PARSER IN REDIS=" + p.getName());
            try {
                executeParser(p);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Schedule(minute="*", persistent = false)
    public void checkParserActiveState() {

        //live checking of working parsers
        List<ScheduleParser> lst = redisManager.getRange("Parsers", 0, 10);
        redisManager.trimList("Parsers", 0, 10);

        for (ScheduleParser parser: lst) {
//            DateTime completeTime = parser.getLastCompleteTime();
//            if (completeTime.plusDays(1).isBeforeNow()) {
//                try {
//                    executeParser(parser);
//                } catch (InstantiationException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }

            if (!parser.getStatus()) {
                //TODO - уведомление об плохом статусе парсеров админов
            }
        }
    }

    @Asynchronous
    public Future<List<GameEvent>> executeParser(ScheduleParser parser) throws InstantiationException, IllegalAccessException{

        List<Game> activeGames = gameManager.getAllActiveGames();
        DateTime now = DateTime.now();
        List<GameEvent> ls = (eventParserFactory.create(parser)).parse(
                activeGames.get(0), now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        //FIXME - костыль для тестирования
        for (GameEvent l: ls) {
            em.persist(l);
        }
        return new AsyncResult<>(ls);
    }

}
