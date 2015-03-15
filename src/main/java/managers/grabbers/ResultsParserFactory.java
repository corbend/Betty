package main.java.managers.grabbers;

import main.java.managers.grabbers.parsers.ResultParser;
import main.java.managers.grabbers.parsers.interfaces.ParserFactory;
import main.java.models.sys.ScheduleParser;

import java.util.HashMap;
import java.util.Map;

public class ResultsParserFactory implements ParserFactory<ResultParser, ScheduleParser> {
    //фабрика парсеров результатов матчей
    private Map<String, Class<? extends ResultParser>> parserPool = new HashMap<>();

    public ResultsParserFactory() {

        //parserPool.put();
    }

    public ResultParser create(ScheduleParser persistentParser) throws InstantiationException, IllegalAccessException {
        return parserPool.get(persistentParser.getName()).newInstance();
    }
}
