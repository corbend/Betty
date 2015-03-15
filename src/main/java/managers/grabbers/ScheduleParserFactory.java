package main.java.managers.grabbers;

import main.java.managers.grabbers.parsers.EventParser;
import main.java.managers.grabbers.parsers.interfaces.ParserFactory;
import main.java.managers.grabbers.parsers.nba.ESPNParser;
import main.java.managers.grabbers.parsers.nba.LiveScoreInParser;
import main.java.managers.grabbers.parsers.qualifiers.EventSchedule;
import main.java.models.sys.ScheduleParser;

import java.util.HashMap;
import java.util.Map;

@EventSchedule
public class ScheduleParserFactory implements ParserFactory<EventParser, ScheduleParser> {
    //фабрика парсеров расписания матчей
    private Map<String, Class<? extends EventParser>> parserPool = new HashMap<>();

    public ScheduleParserFactory() {
        parserPool.put("livescore.in", LiveScoreInParser.class);
        parserPool.put("espn.com", ESPNParser.class);
    }

    public EventParser create(ScheduleParser persistentParser) throws InstantiationException, IllegalAccessException {
        EventParser p = parserPool.get(persistentParser.getName()).newInstance();
        p.setPersistenceParser(persistentParser);
        return p;
    }
}
