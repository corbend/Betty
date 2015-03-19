package main.java.managers.grabbers.parsers.nba;

import main.java.managers.grabbers.parsers.ResultParser;
import main.java.managers.service.RedisManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;
import main.java.models.sys.ScheduleParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiveScoreInResultParser extends ResultParser {

    private String url = "http://www.livescore.in/ru/";

    private Logger log = Logger.getAnonymousLogger();

    @Inject
    private RedisManager<GameEvent> redisManager;

    private static String rootClass = "table-main";

    public LiveScoreInResultParser(ScheduleParser parser) {

        super(parser);
        if (redisManager == null) {
            redisManager = new RedisManager<>("127.0.0.1", 6379, "GameEvent");
        }

    }

    private GameEvent searchInStorage(List<GameEvent> activeGames,
                                            GameEvent checkGame) {
        String eventName = checkGame.getEventName();
        String location = checkGame.getEventLocation();
        String time = checkGame.getEventTime();
        String team1Name = checkGame.getTeam1Name();
        String team2Name = checkGame.getTeam2Name();

        GameEvent result = null;

        for (GameEvent gameEvent: activeGames) {

            if (gameEvent.getEventLocation().equals(location) &&
                    gameEvent.getEventName().equals(eventName) &&
                    gameEvent.getEventTime().equals(time) &&
                    gameEvent.getTeam1Name().equals(team1Name) &&
                    gameEvent.getTeam2Name().equals(team2Name)) {

                result = gameEvent;
            }
        }

        return result;
    }

    private List<WebElement> parseGameStat(String gameStatus, GameEvent gameEvent, WebElement root) {

        List<WebElement> teamInfoRows = new ArrayList<>();
        if (gameStatus.equals("finished")) {

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

                    List<Integer> scores = new ArrayList<>();

                    String scoreTotal = teamInfo.findElement(By.className("score-home")).getText();
                    String score1 = teamInfo.findElement(By.className("cell_sd")).getText();
                    String score2 = teamInfo.findElement(By.className("cell_se")).getText();
                    String score3 = teamInfo.findElement(By.className("cell_sf")).getText();
                    String score4 = teamInfo.findElement(By.className("cell_sg")).getText();
                    String scoreOvertime = teamInfo.findElement(By.className("cell_sh")).getText();

                    scores.add(Integer.parseInt(scoreTotal));
                    scores.add(Integer.parseInt(score1));
                    scores.add(Integer.parseInt(score2));
                    scores.add(Integer.parseInt(score3));
                    scores.add(Integer.parseInt(score4));
                    scores.add(Integer.parseInt(scoreOvertime));
                    gameEvent.setScores1(scores);

                } else {

                    String scoreTotal = teamInfo.findElement(By.className("score-away")).getText();
                    String score1 = teamInfo.findElement(By.className("cell_ta")).getText();
                    String score2 = teamInfo.findElement(By.className("cell_tb")).getText();
                    String score3 = teamInfo.findElement(By.className("cell_tc")).getText();
                    String score4 = teamInfo.findElement(By.className("cell_te")).getText();
                    String scoreOvertime = teamInfo.findElement(By.className("cell_sh")).getText();

                    List<Integer> scores = new ArrayList<>();

                    scores.add(Integer.parseInt(scoreTotal));
                    scores.add(Integer.parseInt(score1));
                    scores.add(Integer.parseInt(score2));
                    scores.add(Integer.parseInt(score3));
                    scores.add(Integer.parseInt(score4));
                    scores.add(Integer.parseInt(scoreOvertime));

                    gameEvent.setScores2(scores);
                    gameEvent.setTeam2Name(teamName);
                }

                c++;
            }
        }

        return teamInfoRows;
    }

    private List<GameEvent> basketballParser(String gameName) {
        //костыль (нужно понять почему не инжектится зависимость

        redisManager = new RedisManager<>("localhost", 6379, "GameEvent");

        List<GameEvent> activeGames = redisManager.getRange("GameEvent", 0, -1);

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
                List<WebElement> teamInfoRows = parseGameStat("finished", newGameEvent, elem);

                GameEvent inMemory = searchInStorage(activeGames, newGameEvent);
                //если игра найдена в памяти, то сделаем слепок
                if (inMemory == null) {
                    log.log(Level.INFO, "SCORE RESULTS PARSED->GAME FULLY EXPIRED=" + newGameEvent);
                    return shedules;
                } else {
                    redisManager.set(newGameEvent);
                    if (teamInfoRows.size() > 0) {
                        shedules.add(newGameEvent);
                    }
                    log.log(Level.INFO, "SCORE RESULTS PARSED->GAME FINISHED=" + newGameEvent);
                }

            }

            return shedules;

        } catch (MalformedURLException | NoSuchElementException e) {
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

    public List<GameEvent> parse(Game game, int forYear, int forMonth, int forDays) {

        List<GameEvent> results = parseFor("basketball");

        return results;
    }
}
