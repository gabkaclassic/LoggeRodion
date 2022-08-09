package com.company.logger.purposes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Writer;

@JsonTypeInfo(use = Id.NAME)
@JsonSerialize
@JsonSubTypes({
        @Type(FilePurpose.class),
        @Type(ConsolePurpose.class)
})
public abstract class Purpose {
    
    protected PurposeLevel level;
    protected Writer writer;
    protected PurposeType type;
    
    public Purpose() throws IOException {
    
        type = PurposeType.CONSOLE;
        setLevel(PurposeLevel.INFO);
    }
    
    public Purpose(PurposeLevel level) {
        
        type = PurposeType.CONSOLE;
        setLevel(level);
    }
    
    public static Purpose defaultPurpose() throws IOException {
        
        return new ConsolePurpose();
    }
    
    public synchronized void write(String text) throws IOException {
        
        try {
            writer.write(text);
        } catch (IOException e) {
            System.out.printf("An exception occurred while making log:\n%s", e);
        }
        finally {
            writer.close();
        }
    }
    
    public void setWriter(Writer writer) {
        
        if(writer != null)
            this.writer = writer;
    }
    
    public PurposeLevel getLevel() {
        
        return level;
    }
    
    public void cleanUp() throws IOException {
        
        writer.close();
    }
    
    public void setLevel(PurposeLevel level) {
        
        this.level = level;
    }
    
    public PurposeType getType() {
        
        return type;
    }
}
