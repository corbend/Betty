package main.java.managers.games;

import main.java.models.games.Game;
import main.java.models.games.GameShedule;
//import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;


@Singleton
public class ShedulePlanner {

   // private Logger logger;

    @PersistenceContext
    private EntityManager em;

    @EJB
    private GameManager gameManager;

    @EJB
    private GameSheduleManager sheduleManager;

    //каждый день формируем расписание игр
    @Schedule(second="*/10", persistent = false)
    public void putShedulesOnPlan() {

        Calendar curDate = new GregorianCalendar();

        //получаем список активных игровых типов, для которых необходимо сформировать расписание
        List<Game> activeGames = gameManager.getAllActiveGames();
        System.out.println("GET ACTIVE GAMES->");
        System.out.println(activeGames);

        for (Game g : activeGames) {
            Number gameShedulePeriod = g.getSheduleWindow();
            Game.TimeUnit gamePeriodTimeUnit = g.getPeriodTimeUnit();
            int sheduleEvery = g.getSheduleEvery();

            int tm = ((int) gameShedulePeriod * Game.TimeUnit.getSeconds(gamePeriodTimeUnit)) / sheduleEvery;

            System.out.println("CALCULATE TICKS->");
            System.out.println(tm);

            for (int i = 0; i < tm; i++) {
                Calendar forDate = new GregorianCalendar();
                forDate.roll(Calendar.SECOND, i * Game.TimeUnit.getSeconds(gamePeriodTimeUnit));
                System.out.println("CREATE TIMER FOR GAME->");
                System.out.println(g.getName());
                sheduleManager.createTimer(g, forDate);
            }

        }

    }
}
