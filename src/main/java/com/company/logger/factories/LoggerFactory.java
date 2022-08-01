package com.company.logger.factories;

import com.company.logger.Logger;
import com.company.logger.purposes.Purpose;

import java.io.File;
import java.io.IOException;

public interface LoggerFactory<T extends Logger> {
    
     T defaultLogger();
    
     T logger(Purpose... purposes);
    
     T logger(boolean color, boolean secret, String datePattern, boolean concurrent, boolean daemon, Purpose... purposes);
    
     T logger(Purpose purposes);
    
     T loggerFromFile(File source) throws IOException;
    
     T loggerFromFile(String path) throws IOException;
}
