package com.company.logger.purposes.strategies;

import java.io.File;
import java.io.IOException;

public final class DefaultStrategy extends LogStrategy {
    
    public DefaultStrategy() {
        
        super();
    }
    
    public DefaultStrategy(String path) {
        
        super(path, true);
    }
    
    public synchronized String getPath() throws IOException {
        
        var path = (directory != null) ?
                directory + SEPARATOR + prefix + EXTENSION : prefix + EXTENSION;
        File file = new File(path);
        
        if(!file.exists())
            file.createNewFile();
        
        return path;
    }
}
