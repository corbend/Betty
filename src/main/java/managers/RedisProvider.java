package main.java.managers;


import main.java.managers.grabbers.parsers.nba.LiveScoreInParser;
import main.java.managers.grabbers.parsers.nba.LiveScoreInResultParser;
import main.java.managers.interfaces.RedisPoolManager;
import main.java.managers.service.RedisManager;
import main.java.models.bets.UserBet;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Startup
@Singleton
public class RedisProvider {

    @EJB
    private JedisPoolProvider jedisPoolProvider;
    private RedisManager<ScheduleParser> scheduleRedisManager;
    private RedisManager<GameEvent> eventRedisManager;
    private RedisManager<UserBet> betRedisManager;

    @PostConstruct
    public void init() {
        scheduleRedisManager = new RedisManager<>(jedisPoolProvider.getPool(), "Schedule");
        eventRedisManager = new RedisManager<>(jedisPoolProvider.getPool(), "GameEvent");
        betRedisManager = new RedisManager<>(jedisPoolProvider.getPool(), "UserBet");
    }

    @Produces @Default
    public RedisManager<ScheduleParser> getParserProvider() {
        return scheduleRedisManager;
    };

    @Produces @Default
    public RedisManager<GameEvent> getEventProvider() {
        return eventRedisManager;
    }

    @Produces @Default
    public RedisManager<UserBet> getBetsProvider() {
        return betRedisManager;
    }

}
