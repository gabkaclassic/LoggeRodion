package com.company.logger.purposes.strategies;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public final class SizeStrategy extends LogStrategy {
    
    private static final long DEFAULT_SIZE = 1024;
    
    private long bytes;
    
    public SizeStrategy() {
        
        super();
        setBytes(0);
    }
    
    public SizeStrategy(String path, boolean delete, String condition) {
        
        super(path, delete);
        setSize(condition);
    }
    
    public SizeStrategy(String path, boolean delete, long bytes) {
        
        super(path, delete);
        setBytes(bytes);
    }
    
    public SizeStrategy(String path) {
     
        this(path, true, 0);
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

        var currentSize = (Files.exists(Path.of(path))) ? Files.size(Path.of(path)) : 0;

        if (bytes <= currentSize) {
    
            if (delete)
                Files.delete(Path.of(path));
            else
                path = directory + SEPARATOR + prefix + (numberFiles + 1) + EXTENSION;
        }
    
        File file = new File(path);
    
        if(!file.exists())
            file.createNewFile();
        
        return path;
    }
    
    private int isNumeric(String string) {
        
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public void setSize(String condition) {
        
        condition = (condition == null) ? "1024" : condition;
        
        var multiplier = switch (condition.substring(condition.indexOf(" ") + 1)) {
            case "KB" -> 1024;
            case "MB" -> 1024 * 1024;
            case "GB" -> 1024 * 1024 * 1024;
            default -> 1;
        };
        
        bytes = Math.abs(Long.parseLong(condition.substring(0, condition.indexOf(" ")))) * multiplier;
    }
    
    public void setBytes(long bytes) {
        
        this.bytes = (bytes > 0) ? bytes : DEFAULT_SIZE;
    }
}
