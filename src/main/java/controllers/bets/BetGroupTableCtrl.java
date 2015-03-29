package main.java.controllers.bets;

import main.java.managers.bets.BetGroupManager;
import main.java.models.bets.BetGroup;
import main.java.models.bets.CustomBet;
import main.java.models.bets.LiveBet;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("betGroupTableCtrl")
@ViewScoped
@Stateless
public class BetGroupTableCtrl implements Serializable {

    @Inject
    private SelectedGameStore selectedGameStore;

    private Game selectedGame;
    private SelectItem selectedGameItem;

    private String attachMode = "event";
    private boolean betGroupSelected;

    public List<CustomBet> getBetGroupBets() {
        return betGroupBets;
    }

    public void setBetGroupBets(List<CustomBet> betGroupBets) {
        this.betGroupBets = betGroupBets;
    }

    private List<CustomBet> betGroupBets;

    private boolean createAllowed = false;
    public boolean isCreateAllowed() {
        return createAllowed;
    }
    public void setCreateAllowed(boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    private boolean createNewGroupToGameAllowed = false;
    public boolean isCreateNewGroupToGameAllowed() {
        return createNewGroupToGameAllowed;
    }

    public void setCreateNewGroupToGameAllowed(boolean createNewGroupToGameAllowed) {
        this.createNewGroupToGameAllowed = createNewGroupToGameAllowed;
    }

    private boolean noGroup = true;
    public boolean isNoGroup() {
        return noGroup;
    }

    public void setNoGroup(boolean noGroup) {
        this.noGroup = noGroup;
    }

    public boolean isBetGroupSelected() {
        return betGroupSelected;
    }

    public void setBetGroupSelected(boolean betGroupSelected) {
        this.betGroupSelected = betGroupSelected;
    }


    @PersistenceContext
    private EntityManager em;

    @EJB
    private BetGroupManager betGroupManager;

    private GameEvent selectedGameEvent;
    public GameEvent getSelectedGameEvent() {
        return selectedGameEvent;
    }

    public void setSelectedGameEvent(GameEvent selectedGameEvent) {
        this.selectedGameEvent = selectedGameEvent;
    }

    private CustomBet selectedCustomBet;
    public CustomBet getSelectedCustomBet() {
        return selectedCustomBet;
    }

    public void setSelectedCustomBet(CustomBet selectedCustomBet) {
        this.selectedCustomBet = selectedCustomBet;
    }

    private CustomBet selectedBet;
    public CustomBet getSelectedBet() {
        return selectedBet;
    }

    public void setSelectedBet(CustomBet selectedBet) {
        this.selectedBet = selectedBet;
    }

    private BetGroup selectedBetGroup;
    public BetGroup getSelectedBetGroup() {
        return selectedBetGroup;
    }

    public void setSelectedBetGroup(BetGroup selectedBetGroup) {
        this.selectedBetGroup = selectedBetGroup;
    }

    public BetGroup getBetGroupForEvent() {

        //TODO - сделать фильтрацию по событию
        BetGroup emptyGroup = new BetGroup();
        if (selectedGameEvent != null) {
            return betGroupManager.getBetGroupForEvent(selectedGameEvent);
        } else {
            return emptyGroup;
        }
    }

    public void onSelectGameEvent(SelectEvent event) {

        if (attachMode.equals("event")) {
            selectedGameEvent = (GameEvent) event.getObject();
            selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);

            Long betGroupId = 0L;

            if (selectedBetGroup.getId() == null) {
                clearActiveBetGroup();
            } else {
                noGroup = false;
                createAllowed = false;
                betGroupSelected = true;
                betGroupId = selectedBetGroup.getId();
                betGroupBets = selectedBetGroup.getBets();
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select an event=" + selectedGameEvent.getId() + ", betGroupId=" + betGroupId));
        }
    }

    public boolean eventHasNoGroup() {

        BetGroup existBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
        if (existBetGroup != null) {
            noGroup = true;
            return true;
        } else {
            noGroup = false;
            return false;
        }
    }

    private boolean verify(boolean verifyBet) {

        boolean result = true;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("gameEvent=" + selectedGameEvent + ", betGroup=" + selectedBetGroup + ", bet=" + selectedBet));

        if (selectedGameEvent == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You must select an event before saving changes."));
            result = false;
        }

        if (selectedBet == null && verifyBet) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You must select a custom bet."));
            result = false;
        }

        return result;
    }

    public void addBetToGroup() {
        if (attachMode.equals("event")) {
            if (verify(true)) {
                if (betGroupManager.addBetToGroup(selectedGameEvent, selectedBet)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet added successfully."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet group updated error."));
                }
            }
        } else {
            if (selectedBetGroup != null) {
                betGroupManager.addBetToGroup(selectedBetGroup, selectedBet);
            }
        }

    }

    public void removeBetFromGroup() {
        if (attachMode.equals("event")) {
            selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
            if (verify(false)) {
                if (selectedCustomBet != null) {
                    betGroupManager.removeBet(selectedBetGroup, selectedCustomBet);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet group updated successfully."));
            }
        } else {
            if (selectedCustomBet != null) {
                betGroupManager.removeBet(selectedBetGroup, selectedCustomBet);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet group updated successfully."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select bet to remove."));
            }

        }
    }

    public void saveChangesInGroup() {
        if (attachMode.equals("event")) {
            selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
            if (verify(false)) {
                betGroupManager.saveChanges(selectedBetGroup);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Saved."));
            }
        } else {
            if (selectedBetGroup != null) {
                betGroupManager.saveChanges(selectedBetGroup);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Saved."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please create new group before update."));
            }
        }
    }

    public void createNewGroup() {
        if (attachMode.equals("event")) {
            BetGroup newGroup = new BetGroup();
            newGroup.setGameEvent(selectedGameEvent);
            betGroupManager.createNewBetGroup(newGroup);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("New group saved for Event=" + selectedGameEvent.getId()));
        } else {
            createNewGroupToGame();
        }

    }

    public void createNewGroupToGame() {
        selectedGame = selectedGameStore.getSelectedGame();
        //находим игру по id из сессионного хранилища
        Long selectedGameId = selectedGameStore.getSelectedGameId();
        if (selectedGame == null) {
            selectedGame = em.find(Game.class, selectedGameId);
        }

        if (selectedGame != null) {
            BetGroup newGroup = new BetGroup();
            newGroup.setGame(selectedGame);
            em.persist(newGroup);
            selectedGame.getBetGroups().add(newGroup);
            em.merge(selectedGame);
            setSelectedBetGroup(newGroup);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("New group saved for Game=" + selectedGame.getId()));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select game to continue."));
        }
    }

    public void activateGroup() {
        //активация группы ставок подразумевает копирование состава группы в активные ставки
        if (attachMode.equals("event")) {
            selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
            if (verify(false)) {
                betGroupManager.activateGroup(selectedBetGroup);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Activation successful!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Activation not performed!"));
            }
        } else {
            BetGroup betGroupToActivate = selectedGameStore.getSelectedBetGroup();
            betGroupManager.activateGroup(betGroupToActivate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Activation successful for game!"));
        }
    }

    public String getAttachMode() {
        return attachMode;
    }
    public void setAttachMode(String attachMode) {
        this.attachMode = attachMode;
    }

    public Game getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }

    public void onSelectGame() {
        clearActiveBetGroup();
    }

    public void onSelectBetGroupForGameEvent(AjaxBehaviorEvent event) {

        selectedBetGroup = selectedGameStore.getSelectedBetGroup();
        setBetGroupBets(selectedBetGroup.getBets());
        noGroup = false;
        createAllowed = false;
        betGroupSelected = true;
    }

    private void clearActiveBetGroup() {
        selectedBetGroup = null;
        noGroup = true;
        createAllowed = true;
        betGroupSelected = false;
        betGroupBets = new ArrayList<>();
    }

    public void onChangeAttachMode(AjaxBehaviorEvent event) {
        if (attachMode.equals("game")) {
            createNewGroupToGameAllowed = true;
        } else {
            createNewGroupToGameAllowed = false;
        }
        clearActiveBetGroup();
    }
}
