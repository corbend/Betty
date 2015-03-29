package main.java.controllers.bets;

import main.java.models.bets.BetGroup;
import main.java.models.games.Game;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("betGroupForGameCtrl")
@ViewScoped
@Stateless
public class BetGroupForGameCtrl implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private SelectedGameStore selectedGameStore;

    private Game selectedGame;

    private String selectedGameId;
    public String getSelectedGameId() {
        return selectedGameId;
    }

    public void setSelectedGameId(String selectedGameId) {
        this.selectedGameId = selectedGameId;
    }


    private List<Game> gamesToSelect;
    private BetGroup selectedBetGroup;
    private BetGroup newBetGroup;
    private List<BetGroup> betGroups;
    private List<SelectItem> gameItems = new ArrayList<>();

    private Boolean autoActivate;

    @PostConstruct
    public void init() {
        gamesToSelect = getGamesToSelect();
        for (Game game: gamesToSelect) {
            SelectItem selectItem = new SelectItem(game.getId().toString(), game.getName());
            gameItems.add(selectItem);
        }
    }

    public List<Game> getGamesToSelect() {
        return em.createNamedQuery("Game.findAll", Game.class).getResultList();
    }

    public void setGamesToSelect(List<Game> gamesToSelect) {
        this.gamesToSelect = gamesToSelect;
    }


    public void setGameBetGroupForGame() {

        List<BetGroup> gameBetGroups = selectedGame.getBetGroups();
        gameBetGroups.add(selectedBetGroup);

        em.merge(selectedGame);
    }

    public List<BetGroup> getBetGroups() {

        try {
            if (selectedGameId != null) {
                selectedGame = em.find(Game.class, Long.parseLong(selectedGameId));
                List<BetGroup> betGroupsForGame = selectedGame.getBetGroups();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("selectedGame=" + selectedGame.getId()));
                return betGroupsForGame;
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No game selected!"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.toString()));
        }

        return new ArrayList<>();
    }

    public void setBetGroups(List<BetGroup> betGroups) {
        this.betGroups = betGroups;
    }

    public Boolean getAutoActivate() {
        return autoActivate;
    }

    public void setAutoActivate(Boolean autoActivate) {
        //устанавливаем авто активацию ставок из групп привязанных к игровому типу

        List<BetGroup> betGroupGame = selectedGame.getBetGroups();

        for (BetGroup betGroup: betGroupGame) {
            betGroup.setAutoActivate(autoActivate);
            em.merge(betGroup);
        }

        autoActivate = true;
        String modeText = autoActivate ? "On": "Off";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Auto Activate Mode is " + modeText));
    }

    public Game getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }

    public void onSelectGame() {

        betGroups = getBetGroups();

        if (selectedGameStore != null) {
            selectedGameStore.setSelectedGame(selectedGame);
            selectedGameStore.setSelectedGameId(Long.parseLong(selectedGameId));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select game" + selectedGameId));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Game not set to session!"));
        }
    }

    public BetGroup getSelectedBetGroup() {
        return selectedBetGroup;
    }

    public void setSelectedBetGroup(BetGroup selectedBetGroup) {

        this.selectedBetGroup = selectedBetGroup;
        selectedGameStore.setSelectedBetGroup(selectedBetGroup);
    }

    public void onSelectEvent(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Select a bet group=" + ((BetGroup) event.getObject()).getId()));
    }

    public void removeBetGroupFromGame() {
        if (this.selectedBetGroup != null) {
            selectedGame.getBetGroups().remove(selectedBetGroup);
            em.merge(selectedGame);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("No bet group selected"));
        }
    }

    public BetGroup getNewBetGroup() {
        return newBetGroup;
    }
    public void setNewBetGroup(BetGroup newBetGroup) {
        this.newBetGroup = newBetGroup;
    }

    public List<SelectItem> getGameItems() {
        return gameItems;
    }
    public void setGameItems(List<SelectItem> gameItems) {
        this.gameItems = gameItems;
    }

}
