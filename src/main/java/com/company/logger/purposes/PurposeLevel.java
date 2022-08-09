package com.company.logger.purposes;

public enum PurposeLevel {
    DEBUG(0, "|--DEBUG--|"),
    TRACE(1, "|--TRACE--|"),
    INFO(2, "|---INFO--|"),
    WARN(3, "|---WARN--|"),
    ERROR(4, "|--ERROR--|"),
    FATAL(5, "|--FATAL--|");
    
    private int level;
    private String view;
    
    PurposeLevel(int level, String view) {
        
        this.level = level;
        this.view = view;
    }
    
    public int getLevel() {
        
        return level;
    }
    
    public String getView() {
        
        return view;
    }
}
