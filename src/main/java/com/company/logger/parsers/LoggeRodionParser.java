package com.company.logger.parsers;

import com.company.logger.LoggeRodion;
import com.company.logger.purposes.ConsolePurpose;
import com.company.logger.purposes.FilePurpose;
import com.company.logger.purposes.Purpose;
import com.company.logger.purposes.PurposeLevel;
import com.company.logger.purposes.strategies.DefaultStrategy;
import com.company.logger.purposes.strategies.LogStrategy;
import com.company.logger.purposes.strategies.SizeStrategy;
import com.company.logger.purposes.strategies.TimeStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoggeRodionParser extends LoggerParser<LoggeRodion> {
    
    protected LoggeRodion loggerFromJson(BufferedReader reader) throws IOException {
    
        var mapper = new ObjectMapper();
    
        return mapper.readValue(reader, LoggeRodion.class);
    }
    
    protected LoggeRodion loggerFromJson(String path) throws IOException {
        
        var mapper = new ObjectMapper();
        
        return mapper.readValue(new File(path), LoggeRodion.class);
    }
    
    protected LoggeRodion loggerFromXml(BufferedReader reader) throws IOException {
        
        var mapper = new XmlMapper();
    
        return mapper.readValue(reader, LoggeRodion.class);
    }
    
    protected LoggeRodion loggerFromXml(String path) throws IOException {
        
        var mapper = new XmlMapper();
        
        return mapper.readValue(new File(path), LoggeRodion.class);
    }
    
    protected LoggeRodion loggerFromProperties(BufferedReader reader) throws IOException {
        
        var props = new Properties();
        LoggeRodion logger;
        props.load(reader);
    
        var purposeList = new ArrayList<>();
        purposeList.add(null);
        purposeList.add(null);
        
        if(props.containsKey("console.level")) {
            PurposeLevel level = PurposeLevel.valueOf((String) props.get("console.level"));
            purposeList.set(0, new ConsolePurpose(level));
        }
        if(props.containsKey("file.level")) {
            PurposeLevel level = PurposeLevel.valueOf((String) props.get("file.level"));
            String strategyType = (String)props.get("file.strategy");
            String path = (String)props.get("file.path");
            boolean delete = !props.containsKey("file.strategy.delete")
                    || Boolean.parseBoolean((String) props.get("file.strategy.delete"));
            
            LogStrategy strategy = switch (strategyType) {
                
                case "SIZE" -> {
    
                    String condition = (String)props.get("file.strategy.condition");
                    
                    yield new SizeStrategy(path, delete, condition);
                }
                case "TIME" -> {
    
                    Map<Integer, Integer> time = Map.of(
                            Calendar.SECOND, props.containsKey("file.strategy.seconds") ?
                                    Integer.parseInt((String)props.get("file.strategy.seconds")) : 0,
                            Calendar.MINUTE, props.containsKey("file.strategy.minutes") ?
                                    Integer.parseInt((String)props.get("file.strategy.minutes")) : 0,
                            Calendar.HOUR, props.containsKey("file.strategy.hours") ?
                                    Integer.parseInt((String)props.get("file.strategy.hours")) : 0,
                            Calendar.DAY_OF_MONTH, props.containsKey("file.strategy.days") ?
                                    Integer.parseInt((String)props.get("file.strategy.days")) : 0,
                            Calendar.MONTH, props.containsKey("file.strategy.months") ?
                                    Integer.parseInt((String)props.get("file.strategy.months")) : 0
                    );
    
                    yield new TimeStrategy(path, delete, time);
                }
                default -> new DefaultStrategy(path);
            };
            
            var file = new FilePurpose(level, strategy);
            purposeList.set(1, file);
        }
        Purpose[] purposes = purposeList.stream().filter(Objects::nonNull).toArray(Purpose[]::new);
        
        logger = new LoggeRodion(
                Boolean.parseBoolean((String) props.get("color")),
                Boolean.parseBoolean((String) props.get("secret")),
                (String)props.get("datePattern"),
                Boolean.parseBoolean((String) props.get("concurrent")),
                Boolean.parseBoolean((String) props.get("daemon")),
                purposes
        );
        
        return logger;
    }
}
