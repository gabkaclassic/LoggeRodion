package com.company.logger.parsers;

import com.company.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public abstract class LoggerParser<T extends Logger> {
    
        public T setup(File source) throws IOException {
            
            String filename = source.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            BufferedReader reader = new BufferedReader(new FileReader(source));
    
            switch (extension) {
                case "xml" -> {
            
                    return loggerFromXml(reader);
                }
                case "json" -> {
            
                    return loggerFromJson(reader);
                }
                case "properties" -> {
                    return loggerFromProperties(reader);
                }
                default -> throw new IllegalStateException("Unexpected value: " + extension);
            }
        }
    
    public T setup(String path) throws IOException {
        
        return setup(new File(path));
    }
        
        protected abstract T loggerFromJson(BufferedReader reader) throws IOException;
        
        protected abstract T loggerFromXml(BufferedReader reader) throws IOException;
    
        protected abstract T loggerFromJson(String path) throws IOException;
        
        protected abstract T loggerFromXml(String path) throws IOException;
        
        protected abstract T loggerFromProperties(BufferedReader reader) throws IOException;
}
