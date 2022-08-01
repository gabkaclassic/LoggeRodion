package com.company.logger.purposes;

import java.io.IOException;
import java.io.PrintWriter;

public final class ConsolePurpose extends Purpose {
    
    public ConsolePurpose() throws IOException {
        super();
        setWriter(new PrintWriter(System.out));
    }
    
    public ConsolePurpose(PurposeLevel level) {
        
        super(level);
        setWriter(new PrintWriter(System.out));
    }
}
