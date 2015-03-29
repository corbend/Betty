package main.java.managers.bets;

import main.java.managers.games.GameSheduleManager;
import main.java.models.bets.BetGroup;
import main.java.models.bets.CustomBet;
import main.java.models.bets.LiveBet;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BetGroupManager {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private GameSheduleManager gameEventManager;

    public List<BetGroup> getAllBetGroups() {

        return em.createNamedQuery("BetGroup.findAll", BetGroup.class).getResultList();
    }

    public void createNewBetGroup(BetGroup bgroup) {

        em.persist(bgroup);
    }

    public BetGroup getBetGroupForEvent(GameEvent gameEvent) {
        BetGroup betGroup = new BetGroup();

        try {
            return em.createNamedQuery("BetGroup.getByEvent", BetGroup.class).setParameter("event", gameEvent).getSingleResult();
        } catch (NoResultException e) {
            return betGroup;
        }
    }

    public void saveChanges(BetGroup betGroup) {

        em.merge(betGroup);
    }

    public boolean addBetToGroup(GameEvent gameEvent, CustomBet selectedBet) {
        BetGroup betGroup = getBetGroupForEvent(gameEvent);

        if (betGroup.getId() != null) {
            //List<CustomBet> bets = betGroup.getBets();
            em.flush();
            if (!betGroup.getBets().contains(selectedBet)) {
                betGroup.getBets().add(selectedBet);
            }
            //betGroup.setBets(bets);
            if (!selectedBet.getBetGroups().contains(betGroup)) {
                selectedBet.getBetGroups().add(betGroup);
            }
            em.merge(betGroup);
            em.merge(selectedBet);
            em.flush();
            return true;
        }

        return false;
    }

    public boolean addBetToGroup(BetGroup betGroup, CustomBet selectedBet) {

        if (betGroup.getId() != null) {
            em.flush();
            if (!betGroup.getBets().contains(selectedBet)) {
                betGroup.getBets().add(selectedBet);
            }
            if (!selectedBet.getBetGroups().contains(betGroup)) {
                selectedBet.getBetGroups().add(betGroup);
            }
            em.merge(betGroup);
            em.merge(selectedBet);
            em.flush();
            return true;
        }

        return false;
    }

    public int activateGroup(BetGroup group) {
        int counter = 0;
        List<CustomBet> betsToActivate = group.getBets();

        //активация для конкретного игрового события
        if (group.getGame() == null) {
            List<LiveBet> liveBets = new ArrayList<>();
            GameEvent gameEvent = group.getGameEvent();
            gameEvent.setActivateBetTypes(gameEvent.getLiveBets());
            for (CustomBet bet : betsToActivate) {
                LiveBet liveBet = new LiveBet();
                liveBet.setType(bet.getBetTypeString());
                liveBet.setCoefficient(bet.getCoefficient());
                liveBet.setGameEvent(gameEvent);
                em.persist(liveBet);
                liveBets.add(liveBet);
                counter++;
            }
            gameEvent.setLiveBets(liveBets);
            em.merge(gameEvent);

        } else {
            //активация для выбранного типа игры (для всех событий)
            List<GameEvent> allEventForGameToday = gameEventManager.getAllTodaySchedules();

            for (GameEvent gameEvent: allEventForGameToday) {
                List<LiveBet> liveBets = new ArrayList<>();
                gameEvent.setActivateBetTypes(gameEvent.getLiveBets());
                if (gameEvent.getGame().getId().equals(group.getGame().getId())) {
                    List<CustomBet> bets = group.getBets();
                    for (CustomBet bet : bets) {
                        if (!gameEvent.getActivateBetTypes().contains(bet.getBetTypeString())) {
                            LiveBet liveBet = new LiveBet();
                            liveBet.setType(bet.getBetTypeString());
                            liveBet.setCoefficient(bet.getCoefficient());
                            liveBet.setGameEvent(gameEvent);
                            em.persist(liveBet);
                            liveBets.add(liveBet);
                            counter++;
                        }
                    }
                    gameEvent.setLiveBets(liveBets);
                }
            }
        }

        return counter;
    }

    public boolean removeBet(BetGroup group, CustomBet betToRemove) {
        List<CustomBet> bets = group.getBets();
        bets.remove(betToRemove);
        betToRemove.getBetGroups().remove(group);
        em.merge(group);
        em.merge(betToRemove);
        return true;
    }
}
