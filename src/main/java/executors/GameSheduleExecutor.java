package main.java.executors;

import main.java.managers.games.GameSheduleManager;
import main.java.models.games.Game;
import main.java.managers.games.GameManager;
import main.java.models.games.GameShedule;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSheduleExecutor {
    public static void main(String[] args) throws NamingException {
        System.out.println("-----START-----");
        Map<String, Object> props = new HashMap<String, Object>();

        props.put(EJBContainer.MODULES, new File("target/classes"));

        try {
            EJBContainer ec = EJBContainer.createEJBContainer(props);
            Context ctx = ec.getContext();
            System.out.println("Context retrieved");
            GameManager gameManager = (GameManager) ctx.lookup("java:global/classes/GameManager!main.java.managers.games.GameManager");
            Game searchGame = gameManager.findByName("basketball");
            if (searchGame == null) {
                gameManager.createNewGame("basketball");
            }
            Game createdGame = gameManager.findByName("basketball");
            System.out.println(createdGame);

            System.out.println(gameManager.findAllGames());

            GameSheduleManager gameShedManager = (GameSheduleManager) ctx.lookup("java:global/classes/GameSheduleManager!main.java.managers.games.GameSheduleManager");
            List<GameShedule> sheduleList = gameShedManager.getAvailableShedules(createdGame, 2015, 2, 3);

            for (GameShedule s : sheduleList) {
                System.out.println("GAME EVENT(team1=" + s.getTeam1Name() + ", team2=" + s.getTeam2Name() +")");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
