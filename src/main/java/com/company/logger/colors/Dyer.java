package com.company.logger.colors;

import com.company.logger.purposes.PurposeLevel;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class Dyer {
    
    public static final String RESET = "\033[0m";
    private static final Dyer dyer = new Dyer();
    
    private StringBuilder value;
    
    private Dyer(){
        
    }
    
    public static Map<PurposeLevel, String> coloring(String log, Map<PurposeLevel, Effect> colorMap, boolean color) {
    
       if(color)
            return colorMap.keySet().stream().filter(k -> colorMap.get(k) != null)
                    .collect(Collectors.toMap(l -> l, l -> paint(l.getView() + log, colorMap.get(l)).text()));
       
       return Arrays.stream(PurposeLevel.values())
               .collect(Collectors.toMap(l -> l, l -> l.getView() + log));
    }
    
    public static Dyer paint(String text, Effect... effects) {
        
        dyer.value = new StringBuilder();
        dyer.value.append(text);
        for(var effect: effects)
            dyer.value.insert(0, effect.getValue());
        
        return dyer;
    }
    
    public Dyer then(Effect effect) {
        
        dyer.value.insert(0, effect.getValue());
        
        return dyer;
    }
    
    public String text() {
        
        dyer.value.append(RESET);
        String text = dyer.value.toString();
        dyer.value.setLength(0);
        
        return text;
    }
}
