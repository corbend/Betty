package main.java.managers.grabbers;

import main.java.managers.grabbers.parsers.ResultParser;
import main.java.managers.grabbers.parsers.interfaces.ParserFactory;
import main.java.managers.grabbers.parsers.nba.LiveScoreInParser;
import main.java.managers.grabbers.parsers.nba.LiveScoreInResultParser;
import main.java.managers.grabbers.parsers.qualifiers.ResultCheck;
import main.java.models.sys.ScheduleParser;

import java.util.HashMap;
import java.util.Map;

@ResultCheck
public class ResultsParserFactory implements ParserFactory<ResultParser, ScheduleParser> {
    //фабрика парсеров результатов матчей
    private Map<String, Class<? extends ResultParser>> parserPool = new HashMap<>();

    public ResultsParserFactory() {

        parserPool.put("livescore.in", LiveScoreInResultParser.class);
    }

    public ResultParser create(ScheduleParser persistentParser) throws InstantiationException, IllegalAccessException {
        ResultParser p = parserPool.get(persistentParser.getName()).newInstance();
        p.setPersistenceParser(persistentParser);
        return p;
    }
}
