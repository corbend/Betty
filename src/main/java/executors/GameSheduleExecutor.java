package main.java.executors;

import main.java.models.games.Game;
import main.java.managers.games.GameManager;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.util.HashMap;
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
