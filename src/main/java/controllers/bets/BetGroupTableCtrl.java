package main.java.controllers.bets;

import main.java.managers.bets.BetGroupManager;
import main.java.models.bets.BetGroup;
import main.java.models.bets.CustomBet;
import main.java.models.bets.LiveBet;
import main.java.models.games.GameEvent;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class BetGroupTableCtrl implements Serializable {

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

        selectedGameEvent = (GameEvent) event.getObject();
        selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);

        Long betGroupId = 0L;

        if (selectedBetGroup.getId() == null) {
            noGroup = true;
            createAllowed = true;
            betGroupSelected = false;
            betGroupBets = new ArrayList<>();
        } else {
            noGroup = false;
            createAllowed = false;
            betGroupSelected = true;
            betGroupId = selectedBetGroup.getId();
            betGroupBets = selectedBetGroup.getBets();
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select an event="+ selectedGameEvent.getId() + ", betGroupId=" + betGroupId));
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

        if (verify(true)) {
            if (betGroupManager.addBetToGroup(selectedGameEvent, selectedBet)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet added successfully."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet group updated error."));
            }
        }
    }

    public void removeBetFromGroup() {
        selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
        if (verify(false)) {
            if (selectedCustomBet != null) {
                betGroupManager.removeBet(selectedBetGroup, selectedCustomBet);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bet group updated successfully."));
        }
    }

    public void saveChangesInGroup() {
        selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
        if (verify(false)) {
            betGroupManager.saveChanges(selectedBetGroup);
        }
    }

    public void createNewGroup() {
        BetGroup newGroup = new BetGroup();
        newGroup.setGameEvent(selectedGameEvent);
        betGroupManager.createNewBetGroup(newGroup);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("New group saved for Event=" + selectedGameEvent.getId()));
    }

    public void activateGroup() {
        //активация группы ставок подразумевает копирование состава группы в активные ставки
        selectedBetGroup = betGroupManager.getBetGroupForEvent(selectedGameEvent);
        if (verify(false)) {
            betGroupManager.activateGroup(selectedBetGroup);
        }
    }
}
