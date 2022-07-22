package com.company.logger;

public interface Logger {
    
    void trace(String log);
    
    void info(String log);
    
    void debug(String log);
    
    void warn(String log);
    
    void error(String log);
    
    void fatal(String log);
}
