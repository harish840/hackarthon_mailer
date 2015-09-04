/**
 *  Copyright 2015 Jasper Infotech (P) Limited . All Rights Reserved.
 *  JASPER INFOTECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */  
package IntelligentThinker;

import java.util.HashMap;
import java.util.List;

/**
 *  
 *  @version     1.0, 04-Sep-2015
 *  @author harish
 */
public class NounCounter {
    private long counter;
    private HashMap<String,Long> determinantMap;
    public HashMap<String, Long> getDeterminantMap() {
        return determinantMap;
    }
    public void setDeterminantMap(HashMap<String, Long> determinantMap) {
        this.determinantMap = determinantMap;
    }
    private List<DeterminantCounter> determinant;
    public long getCounter() {
        return counter;
    }
    public void setCounter(long counter) {
        this.counter = counter;
    }
    public List<DeterminantCounter> getDeterminant() {
        return determinant;
    }
    public void setDeterminant(List<DeterminantCounter> determinant) {
        this.determinant = determinant;
    }
    
    public void addDeterminant(DeterminantCounter determinant) {
        this.determinant.add(determinant);
    }
    public void deleteDeterminant(DeterminantCounter determinant) {
        this.determinant.remove(determinant);
    }
    
    
    

}
