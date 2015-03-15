package main.java.managers.grabbers.parsers.nba;
import main.java.managers.grabbers.parsers.EventParser;
import main.java.managers.service.RedisManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class LiveScoreInParser extends EventParser {

    public LiveScoreInParser() {
        super();
    }

    @PersistenceContext
    private EntityManager em;

    private Logger log = Logger.getLogger(LiveScoreInParser.class.getName());
    private String url = "http://www.livescore.in/ru/";

    @Inject
    private RedisManager<GameEvent> redisManager;
    private static String rootClass = "table-main";

    public LiveScoreInParser(ScheduleParser parser) {
        super(parser);
    }

    private List<WebElement> parseGameStat(String gameStatus, GameEvent gameEvent, WebElement root) {

        List<WebElement> teamInfoRows = new ArrayList<>();
        if (gameStatus.equals("live") || gameStatus.equals("scheduled")) {

            teamInfoRows = root.findElements(By.className("stage-" + gameStatus));
            int c = 0;

            for (WebElement teamInfo: teamInfoRows) {
                String teamName = teamInfo.findElement(By.className("padl")).getText();
                if (c == 0) {
                    String eventTime = teamInfo.findElement(By.className("time")).getText();
                    String eventStatus = teamInfo.findElement(By.className("timer")).getText();
                    gameEvent.setTeam1Name(teamName);
                    gameEvent.setEventTime(eventTime);
                    gameEvent.setStatus(eventStatus);
                    gameEvent.setDateStart(new Date());
                    gameEvent.setDateEnd(new Date());
                } else {
                    gameEvent.setTeam2Name(teamName);
                }

                if (gameEvent == null) {
                    log.log(Level.SEVERE, "No game event provided!");
                }

                c++;
            }
        }

        return teamInfoRows;
    }

    private List<GameEvent> basketballParser(String gameName) {
        //костыль (нужно понять почему не инжектится зависимость

        redisManager = new RedisManager<>("localhost", 6379, "GameEvent");

        try {
            WebDriver driver = new RemoteWebDriver(new URL("http://localhost:9515"), DesiredCapabilities.chrome());
            driver.get(url);

            (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {

                    List<WebElement> tableElements = d.findElements(By.className(rootClass));

                    return !tableElements.isEmpty();
                }
            });
            WebElement rootTable = driver.findElement(By.className(rootClass));
            List<WebElement> tableElements = rootTable.findElements(By.className("basketball"));
            List<GameEvent> shedules = new ArrayList<>();

            for (WebElement elem: tableElements) {

                //parse header
                WebElement header = elem.findElement(By.className("head_ab"));
                String eventLocation = header.findElement(By.className("country_part")).getText();
                String eventName = header.findElement(By.className("tournament_part")).getText();
                GameEvent newGameEvent = new GameEvent();
                newGameEvent.setEventName(eventName);
                newGameEvent.setEventLocation(eventLocation);
                //parse teams
                List<WebElement> teamInfoRows = parseGameStat("live", newGameEvent, elem);
                //TODO - parse scheduled
                if (teamInfoRows.size() > 0) {
                    shedules.add(newGameEvent);
                }
            }

            redisManager.addList("GameEvents", shedules);

            log.log(Level.INFO, "Game Schedule ->" + gameName + "->PARSE=OK");
            return shedules;
        } catch (MalformedURLException | NoSuchElementException e) {
            log.log(Level.SEVERE, e.getStackTrace().toString());
            return new ArrayList<>();
        }

    }

    public List<GameEvent> parseFor(String gameName) {

        url += gameName;

        switch (gameName) {
            case "basketball":
                return basketballParser(gameName);
            default:
                return new ArrayList<>();
        }

    }

    public List<GameEvent> parse(Game game, int forYear, int forMonth, int forDate) {

        List<GameEvent> ls = parseFor(game.getName());

        return ls;
    }
}
