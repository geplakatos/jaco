package com.chickenleg.remote;

import java.text.*;
import java.util.*;

public class DateTimeHelper {
    SimpleDateFormat format = null;    
    SimpleDateFormat localformat = null;    
    
    public DateTimeHelper() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
        format.setTimeZone(TimeZone.getTimeZone("ETC/UTC"));            
        
        localformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                
    }
    
    public String getTimeAsString() {        
        return "UTC " + getTimeAsMySQLFormat();
    }
    
    public String getTimeAsMySQLFormat() {        
        return format.format(Calendar.getInstance().getTime());
    }
    
    public String getLocalTimeAsMySQLFormat() {        
        return localformat.format(Calendar.getInstance().getTime());
    }
    
    public long getCurrMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
