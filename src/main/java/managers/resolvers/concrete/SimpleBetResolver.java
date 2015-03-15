package main.java.managers.resolvers.concrete;

import main.java.managers.resolvers.interfaces.BetResolveProvider;
import main.java.managers.service.RedisManager;
import main.java.models.bets.UserBet;
import main.java.models.games.BetResult;
import main.java.models.games.GameEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleBetResolver implements BetResolveProvider {
    //TODO - implement batch-processing

    private static Logger log = Logger.getLogger(SimpleBetResolver.class.getName());
    public static final String GAME_RESOLVE_KEY = "GameResult:";

    private RedisManager<UserBet> redisManager;

    public void resolveAll(List<UserBet> betList, GameEvent gameEvent, HashMap<BetResult, Boolean> scoreTable, List<Integer> scores) {

        for (UserBet bet: betList) {
            Boolean bet1Success = scoreTable.get(new BetResult(bet.getLiveBet().getType(), scores.get(0)));
            Boolean bet2Success = scoreTable.get(new BetResult(bet.getLiveBet().getType(), scores.get(1)));
            bet.setResult(bet1Success || bet2Success);
        }

        redisManager.addList(GAME_RESOLVE_KEY + gameEvent.getId(), betList);

        for (UserBet h: redisManager.getRange(GAME_RESOLVE_KEY + gameEvent.getId(), 0, -1)) {
            log.log(Level.FINE, "RESOLVE-> USER BET=" + h.toString());
        };
    }
}
