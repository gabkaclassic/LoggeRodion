package com.company.logger;

import com.company.logger.colors.Effect;
import com.company.logger.purposes.ConsolePurpose;
import com.company.logger.purposes.Purpose;
import com.company.logger.purposes.PurposeLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@JsonSerialize
public abstract class Logger {
    
    protected static final String DEFAULT_DATE_FORMAT = "ss:mm:HH|dd-MM-yy";
    protected static final String DEFAULT_FORMAT = "{date}|{log}";
    
    @JsonProperty
    protected DateFormat dateFormat;
    @JsonProperty
    protected String format = DEFAULT_FORMAT;
    @JsonProperty
    protected boolean daemon;
    @JsonProperty
    protected boolean concurrent;
    @JsonProperty
    protected boolean color;
    @JsonProperty
    protected boolean secret;
    @JsonProperty
    protected String datePattern;
    @JsonProperty
    protected Map<PurposeLevel, Effect> colorMap;
    @JsonProperty
    protected Set<Purpose> purposes;
    @JsonProperty
    protected String putPoint = "\\{}";
    
    {
        colorMap = Map.of(
                PurposeLevel.TRACE, Effect.BLACK,
                PurposeLevel.INFO, Effect.BLACK_BRIGHT,
                PurposeLevel.DEBUG, Effect.GREEN,
                PurposeLevel.WARN, Effect.YELLOW,
                PurposeLevel.ERROR, Effect.RED_BOLD,
                PurposeLevel.FATAL, Effect.PURPLE_BOLD
        );
    }
    
    Logger() {
        
        this(false, false, DEFAULT_DATE_FORMAT, false, false);
    }
    
    Logger(boolean color, boolean secret, String datePattern, boolean concurrent, boolean daemon, Purpose... purposes) {
        
        setSecret(secret);
        setDateFormat(datePattern);
        setConcurrent(concurrent);
        setDaemon(daemon);
        setColor(color);
        this.purposes = ConcurrentHashMap.newKeySet();
        if(purposes != null && purposes.length != 0)
            this.purposes.addAll(Arrays.asList(purposes));
        else {
            try {
                this.purposes.add(new ConsolePurpose());
            } catch (IOException e) {
                System.out.printf("An exception occurred while creating console logger:\n%s", e);
            }
        }
    }
    
    Logger(Purpose... purposes) {
        
        this(false, false, DEFAULT_DATE_FORMAT, false, false, purposes);
    }
    
    public abstract void trace(String log, Object ... args);
    
    public abstract void info(String log, Object ... args);
    
    public abstract void debug(String log, Object ... args);
    
    public abstract void warn(String log, Object ... args);
    
    public abstract void error(String log, Object ... args);
    
    public abstract void fatal(String log, Object ... args);
    
    protected String formattingText(String text, PurposeLevel level) {
        
        return format.replaceAll("\\{date}", getTimeView())
                .replaceAll("\\{log}", text)
                .replaceAll("\\{level}", level.getView())
                .replaceAll(
                        "\\{stack}",
                        Arrays.stream(Thread.currentThread().getStackTrace())
                                .map(Object::toString)
                                .collect(Collectors.joining("\n"))
                );
    }
    
    
    public String getFormat() {
        
        return format;
    }
    
    public void setFormat(String format) {
        
        this.format = format;
    }
    
    public abstract void setupColorMap(Map<PurposeLevel, Effect> settings);
    
    protected String getTimeView() {
        
        return dateFormat.format(Calendar.getInstance().getTime());
    }
    
    private void setDaemon(boolean daemon) {
        
        if(this.daemon = daemon)
            concurrent = true;
    }
    
    private void setConcurrent(boolean concurrent) {
        
        this.concurrent = concurrent;
    }
    
    private void setColor(boolean color) {
        
        this.color = color;
    }
    
    private void setSecret(boolean secret) {
        
        this.secret = secret;
    }
    
    public void setDateFormat(String datePattern) {
    
        this.datePattern = Objects.requireNonNullElse(datePattern, DEFAULT_DATE_FORMAT);
        dateFormat = new SimpleDateFormat(this.datePattern);
    }
    
    public void setDateFormat(DateFormat dateFormat) {
        
        if(this.dateFormat != null)
            this.dateFormat = dateFormat;
        else 
            throw new NullPointerException();
    }
    
    public void setColorMap(Map<PurposeLevel, Effect> colorMap) {
        
        this.colorMap = colorMap;
    }
    
    public void setPurposes(Set<Purpose> purposes) {
        
        this.purposes = purposes;
    }
    
    public void setPutPoint(String putPoint) {
        
        this.putPoint = putPoint;
    }
}
