package org.polyjdbc.core.type;

public class SqlType {
    
    private final int code;

    /**
     * @param code code from {@link java.sql.Types}
     */
    public SqlType(int code) {
        this.code = code;
    }
    
    public int code() {
        return code;
        
    }
}
