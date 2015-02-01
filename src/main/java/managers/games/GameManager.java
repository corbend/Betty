package main.java.managers.games;

//import managers.grabbers.parsers.EventParser;
import main.java.models.games.Game;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
@LocalBean
public class GameManager {

    @PersistenceContext(unitName = "bettyPU")
    private EntityManager em;

    public List<Game> findAllGames() {

        TypedQuery<Game> query = em.createNamedQuery("Game.findAll", Game.class);
        return query.getResultList();
    }

    public Game findByName(String name) {

        TypedQuery<Game> query = em.createNamedQuery("Game.findByName", Game.class);
        query.setParameter("gameName", name);
        Game g = null;
        try {
            g = query.getSingleResult();
            System.out.println("GAME=");
            System.out.println(g);
        } catch(NoResultException e) {
            System.out.println("NO games with name " + name);
        }
        return g;
    }

    public Game createNewGame(String gameName) {

        Game g = new Game();
        g.setName(gameName);

        em.persist(g);
        return g;
    }

    //public void setAvailableParser(Game game, EventParser parser) {
//        game.setParser();
//    }
}