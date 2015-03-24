package main.java.managers.grabbers;

import main.java.managers.grabbers.parsers.nba.ESPNParser;
import main.java.managers.grabbers.parsers.nba.LiveScoreInParser;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ScheduleParserFactory {
    //фабрика парсеров расписания матчей

    @EJB
    private LiveScoreInParser livescoreInParser;

    public List<GameEvent> parseByName(ScheduleParser parser, Game game, int year, int month, int day) {
        String name = parser.getName();
        List<GameEvent> lst = new ArrayList<>();
        switch(name) {
            case "livescore.in":
                lst = livescoreInParser.parse(parser, game, year, month, day);
                break;
        }

       return lst;
    }

}
