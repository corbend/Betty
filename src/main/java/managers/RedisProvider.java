package main.java.managers;


import main.java.managers.service.RedisManager;
import main.java.models.bets.UserBet;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;

import javax.enterprise.inject.Produces;

public class RedisProvider {

    @Produces
    public RedisManager<ScheduleParser> getProvider() {
        return new RedisManager<>("127.0.0.1", 6379, "Schedule");
    };

    @Produces
    public RedisManager<GameEvent> getEventProvider() {
        return new RedisManager<>("127.0.0.1", 6379, "GameEvent");
    }

    @Produces
    public RedisManager<UserBet> getBetsProvider() {
        return new RedisManager<>("127.0.0.1", 6379, "UserBet");
    }

}
