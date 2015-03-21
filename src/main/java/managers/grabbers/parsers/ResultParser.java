package main.java.managers.grabbers.parsers;

import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;

import java.util.ArrayList;
import java.util.List;

public abstract class ResultParser {

    private String url;

    private ScheduleParser persistenceParser;
    public ScheduleParser getPersistenceParser() {
        return persistenceParser;
    }

    public void setPersistenceParser(ScheduleParser persistenceParser) {
        this.persistenceParser = persistenceParser;
    }


    public ResultParser(ScheduleParser parser) {this.persistenceParser = parser;}

    public ResultParser() {}

    public List<GameEvent> parse(Game game) {
        return new ArrayList<>();
    }
}
