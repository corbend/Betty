package main.java.managers.grabbers.parsers;

import main.java.managers.games.GameManager;
import main.java.managers.games.GameSheduleManager;
import main.java.managers.grabbers.ScheduleParserFactory;
import main.java.managers.service.RedisManager;
import main.java.managers.service.WebDriverLauncher;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class ScheduleParserStarter {

    private Logger log = Logger.getAnonymousLogger();

    private static final String REDIS_LAST_SCHEDULE_DATE_KEY = "GameLastScheduleTime";
    private static final String REDIS_NEXT_SCHEDULE_DATE_KEY = "GameNextScheduleTimes";

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    @EJB
    private WebDriverLauncher webDriverLauncher;

    @Inject
    private RedisManager<ScheduleParser> redisManager;

    @Inject
    private RedisManager<GameEvent> gameEventRedisManager;

    @EJB
    private ScheduleParserFactory eventParserFactory;

    @EJB
    private GameManager gameManager;

    @EJB
    private GameSheduleManager gameEventManager;

    private Boolean isNeedForNewSchedule(Game game, Date date) {

        DateTime nowDate = new DateTime(date);
        Boolean res = false;

        DateTime checkDate = nowDate;
        List<GameEvent> nextScheduleList = gameEventManager.getSavedShedulesForGame(game, checkDate.toDate());

        if (nextScheduleList.size() == 0) {
            game.setNeedToSchedule(true);
            res = true;
        } else {
            log.log(Level.INFO, "Schedule is up to date.");
        }

        return res;
    }

    private void initTodayEvents() {

        //инициализируем события на сегодня
        List<GameEvent> todayEvents = gameEventManager.getAllTodaySchedules();
        List<GameEvent> serializableList = new ArrayList<>();
        log.log(Level.INFO, "SET TODAY EVENT=" + todayEvents.size());
        Map<String, List<GameEvent>> map = new HashMap<>();

        for (GameEvent clonable: todayEvents) {
            GameEvent clone = new GameEvent();
            clone.setId(clonable.getId());
            clone.setEventLocation(clonable.getEventLocation());
            clone.setEventName(clonable.getEventName());
            clone.setEventTime(clonable.getEventTime());
            clone.setDateStart(clonable.getDateStart());
            clone.setDateEnd(clonable.getDateEnd());
            clone.setTeam1Name(clonable.getTeam1Name());
            clone.setTeam2Name(clonable.getTeam2Name());
            String gameName = clonable.getGame().getName();
            List<GameEvent> events = map.get(gameName);

            if (events == null) {
                List<GameEvent> nList = new ArrayList<>();
                nList.add(clone);
                map.put(gameName, nList);
            } else {
                events.add(clone);
            }
        }

        for (String key: map.keySet()) {
            gameEventRedisManager.addList("GameEvent:" + key, map.get(key));
        }

    }

    @PostConstruct
    public void init() {

        try {
            webDriverLauncher.runWebdriver();

            List<ScheduleParser> lst = em.createNamedQuery("ScheduleParser.findAll", ScheduleParser.class).getResultList();
            List<ScheduleParser> detachedList = new ArrayList<>();

            //делаем все парсеры изначально исполнеными, чтобы запустить при первом запуске
            for (ScheduleParser parser : lst) {
                parser.setLastCompleteTime(DateTime.now().minusDays(1));
                ScheduleParser clone = parser.clone();
                clone.setGame(null);
                detachedList.add(clone);
            }

            //добавляем список парсеров в редис
            redisManager.addList("Parsers", detachedList);

            initTodayEvents();
            executeParsers(detachedList, DateTime.now());
            createNewSchedule();
        } catch (IOException | InterruptedException e) {
            log.log(Level.SEVERE, "FATAL--Chrome driver is not launched.");
        }

    }

    @PreDestroy
    public void onDestroy() {

        redisManager.trimList(REDIS_NEXT_SCHEDULE_DATE_KEY, 0, -1);
//        redisManager.trimList("GameEvent", 0, -1);

    }

    private void createNewSchedule() {

        TimerService timerService = ctx.getTimerService();
        //планируем расписание на 5 дней вперед
        int dateInFuture = 8;
        DateTime lastScheduleTime = DateTime.now().plusDays(dateInFuture);
        DateTime targetTime = DateTime.now();
        for (int i = 1; i < dateInFuture; i++) {

            redisManager.pushDateList(REDIS_NEXT_SCHEDULE_DATE_KEY, targetTime.plusDays(i));
            TimerConfig conf = new TimerConfig();
            conf.setPersistent(false);
            Timer timer = timerService.createSingleActionTimer(targetTime.plusSeconds(i * 5).toDate(), conf);
        }

        redisManager.setRawKey(REDIS_LAST_SCHEDULE_DATE_KEY,
                lastScheduleTime.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ss")));
    }

    @Timeout
    public void getNewSchedules(Timer timer) {

        List<ScheduleParser> parsers = redisManager.getRange("Parsers", 0, 10);

        DateTime date = redisManager.popDate(REDIS_NEXT_SCHEDULE_DATE_KEY);

        log.log(Level.INFO, "Schedule Prepare Next->" + date);

        if (parsers.size() == 0) {
            log.log(Level.SEVERE, "[ERROR] - NO PARSERS FOUND!");
        }

        try {
            executeParsers(parsers, date);
            log.log(Level.INFO, "PARSE: PARSE COMPLETE!" + date);
        } catch (Exception e) {
            log.log(Level.INFO, "PARSE ERROR: PARSE INCOMPLETE!" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Schedule(dayOfMonth = "*", hour="0", persistent=false)
    public void setNextScheduleDate() {
        try {
            DateTime lastDate = DateTime.parse((String) redisManager.getRawKey(REDIS_LAST_SCHEDULE_DATE_KEY));
            //если последняя дата расписания меньше текущей даты
            //то нужно запланировать новое расписание
            if (lastDate.minusDays(1).isBeforeNow()) {
                log.log(Level.INFO, "Schedule Reinit ->Last Checked Time=" + lastDate);
                createNewSchedule();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Schedule is not fill! - ERROR:" + e.getMessage());
        }
    }

    @Schedule(minute="*/2", hour="*", persistent=false)
    public void checkParserActiveState() {

        //live checking of working parsers
        List<ScheduleParser> lst = redisManager.getRange("Parsers", 0, 10);

        //срезать список не надо пока
        //redisManager.trimList("Parsers", 0, 10);
        log.log(Level.INFO, "PARSERS HEALTH CHECK->date=" + DateTime.now());

        for (ScheduleParser parser: lst) {
            DateTime completeTime = parser.getLastCompleteTime();

            if (completeTime.plusDays(1).isBeforeNow()) {
                //TODO - парсер не исполнялся х дней со дня последнего старта
                log.log(Level.SEVERE, "Parser->" + parser.getName() + "is in not functional state.");
            }

            if (!parser.getStatus()) {
                //TODO - уведомление об плохом статусе парсеров админов
                log.log(Level.SEVERE, "Parser->" + parser.getName() + "is in abnormal state.");
            }
        }

        log.log(Level.INFO, "PARSERS HEALTH CHECK->DONE");
    }

    @Asynchronous
    public Future<List<GameEvent>> executeParsers(List<ScheduleParser> parsers, DateTime date) {

        List<Game> activeGames = gameManager.getAllActiveGames();

        if (date == null) {
            date = DateTime.now();
        }

        List<ScheduleParser> needToParse = new ArrayList<>();

        for (ScheduleParser parser: parsers) {
            ScheduleParser dbParser = em.find(ScheduleParser.class, parser.getId());
            if (isNeedForNewSchedule(dbParser.getGame(), date.toDate())) {
                needToParse.add(dbParser);
            }
        }

        log.log(Level.INFO, "MAKE QUEUE OF SCHEDULE PARSERS->" + needToParse.size() + ", date=" + date);

        List<GameEvent> ls = eventParserFactory.parseByName(needToParse,
                activeGames, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());

        log.log(Level.INFO, "LIST OF PREPARED SCHEDULE->" + ls.size() + ", date=" + date);
        List<String> gameNames = gameManager.getGameNames(activeGames);
        for (GameEvent l: ls) {
            l.setGame(activeGames.get(gameNames.indexOf(l.getGameName())));
            em.persist(l);
        }

        //если запрашивается данные по расписанию за текущую дату, то нужно инициировать записи в Redis
        //для возможности обработки ставок
        if (DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillis(0) ==
                date.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillis(0)) {
            initTodayEvents();
        }

        return new AsyncResult<>(ls);
    }

}
