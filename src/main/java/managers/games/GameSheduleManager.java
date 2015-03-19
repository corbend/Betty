package main.java.managers.games;

import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;


import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@Stateless
public class GameSheduleManager {

    @Resource
    private SessionContext context;

    @PersistenceContext
    private EntityManager em;

    public List<GameEvent> getAvailableShedules(Game game, int forYear, int forMonth, int forDate) {

        List<GameEvent> shedules = new ArrayList<>();

        Calendar cDate = new GregorianCalendar(forYear, forMonth, forDate);

        System.out.println("GET SHEDULE FOR DATE=" + cDate.get(Calendar.YEAR) + ", " + cDate.get(Calendar.MONTH) + ", " + cDate.get(Calendar.DAY_OF_MONTH));

        String requestYear = Integer.toString(cDate.get(Calendar.YEAR));
        String requestMonth = StringUtils.right("0" + cDate.get(Calendar.MONTH), 2);
        String requestDay = StringUtils.right("0" + cDate.get(Calendar.DAY_OF_MONTH), 2);

        return shedules;
    }

    public List<GameEvent> getAllTodaySchedules() {

        return em.createNamedQuery("GameEvent.getForPreciseDate", GameEvent.class)
                .setParameter("dateStart", new Date()).setParameter("dateEnd",
                        DateTime.now().plusDays(1).withHourOfDay(0).toDate()).getResultList();
    }

    public void createTimer(Game game, Calendar cl) {
        TimerService ts = context.getTimerService();
        ts.createTimer(cl.getTime(), game);
    }

    public List<GameEvent> getSavedShedulesForGame(Game game, Date sheduleDate) {

        List<GameEvent> gs;
        TypedQuery<GameEvent> query = em.createNamedQuery("GameEvent.getForDate", GameEvent.class);

        query.setParameter("date", sheduleDate);
        query.setParameter("gameId", game);

        gs = query.getResultList();

        return gs;
    }

    public GameEvent get(Long id) {
        return em.find(GameEvent.class, id);
    }
}

