package main.java.managers.bets;

import main.java.models.bets.LiveBet;
import main.java.models.games.GameEvent;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class LiveBetsManager {

    @PersistenceContext
    private EntityManager em;

    public List<LiveBet> getBetsForEvent(GameEvent gameEvent) {

        GameEvent updatedGameEvent = em.find(GameEvent.class, gameEvent.getId());
        return em.createNamedQuery("LiveBet.findBetsForEvent", LiveBet.class)
                .setParameter("gameEvent", updatedGameEvent).getResultList();
    }

    public LiveBet get(Long id) {
        return em.find(LiveBet.class, id);
    }
}
