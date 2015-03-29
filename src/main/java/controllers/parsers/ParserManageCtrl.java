package main.java.controllers.parsers;

import main.java.managers.service.RedisManager;
import main.java.models.sys.ScheduleParser;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("parserManageCtrl")
@ViewScoped
public class ParserManageCtrl implements Serializable {

    private List<ScheduleParser> parsers;
    private ScheduleParser selectedParser;

    @Inject
    private RedisManager<ScheduleParser> redisManager;

    @PostConstruct
    public void init() {
        parsers = getParsers();
    }

    public List<ScheduleParser> getParsers() {
        return redisManager.getRange("Parsers", 0, -1);
    }


    public ScheduleParser getSelectedParser() {
        return selectedParser;
    }

    public void setSelectedParser(ScheduleParser selectedParser) {
        this.selectedParser = selectedParser;
    }
}
