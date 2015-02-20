package main.java.managers.games;

import main.java.models.games.Game;
import main.java.models.games.GameShedule;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import javax.interceptor.AroundInvoke;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@Stateless
public class GameSheduleManager {

    private String url = "http://espn.go.com/travel/sports/calendar/";

    @Resource
    private SessionContext context;

    @PersistenceContext
    private EntityManager em;

    private List<WebElement> parseWithSelenium(Game game, int forYear, int forMonth, int forDate) {
        List<GameShedule> shedules = new ArrayList<>();
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new FirefoxDriver();
        // And now use this to visit Google
        driver.get(url);
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        WebElement element = driver.findElement(By.className("nbaList"));

        System.out.println("NBA list: " + element);

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {

                WebElement element = d.findElement(By.className("nbaList"));
                List<WebElement> lst = element.findElements(By.className("row"));

                return !lst.isEmpty();
            }
        });

        List<WebElement> lst = element.findElements(By.className("row"));

        WebElement e = lst.get(0);

        System.out.println("Shedule is gained: " + e.getAttribute("class"));

        for (WebElement sheduleLi : lst) {
            GameShedule newShedule = new GameShedule();
            //newShedule.setGame(game);

            List<GameShedule> ls = game.getGameShedules();
            ls.add(newShedule);
            game.setGameShedules(ls);

            List<WebElement> teamsName = sheduleLi.findElements(By.className("team-logo-small"));
            System.out.println("TEAMS=" + teamsName);
            String team1Name = teamsName.get(0).getAttribute("title");
            String team2Name = teamsName.get(1).getAttribute("title");

            newShedule.setTeam1Name(team1Name);
            newShedule.setTeam2Name(team2Name);
            newShedule.setEventName(sheduleLi.findElement(By.cssSelector("h3 a")).getText());
            newShedule.setEventLocation(sheduleLi.findElement(By.className("location")).getText());

            String timeString = sheduleLi.findElement(By.className("game-time")).getText();
            newShedule.setEventTime(timeString);
            int year = forYear;
            int month = forMonth;
            int date = forDate;

            newShedule.setDateStart(new GregorianCalendar(year, month - 1, date).getTime());
            newShedule.setDateEnd(new GregorianCalendar(year, month - 1, date).getTime());

            em.persist(newShedule);

            System.out.println("AFTER PERSIST=" + newShedule);
            shedules.add(newShedule);
        }

        //Close the browser
        driver.quit();

        return lst;
    }

    public List<GameShedule> getAvailableShedules(Game game, int forYear, int forMonth, int forDate) {

        List<GameShedule> shedules = new ArrayList<>();

        Calendar cDate = new GregorianCalendar(forYear, forMonth, forDate);

        System.out.println("GET SHEDULE FOR DATE=" + cDate.get(Calendar.YEAR) + ", " + cDate.get(Calendar.MONTH) + ", " + cDate.get(Calendar.DAY_OF_MONTH));

        String requestYear = Integer.toString(cDate.get(Calendar.YEAR));
        String requestMonth = StringUtils.right("0" + cDate.get(Calendar.MONTH), 2);
        String requestDay = StringUtils.right("0" + cDate.get(Calendar.DAY_OF_MONTH), 2);

        url += "?date=" + requestYear + requestMonth + requestDay;
        url += "&type=list";

        System.out.println("PARSE URL=" + url);
        parseWithSelenium(game, forYear, forMonth, forDate);

        return shedules;
    }

    public void createTimer(Game game, Calendar cl) {
        TimerService ts = context.getTimerService();
        ts.createTimer(cl.getTime(), game);
    }

    @Timeout
    public void onShedule(Timer timer) {

        System.out.println("TIMER EXPIRED->");
        Calendar cl = new GregorianCalendar();
        System.out.println("GET SHEDULE FOR->" + cl);
        getAvailableShedules((Game) timer.getInfo(), cl.get(Calendar.MONTH), cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.YEAR));
    }

    public List<GameShedule> getSavedShedulesForGame(Game game, Date sheduleDate) {

        List<GameShedule> gs;
        TypedQuery<GameShedule> query = em.createNamedQuery("GameShedule.getForDate", GameShedule.class);

        query.setParameter("date", sheduleDate);
        query.setParameter("gameId", game);

        gs = query.getResultList();

        return gs;
    }
}

