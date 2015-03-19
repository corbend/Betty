package main.java.managers.grabbers.parsers.nba;
import main.java.managers.grabbers.parsers.EventParser;
import main.java.managers.service.RedisManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import org.joda.time.DateTime;
import org.openqa.selenium.*;
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
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class LiveScoreInParser extends EventParser {

    public LiveScoreInParser() {
        super();
    }

    private Logger log = Logger.getLogger(LiveScoreInParser.class.getName());
    private String url = "http://www.livescore.in/ru/";
    private DateTime parsedDate;

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
                } else {
                    gameEvent.setTeam2Name(teamName);
                }

                c++;
            }
        }

        return teamInfoRows;
    }

    private void clickAndParseDateMenu(WebDriver driver) {

        WebElement dateMenuContent;

        try {
            dateMenuContent = driver.findElement(By.id("ifmenu-calendar-content"));
        } catch (org.openqa.selenium.NoSuchElementException e) {

            WebElement dateMenuHeader = driver.findElement(By.id("ifmenu-calendar")).findElement(By.tagName("a"));
            dateMenuHeader.click();

            (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {

                    List<WebElement> testElement = d.findElements(By.id("ifmenu-calendar-content"));

                    return !testElement.isEmpty();
                }
            });

            dateMenuContent = driver.findElement(By.id("ifmenu-calendar-content"));
        }

        List<WebElement> datesToWatch = dateMenuContent.findElements(By.tagName("a"));

        for (WebElement dateLink: datesToWatch) {
            String dateText = dateLink.getText();
            String normDateText = dateText.substring(0, dateText.length() - 3);
            String dateString = "";
            String monthString = "";

            try {
                dateString = normDateText.split("\\/")[0];
                monthString = normDateText.split("\\/")[1];
            } catch (ArrayIndexOutOfBoundsException e) {

                continue;
            }
            String formatedDate = parsedDate.getYear() + "-" + monthString + "-" + dateString;
            DateTime date = DateTime.parse(formatedDate);

            if (    date.getMonthOfYear() == parsedDate.getMonthOfYear() &&
                    date.getDayOfMonth() == parsedDate.getDayOfMonth()) {

                //мы и так уже находимся на текущей дате

                if (dateLink.getAttribute("class").contains("ifmenu-today")) {
                    continue;
                }

                dateLink.click();

                new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver d) {
                        try {

                            WebElement body = d.findElement(By.tagName("body"));
                            WebElement dateMenu = body.findElement(By.id("ifmenu-calendar"));
                            WebElement dateTodayItem = dateMenu.findElement(By.className("today"));
                            WebElement dateTodayLink = dateTodayItem.findElement(By.tagName("a"));

                            String dateTodayText = dateTodayLink.getText();

                            int testDay = parsedDate.getDayOfMonth();
                            String testDayString = Integer.toString(testDay);
                            int testMonth = parsedDate.getMonthOfYear();
                            String testMonthString = Integer.toString(testMonth);

                            if (testDayString.length() == 1) {
                                testDayString = "0" + testDayString;
                            }

                            if (testMonthString.length() == 1) {
                                testMonthString = "0" + testMonthString;
                            }
                            String matchText = testDayString + "/" + testMonthString;

                            return dateTodayText.substring(0, dateTodayText.length() - 3)
                                    .equals(matchText);

                        } catch (StaleElementReferenceException e) {
                            return false;
                        }
                    }
                });

                break;
            }
        }

    }

    private List<GameEvent> basketballParser(String gameName) {
        //костыль (нужно понять почему не инжектится зависимость

        redisManager = new RedisManager<>("localhost", 6379, "GameEvent");
        WebDriver driver = null;

        try {
            driver = new RemoteWebDriver(new URL("http://localhost:9515"), DesiredCapabilities.chrome());
            driver.get(url);

            (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {

                    List<WebElement> tableElements = d.findElements(By.className(rootClass));

                    return !tableElements.isEmpty();
                }
            });

            //найдем в пункте меню, нужный заданной дате пункт и сделаем щелчок мышью на нем
            //дальше нужно подождать отрисовки страницы
            if (parsedDate != null) {
                clickAndParseDateMenu(driver);
            }

            WebElement rootTable = driver.findElement(By.className(rootClass));

            List<WebElement> tableElements = rootTable.findElements(By.className("basketball"));
            List<GameEvent> shedules = new ArrayList<>();

                for (WebElement elem : tableElements) {

                    //parse header
                    WebElement header = elem.findElement(By.className("head_ab"));
                    String eventLocation = header.findElement(By.className("country_part")).getText();
                    String eventName = header.findElement(By.className("tournament_part")).getText();
                    GameEvent newGameEvent = new GameEvent();
                    newGameEvent.setEventName(eventName);
                    newGameEvent.setEventLocation(eventLocation);
                    newGameEvent.setDateStart(parsedDate.toDate());
                    newGameEvent.setDateEnd(parsedDate.toDate());

                    //parse teams
                    List<WebElement> teamInfoRows = parseGameStat("scheduled", newGameEvent, elem);
                    List<WebElement> teamLiveInfoRows = parseGameStat("live", newGameEvent, elem);
                    //пока существует только для тестирования
                    List<WebElement> teamFinishedInfoRows = parseGameStat("finished", newGameEvent, elem);
                    //TODO - parse scheduled
                    if (teamInfoRows.size() > 0 || teamLiveInfoRows.size() > 0 || teamFinishedInfoRows.size() > 0) {
                        shedules.add(newGameEvent);
                    }

                    log.log(Level.INFO, "SCHEDULED GAME EVENT PARSED->" +
                                    newGameEvent.getEventName() + ", " +
                                    newGameEvent.getEventLocation() + "," +
                                    newGameEvent.getTeam1Name() + "," +
                                    newGameEvent.getTeam2Name() + "."

                    );
                }

            redisManager.addList("GameEvent", shedules);

            log.log(Level.INFO, "Game Schedule ->" + gameName + "->PARSE=OK");

            return shedules;
        } catch (MalformedURLException | NoSuchElementException e) {
            log.log(Level.SEVERE, e.getStackTrace().toString());
            return new ArrayList<>();
        } finally {
            if (driver != null) {
                driver.quit();
            }
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

        parsedDate = new DateTime(forYear, forMonth, forDate, 0, 0, 0);
        List<GameEvent> ls = parseFor(game.getName());
        getPersistenceParser().setLastCompleteTime(DateTime.now());
        getPersistenceParser().setComplete(true);

        return ls;
    }
}
