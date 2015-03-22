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

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class LiveScoreInResultParser extends ResultParser {

    private String url = "http://www.livescore.in/ru/";

    private Logger log = Logger.getAnonymousLogger();

    @Inject
    private RedisManager<GameEvent> redisManager;

    private static String rootClass = "table-main";

    public LiveScoreInResultParser() {
        super();
    }
    public LiveScoreInResultParser(ScheduleParser parser) { super(parser); }

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

    private void parseGameStat(int commandLine, GameEvent gameEvent, WebElement root) {

        log.log(Level.INFO, "FIND ENDED GAME=" + gameEvent.getEventName() + "," + gameEvent.getEventLocation());
        String teamName = root.findElement(By.className("padl")).getText();

        if (commandLine == 0) {
            WebElement timeCell = root.findElement(By.className("time"));
            String eventTime = timeCell.getText();
            String eventStatus = root.findElement(By.className("timer")).getText();

            gameEvent.setTeam1Name(teamName);
            gameEvent.setEventTime(eventTime);
            gameEvent.setStatus(eventStatus);
            gameEvent.setDateStart(new Date());
            gameEvent.setDateEnd(new Date());

            log.log(Level.INFO, "TEAM 1=" + teamName + "," + eventTime);
            //событие отменено
            if (timeCell.getAttribute("class").contains("canceled")) {
                return;
            }

            List<String> rawScore = new ArrayList<>();
            List<Integer> scores = new ArrayList<>();

            rawScore.add(root.findElement(By.className("score-home")).getText());
            rawScore.add(root.findElement(By.className("cell_sd")).getText());
            rawScore.add(root.findElement(By.className("cell_se")).getText());
            rawScore.add(root.findElement(By.className("cell_sf")).getText());
            rawScore.add(root.findElement(By.className("cell_sg")).getText());
            rawScore.add(root.findElement(By.className("cell_sh")).getText());

            log.log(Level.INFO, "SCORE TEAM1 INFO=" + rawScore);

            for (int i = 0; i < 6; i++) {
                String score = rawScore.get(i);
                try {
                    scores.add(Integer.parseInt(score));
                } catch (NumberFormatException e) {
                    continue;
                }
            }

            gameEvent.setScores1(scores);

        } else {

            log.log(Level.INFO, "TEAM 2=" + teamName);

            List<String> rawScore = new ArrayList<>();
            List<Integer> scores = new ArrayList<>();

            rawScore.add(root.findElement(By.className("score-away")).getText());
            rawScore.add(root.findElement(By.className("cell_ta")).getText());
            rawScore.add(root.findElement(By.className("cell_tb")).getText());
            rawScore.add(root.findElement(By.className("cell_tc")).getText());
            rawScore.add(root.findElement(By.className("cell_te")).getText());
            rawScore.add(root.findElement(By.className("cell_tf")).getText());

            log.log(Level.INFO, "SCORE TEAM2 INFO=" + rawScore);

            for (int i = 0; i < 6; i++) {
                String score = rawScore.get(i);
                try {
                    scores.add(Integer.parseInt(score));
                } catch (NumberFormatException e) {
                    continue;
                }
            }

            gameEvent.setScores2(scores);
            gameEvent.setTeam2Name(teamName);
        }

    }

    private List<GameEvent> basketballParser(Game game) {
        //костыль (нужно понять почему не инжектится зависимость
        boolean closeRedis = false;

        if (redisManager == null) {
            redisManager = new RedisManager<>("localhost", 6379, "GameEvent");
            closeRedis = true;
        }

        List<GameEvent> activeGames = redisManager.getRange("GameEvent", 0, -1);

        WebDriver driver = null;

        log.log(Level.INFO, "Check ended games=>" + activeGames.size());
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
                List<WebElement> subEventRows = elem.findElements(By.className("stage-" + "finished"));
                int c = 1;
                int commandLine = 0;

                for (WebElement teamInfo: subEventRows) {
                    //parse teams
                    if (c % 2 == 0) {
                        commandLine = 1;
                    } else {
                        commandLine = 0;
                    }

                    parseGameStat(commandLine, newGameEvent, teamInfo);
                    GameEvent inMemory = searchInStorage(activeGames, newGameEvent);
                    //если игра найдена в памяти, то сделаем слепок

                    if (inMemory != null) {
                        newGameEvent.setId(inMemory.getId());
                        redisManager.set(newGameEvent);
                        shedules.add(newGameEvent);
                        log.log(Level.INFO, "SCORE RESULTS PARSED->GAME FINISHED=" + inMemory.getId() + "," + newGameEvent);
                    }

                    c++;
                }
            }

            return shedules;

        } catch (MalformedURLException | NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            return new ArrayList<>();
        } finally {
            if (driver != null) {
                driver.quit();
            }

            if (closeRedis) {
                redisManager.close();
            }
        }

    }

    public List<GameEvent> parseFor(Game game) {

        String gameName = game.getName();
        url += gameName;

        switch (gameName) {
            case "basketball":
                return basketballParser(game);
            default:
                return new ArrayList<>();
        }

    }

    public List<GameEvent> parse(Game game) {

        List<GameEvent> results = parseFor(game);

        return results;
    }
}
