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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LoggeRodionParser extends LoggerParser<LoggeRodion> {
    
    
    private static final TypeReference<HashMap<String, LoggeRodion>> typeReference;
    private static final XmlMapper xmlMapper;
    private static final ObjectMapper objectMapper;
    
    static {
        xmlMapper = new XmlMapper();
        objectMapper = new ObjectMapper();
        typeReference = new TypeReference<>() {};
    }
    
    public Map<String, LoggeRodion> loggersFromJson(BufferedReader reader) throws IOException {
        
        return objectMapper.readValue(reader, typeReference);
    }
    
    public Map<String, LoggeRodion> loggersFromJson(String path) throws IOException {
        
        return objectMapper.readValue(new File(path), typeReference);
    }
    
    public Map<String, LoggeRodion> loggersFromXml(BufferedReader reader) throws IOException {
        
        return xmlMapper.readValue(reader, typeReference);
    }
    
    public Map<String, LoggeRodion> loggersFromXml(String path) throws IOException {
        
        return xmlMapper.readValue(new File(path), typeReference);
    }
    
    public Map<String, LoggeRodion> loggersFromProperties(BufferedReader reader) throws IOException {
        
        var props = new Properties();
        props.load(reader);
        var config = props.keySet().stream().collect(Collectors.toMap(Object::toString, k->props.get(k).toString()));
        var loggers = config.keySet().stream().map(s -> s.substring(0, s.indexOf("."))).collect(Collectors.toMap(s->s, LoggeRodion::new));
        
        for(var name: loggers.keySet()) {
    
            if (config.containsKey(name + ".putPoint"))
                loggers.get(name).setPutPoint(config.get(name + ".putPoint"));
            loggers.get(name).setPurposes(getPurposes(name, config));
        }
        
        return loggers;
    }
    
    private Set<Purpose> getPurposes(String name, Map<String, String> config) throws IOException {
    
    
        var file = (config.get(name + ".type").contains("FILE")) ?
                new FilePurpose() : null;
        var console = (config.get(name + ".type").toUpperCase().contains("CONSOLE"))
                ? new ConsolePurpose() : null;
    
        if(file != null) {
    
            LogStrategy strategy;
            strategy = getStrategy(name, config);
            var level = config.containsKey("file.level") ?
                    PurposeLevel.valueOf(config.get("file.level")) : PurposeLevel.WARN;
            
            file.setStrategy(strategy);
            file.setLevel(level);
        }
        if(console != null) {
            
            var level = config.containsKey("console.level") ?
                    PurposeLevel.valueOf(config.get("console.level")) : PurposeLevel.DEBUG;
            console.setLevel(level);
        }
    
        return Stream.of(console, file).filter(Objects::nonNull).collect(Collectors.toSet());
    }
    
    private LogStrategy getStrategy(String name, Map<String, String> config) {
    
        String strategyType = config.get(name + ".file.strategy");
        String path = config.get("file.path");
        boolean delete = !config.containsKey("file.strategy.delete")
                || Boolean.parseBoolean(config.get("file.strategy.delete"));
        
        return switch (strategyType) {
        
            case "SIZE" -> {
            
                String condition = config.get("file.strategy.condition");
            
                yield new SizeStrategy(path, delete, condition);
            }
            case "TIME" -> {
            
                Map<Integer, Integer> time = Map.of(
                        Calendar.SECOND, config.containsKey("file.strategy.seconds") ?
                                Integer.parseInt(config.get("file.strategy.seconds")) : 0,
                        Calendar.MINUTE, config.containsKey("file.strategy.minutes") ?
                                Integer.parseInt(config.get("file.strategy.minutes")) : 0,
                        Calendar.HOUR, config.containsKey("file.strategy.hours") ?
                                Integer.parseInt(config.get("file.strategy.hours")) : 0,
                        Calendar.DAY_OF_MONTH, config.containsKey("file.strategy.days") ?
                                Integer.parseInt(config.get("file.strategy.days")) : 0,
                        Calendar.MONTH, config.containsKey("file.strategy.months") ?
                                Integer.parseInt(config.get("file.strategy.months")) : 0
                );
            
                yield new TimeStrategy(path, delete, time);
            }
            default -> new DefaultStrategy(path);
        };
    }
}
