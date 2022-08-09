package com.company.logger.parsers;

import com.company.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public abstract class LoggerParser<T extends Logger> {
    
    public Map<String, T> config(File source) throws IOException {
        
        String filename = source.getName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        BufferedReader reader = new BufferedReader(new FileReader(source));

        switch (extension) {
            case "xml" -> {
        
                return loggersFromXml(reader);
            }
            case "json" -> {
        
                return loggersFromJson(reader);
            }
            case "properties" -> {
                return loggersFromProperties(reader);
            }
            default -> throw new IllegalStateException("Unexpected value: " + extension);
        }
    }
    
    public Map<String, T> config(String path) throws IOException {
        
        return config(new File(path));
    }
    
    protected abstract Map<String, T> loggersFromJson(BufferedReader reader) throws IOException;
    
    protected abstract Map<String, T> loggersFromJson(String path) throws IOException;
    
    protected abstract Map<String, T> loggersFromXml(BufferedReader reader) throws IOException;
    
    protected abstract Map<String, T> loggersFromXml(String path) throws IOException;
    
    protected abstract Map<String, T> loggersFromProperties(BufferedReader reader) throws IOException;
}
