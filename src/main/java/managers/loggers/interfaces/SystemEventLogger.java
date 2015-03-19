package main.java.managers.loggers.interfaces;

import javax.enterprise.inject.Default;
import javax.interceptor.InvocationContext;

@Default
public interface SystemEventLogger {

    public void log(InvocationContext ic);
}
