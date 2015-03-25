package main.java.managers.grabbers;

import main.java.managers.games.GameManager;
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

    @EJB
    private GameManager gameManager;

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

    public List<GameEvent> parseByName(List<ScheduleParser> parsers, List<Game> games, int year, int month, int day) {

        List<GameEvent> lst = new ArrayList<>();

        List<String> gameNames = gameManager.getGameNames(games);

        for (ScheduleParser parser: parsers) {
            Game game = games.get(gameNames.indexOf(parser.getGame().getName()));

            List<GameEvent> parserEvents = parseByName(parser, game, year, month, day);
            for (GameEvent event: parserEvents) {
                event.setGameName(game.getName());
            }
            lst.addAll(parserEvents);
        }

        return lst;
    }

}
