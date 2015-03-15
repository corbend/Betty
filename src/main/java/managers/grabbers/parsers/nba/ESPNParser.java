package main.java.managers.grabbers.parsers.nba;

import main.java.managers.grabbers.parsers.EventParser;
import main.java.managers.service.RedisManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Stateless
public class ESPNParser extends EventParser {

    String url = "http://espn.go.com/travel/sports/calendar/?date={{date}}&type=list";

    public ESPNParser() {
        super();
    }

    @PersistenceContext
    private EntityManager em;

    private RedisManager<ScheduleParser> redisManager;

    ESPNParser(ScheduleParser persistenceParser) {
        super(persistenceParser);
    }

    public List<GameEvent> parse(Game game, int forYear, int forMonth, int forDate) {

        Boolean status = false;

        url += "?date=" + forYear + forMonth + forDate;
        url += "&type=list";

        List<GameEvent> shedules = new ArrayList<>();
        WebDriver driver = new FirefoxDriver();
        driver.get(url);

        WebElement element = driver.findElement(By.className("nbaList"));

        System.out.println("NBA list: " + element);

        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {

                WebElement element = d.findElement(By.className("nbaList"));
                List<WebElement> lst = element.findElements(By.className("row"));

                return !lst.isEmpty();
            }
        });

        try {
            List<WebElement> lst = element.findElements(By.className("row"));

            if (lst == null) {
                throw new Error("Root element not found!");
            }

            WebElement e = lst.get(0);

            System.out.println("Shedule is gained: " + e.getAttribute("class"));

            for (WebElement sheduleLi : lst) {
                GameEvent newShedule = new GameEvent();
                //newShedule.setGame(game);

                List<GameEvent> ls = game.getGameEvents();
                ls.add(newShedule);
                game.setGameEvents(ls);

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

                newShedule.setDateStart(new GregorianCalendar(forYear, forMonth - 1, forDate).getTime());
                newShedule.setDateEnd(new GregorianCalendar(forYear, forMonth - 1, forDate).getTime());

                em.persist(newShedule);
                shedules.add(newShedule);

                status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Close the browser
        driver.quit();

        List<ScheduleParser> l = new ArrayList<>();
        ScheduleParser parser = getPersistenceParser();
        parser.setStatus(status);
        //parser.setLastCompleteTime(DateTime.now());
        l.add(parser);

        redisManager.addList("Parsers", l);

        return shedules;
    }
}
