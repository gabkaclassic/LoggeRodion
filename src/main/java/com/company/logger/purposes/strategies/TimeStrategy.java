package com.company.logger.purposes.strategies;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public final class TimeStrategy extends LogStrategy {
    
    private final Map<Integer, Integer> time;
    
    {
        time = new HashMap<>();
        time.put(Calendar.SECOND, 0);
        time.put(Calendar.MINUTE, 0);
        time.put(Calendar.HOUR, 0);
        time.put(Calendar.DAY_OF_MONTH, 1);
        time.put(Calendar.MONTH, 0);
    }
    
    public TimeStrategy() {
        
        super();
    }
    
    public TimeStrategy(String path, boolean delete) {
        
        super(path, delete);
    }
    
    public TimeStrategy(String path, boolean delete, Map<Integer, Integer> props) {
        
        super(path, delete);
        time.putAll(props);
    }
    
    public synchronized String getPath() throws IOException {
    
        var path = Arrays.stream(
                        Objects.requireNonNull(new File(directory)
                                .listFiles((pathname -> pathname.getName().endsWith(EXTENSION)))
                        )
                )
                .map(File::getAbsolutePath)
                .max(Comparator.naturalOrder())
                .orElseGet(() -> directory + SEPARATOR + prefix + EXTENSION);
    
        var numberFiles = isNumeric(
                path.substring(path.lastIndexOf(prefix) + prefix.length(), path.lastIndexOf("."))
        );
    
        File file = new File(path);
    
        var creatingDate = Calendar.getInstance().getTime();
        if(file.exists())
            creatingDate = new Date(
                    Files.readAttributes(Path.of(path), BasicFileAttributes.class)
                            .creationTime()
                            .toMillis()
            );
        else
            file.createNewFile();
        
        if(checkTime(creatingDate)) {
        
            if(delete)
                Files.delete(Path.of(path));
            else
                path = directory + SEPARATOR + prefix + (numberFiles + 1) + EXTENSION;
        }
    
        
        return path;
    }
    
    public void setTime(Map<Integer, Integer> props) {
        
        time.putAll(props);
    }
    
    public void setTime(int seconds, int minutes, int hours, int days, int months) {
        
        setProperty(Calendar.SECOND, seconds);
        setProperty(Calendar.MINUTE, minutes);
        setProperty(Calendar.HOUR, hours);
        setProperty(Calendar.DAY_OF_MONTH, days);
        setProperty(Calendar.MONTH, months);
    }
    
    public void setTime(int seconds, int minutes, int hours, int days) {
    
        setProperty(Calendar.SECOND, seconds);
        setProperty(Calendar.MINUTE, minutes);
        setProperty(Calendar.HOUR, hours);
        setProperty(Calendar.DAY_OF_MONTH, days);
    }
    
    public void setTime(int seconds, int minutes, int hours) {
    
        setProperty(Calendar.SECOND, seconds);
        setProperty(Calendar.MINUTE, minutes);
        setProperty(Calendar.HOUR, hours);
    }
    
    public void setTime(int seconds, int minutes) {
    
        setProperty(Calendar.SECOND, seconds);
        setProperty(Calendar.MINUTE, minutes);
    }
    
    public void setTime(int seconds) {
    
        setProperty(Calendar.SECOND, seconds);
    }
    
    private void setProperty(int property, int value) {
        
        time.put(property, Math.max(value, 0));
    }
    
    private boolean checkTime(Date dateCreating) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateCreating);
        for(Integer prop: time.keySet())
            calendar.add(prop, time.get(prop));
        
        return Calendar.getInstance().after(calendar);
    }
    
    private int isNumeric(String string) {
        
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return 0;
        }
    }
}
