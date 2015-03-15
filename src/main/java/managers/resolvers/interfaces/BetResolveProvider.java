package main.java.managers.resolvers.interfaces;

import main.java.models.bets.UserBet;
import main.java.models.games.BetResult;
import main.java.models.games.GameEvent;

import java.util.HashMap;
import java.util.List;

public interface BetResolveProvider {

    public void resolveAll(List<UserBet> betList,
                           GameEvent gameEvent,
                           HashMap<BetResult, Boolean> scoreTable,
                           List<Integer> scores);
}
