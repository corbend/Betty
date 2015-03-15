package main.java.managers.bets;

import main.java.models.bets.BetGroup;
import main.java.models.bets.CustomBet;
import main.java.models.bets.LiveBet;
import main.java.models.games.GameEvent;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class BetGroupManager {

    @PersistenceContext
    private EntityManager em;

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

    public boolean activateGroup(BetGroup group) {

        List<CustomBet> betsToActivate = group.getBets();

        for (CustomBet bet: betsToActivate) {
            LiveBet liveBet = new LiveBet();
            liveBet.setType(bet.getBetTypeString());
            liveBet.setCoefficient(bet.getCoefficient());
            liveBet.setGameEvent(group.getGameEvent());
            em.persist(liveBet);
        }

        return true;
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
