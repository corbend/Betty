package main.java.managers.service;

import main.java.utils.ProcessWatcher;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import java.io.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Startup
@Singleton
public class WebDriverLauncher {

    private static final String filepathUnix = "usr/local/chromedriver32";
    private static final String filenameUnix = "chromedriver32";
    private static final String filepathWindows = "C:\\JavaProjects\\chromedriver_win32\\";
    private static final String filenameWindows = "chromedriver.exe";

    private Logger logger = Logger.getLogger(WebDriverLauncher.class.getName());

    @Resource
    private SessionContext ctx;

    @Asynchronous
    public Future<Integer> runWebdriver() throws IOException, InterruptedException {
        ProcessWatcher prunner;
        if (ProcessWatcher.isWindows()) {
            prunner = new ProcessWatcher(filepathWindows, filenameWindows, logger);
        } else {
            prunner = new ProcessWatcher(filepathUnix, filenameUnix, logger);
        }
        prunner.runProcess();
        return new AsyncResult<>(0);
    }
}
