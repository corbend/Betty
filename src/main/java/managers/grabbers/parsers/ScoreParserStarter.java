package main.java.managers.grabbers.parsers;

import main.java.managers.games.GameManager;
import main.java.managers.grabbers.ResultsParserFactory;
import main.java.managers.service.RedisManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class ScoreParserStarter {

    private Logger log = Logger.getAnonymousLogger();

    @Inject
    private RedisManager<ScheduleParser> redisManager;

    @EJB
    private GameManager gameManager;

    @EJB
    private ResultsParserFactory scoreParserFactory;

    @Schedule(second="*/20", minute="*", hour="*", persistent = false, timezone="Europe/Moscow")
    public void parseGameResults() {
        List<ScheduleParser> parsers = redisManager.getRange("Parsers", 0, 10);
        log.log(Level.INFO, "CHECK: prepare to parse result=>>parsers count=" + parsers.size());

        for (ScheduleParser p : parsers) {

            try {
                executeResultParser(p);
            } catch (InstantiationException | IllegalAccessException e) {
                log.log(Level.SEVERE, "ERROR: executing result parser=" + e.getMessage());
                e.printStackTrace();
                p.setComplete(false);
            }
        }
    }

    @Asynchronous
    public Future<List<GameEvent>> executeResultParser(ScheduleParser parser) throws InstantiationException, IllegalAccessException {
        List<Game> activeGames = gameManager.getAllActiveGames();

        List<GameEvent> ls = scoreParserFactory.parseByName(parser, activeGames.get(0));

        return new AsyncResult<>(ls);
    }
}
