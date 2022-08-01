package com.company.logger.factories;

import com.company.logger.LoggeRodion;
import com.company.logger.parsers.LoggeRodionParser;
import com.company.logger.purposes.FilePurpose;
import com.company.logger.purposes.Purpose;

import java.io.File;
import java.io.IOException;

public final class LoggeRodionFactory implements LoggerFactory<LoggeRodion> {
    
    private LoggeRodionParser parser;
    
    public LoggeRodionFactory(LoggeRodionParser parser) {
        
        this.parser = parser;
    }
    
    public void setParser(LoggeRodionParser parser) {
        
        this.parser = parser;
    }
    
    public LoggeRodion defaultLogger() {
        
        return new LoggeRodion();
    }
    
    public LoggeRodion fileLogger() throws IOException {
        
        return new LoggeRodion(new FilePurpose());
    }
    
    public LoggeRodion logger(Purpose... purposes) {
        
        return new LoggeRodion(purposes);
    }
    
    public LoggeRodion logger(boolean color, boolean secret, String datePattern, boolean concurrent, boolean daemon, Purpose... purposes) {
        
        return new LoggeRodion(color, secret, datePattern, concurrent, daemon, purposes);
    }
    
    public LoggeRodion concurrentLogger(boolean daemon, Purpose...purposes) {
        
        return new LoggeRodion(true, daemon, purposes);
    }
    
    public LoggeRodion colorLogger(Purpose...purposes) {
        
        return new LoggeRodion(true, true, null, false, false, purposes);
    }
    
    public LoggeRodion logger(Purpose purposes) {
        
        return new LoggeRodion(purposes);
    }
    
    public LoggeRodion loggerFromFile(File source) throws IOException {
        
        return parser.setup(source);
    }
    
    public LoggeRodion loggerFromFile(String path) throws IOException {
        
        return loggerFromFile(new File(path));
    }
    
}
