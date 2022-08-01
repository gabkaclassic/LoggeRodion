package com.company.logger;

import com.company.logger.colors.Dyer;
import com.company.logger.colors.Effect;
import com.company.logger.purposes.Purpose;
import com.company.logger.purposes.PurposeLevel;
import com.company.logger.purposes.PurposeType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LoggeRodion extends Logger {
    
    public LoggeRodion() {
        
        super(false, true, DEFAULT_DATE_PATTERN, false, false);
    }
    
    public LoggeRodion(boolean color, boolean secret, String datePattern, boolean concurrent, boolean daemon, Purpose... purposes) {
        
        super(color, secret, datePattern, concurrent, daemon, purposes);
    }
    
    public LoggeRodion(boolean color, Purpose... purposes) {
        
        super(color, true, null, false, false, purposes);
    }
    
    public LoggeRodion(boolean concurrent, boolean daemon, Purpose... purposes) {
        
        super(false, true, null, concurrent,daemon, purposes);
    }
    
    public LoggeRodion(String datePattern, Purpose... purposes) {
        
        super(false, true, datePattern, false, false, purposes);
    }
    
    public LoggeRodion(Purpose... purposes) {
        
        super(false, false, DEFAULT_DATE_PATTERN, false, false, purposes);
    }
    
    public void trace(String log, Object... args) {
    
        log(log, args, PurposeLevel.TRACE);
    }
    
    public void info(String log, Object... args) {
    
        log(log, args, PurposeLevel.INFO);
    }
    
    public void debug(String log, Object... args) {
    
        log(log, args, PurposeLevel.DEBUG);
    }
    
    public void warn(String log, Object... args) {
    
        log(log, args, PurposeLevel.WARN);
    }
    
    public void error(String log, Object... args) {
    
        log(log, args, PurposeLevel.ERROR);
    }
    
    public void fatal(String log, Object... args) {
    
        log(log, args, PurposeLevel.FATAL);
    }
    
    private void log(String log, Object[] args, PurposeLevel level) {
        
        var participants = purposes.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getLevel().getLevel() <= level.getLevel())
                .toList();
        if(participants.isEmpty())
            return;
        
        log = prepareText(log, args);
        var logs = Dyer.coloring(log, colorMap, color);
        
        if(concurrent) {
            concurrentLog(participants, logs);
            return;
        }
        
        for(var participant: participants) {
            try {
                if(participant.getType().equals(PurposeType.CONSOLE))
                    participant.write(logs.get(level));
                else
                    participant.write(log);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void concurrentLog(List<Purpose> participants, Map<PurposeLevel, String> logs) {
    
        ExecutorService service = Executors.newFixedThreadPool(purposes.size());
        for(var participant: participants)
            service.submit(
                    new LogThread(
                            participant,
                            logs.get(participant.getLevel()),
                            daemon
                    )
            );
        
        service.shutdown();
    }
    
    private String prepareText(String log, Object[] args) {
        
        for (var arg : args)
            log = log.replaceFirst(putPoint, arg.toString());
    
        if(secret)
            log = hideSecrets(log);
        log = dateFormat.format(Calendar.getInstance().getTime()) + log;
        
        log = log + "\n";
    
        return log;
    }
    
    private String hideSecrets(String log) {
        
        var envs = System.getenv().values().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
        
        for(String env: envs)
            log = log.replaceAll(env.replaceAll("\\\\", "\\\\\\\\"), "*".repeat(env.length()));
        
        return log;
    }
    
    public void setupColorMap(Map<PurposeLevel, Effect> settings) {
        
        colorMap.putAll(settings);
    }
    
    public boolean addPurposes(Purpose... purposes) {
        
        return this.purposes.addAll(Arrays.asList(purposes));
    }
    
    public boolean removePurposes(OutputStream... purposes) {
        
        return this.purposes.removeAll(Arrays.asList(purposes));
    }
    
    public void setPutPoint(String putPoint) {
        
        if(putPoint != null)
            this.putPoint = putPoint;
    }
}

class LogThread extends Thread {
    
    private Purpose purpose;
    private String log;
    
    LogThread(Purpose purpose, String log, boolean daemon) {
        
        this.purpose = purpose;
        this.log = log;
        setDaemon(daemon);
    }
    
    public void run() {
        
        super.run();
        try {
            purpose.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

