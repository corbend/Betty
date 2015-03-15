package main.java.managers.loggers.interfaces;

import javax.enterprise.inject.Default;

@Default
public interface SystemEventLogger {

    public void log();
}
