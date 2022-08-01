package com.company.logger.purposes.strategies;

import java.io.IOException;
import java.nio.file.FileSystems;

public abstract class LogStrategy {
    
    protected final String DEFAULT_PREFIX = "log";
    protected final String EXTENSION = ".rtf";
    protected final String SEPARATOR = FileSystems.getDefault().getSeparator();
    
    protected String directory;
    protected boolean delete;
    protected String prefix;
    
    public LogStrategy() {
        
        setPrefix(DEFAULT_PREFIX);
        setDelete(true);
    }
    
    public LogStrategy(String path, boolean delete) {
    
        setDirectory(path);
        setPrefix(path);
        setDelete(delete);
    }
    
    public abstract String getPath() throws IOException;
    
    protected void setDirectory(String path) {
        
        this.directory = (path == null || !path.contains(SEPARATOR)) ?
                "" : path.substring(0, path.lastIndexOf(SEPARATOR));
    }
    
    protected void setDelete(boolean delete) {
        
        this.delete = delete;
    }
    
    protected void setPrefix(String path) {
    
    this.prefix = (path == null || !path.contains(".") || !path.contains(SEPARATOR)) ?
        DEFAULT_PREFIX : path.substring(path.lastIndexOf(SEPARATOR) + 1, path.lastIndexOf("."));
    }
}
