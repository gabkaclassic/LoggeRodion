package com.company.logger.purposes;

import com.company.logger.purposes.strategies.DefaultStrategy;
import com.company.logger.purposes.strategies.LogStrategy;

import java.io.FileWriter;
import java.io.IOException;

public final class FilePurpose extends Purpose {
    
    private LogStrategy strategy;
    
    public FilePurpose() throws IOException {
        
        super();
        setStrategy(null);
        type = PurposeType.FILE;
        setWriter();
    }
    
    public FilePurpose(PurposeLevel level) throws IOException {
        
        super(level);
        setStrategy(null);
        type = PurposeType.FILE;
        setWriter();
    }
    
    public FilePurpose(PurposeLevel level, LogStrategy strategy) throws IOException {
        
        super(level);
        setStrategy(strategy);
        type = PurposeType.FILE;
        setWriter();
    }
    
    public FilePurpose(PurposeLevel level, String path) throws IOException {
    
        this(level);
        setStrategy(new DefaultStrategy(path));
        type = PurposeType.FILE;
    }
    
    public synchronized void write(String text) throws IOException {
        
        setWriter(strategy.getPath());
        super.write(text);
    }
    
    public void setWriter(String path) throws IOException {
        
        if(path != null) {
            writer = new FileWriter(path, true);
            strategy.setDirectory(path);
        }
        else
            writer = new FileWriter(strategy.getPath(), true);
    }
    
    private void setWriter() throws IOException {
    
        writer = new FileWriter(strategy.getPath(), true);
    }
    
    public String getWriter() throws IOException {
        
        return strategy.getPath();
    }
    
    public void setStrategy(LogStrategy strategy) {
        
        this.strategy = (strategy == null) ? new DefaultStrategy() : strategy;
    }
}
