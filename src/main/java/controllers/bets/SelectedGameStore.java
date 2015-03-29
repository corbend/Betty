package main.java.controllers.bets;

import main.java.models.bets.BetGroup;
import main.java.models.games.Game;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@SessionScoped
public class SelectedGameStore implements Serializable {

    private Game selectedGame;
    private Long selectedGameId;
    private BetGroup selectedBetGroup;

    public SelectedGameStore() {}

    public Game getSelectedGame() {
        return selectedGame;
    }
    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }

    public Long getSelectedGameId() {
        return selectedGameId;
    }

    public void setSelectedGameId(Long selectedGameId) {
        this.selectedGameId = selectedGameId;
    }

    public BetGroup getSelectedBetGroup() {
        return selectedBetGroup;
    }

    public void setSelectedBetGroup(BetGroup selectedBetGroup) {
        this.selectedBetGroup = selectedBetGroup;
    }
}
