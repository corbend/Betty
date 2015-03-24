package main.java.managers.grabbers.parsers;

import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;

import javax.ejb.Local;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultParser {

    public List<GameEvent> parse(ScheduleParser parser, Game game) {return new ArrayList<>();};

    public ScheduleParser getPersistenceParser() {return null; };
    public void setPersistenceParser(ScheduleParser parser) {};
}
