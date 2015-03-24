package main.java.managers.grabbers;

import main.java.managers.grabbers.parsers.nba.LiveScoreInResultParser;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ResultsParserFactory {
    //фабрика парсеров результатов матчей
    @EJB
    private LiveScoreInResultParser scoreParser;

    public List<GameEvent> parseByName(ScheduleParser persistentParser, Game game) {

        String name = persistentParser.getName();
        List<GameEvent> res = new ArrayList<>();
        switch(name) {
            case "livescore.in":
                res = scoreParser.parse(persistentParser, game);
                break;
        }

        return res;
    }
}
