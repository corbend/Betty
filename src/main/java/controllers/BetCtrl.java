package main.java.controllers;

import main.java.managers.bets.BetManager;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class BetCtrl {

    @Inject
    private BetManager betManager;

    private UserBet bet;

    @PostConstruct
    public void init() {
        bet = new UserBet();
    }

    public List<LiveBet> findAllGames() {
        List<LiveBet> gs = betManager.getAllLiveBets();
        return gs;
    }

    public void putBet() {
        betManager.putBet(1L, bet.getLiveBet(), bet.getAmount());
    }
}
