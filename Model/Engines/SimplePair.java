/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.Engines;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author user
 */
@Getter @Setter
public class SimplePair<T extends Object, U extends Object> implements Comparable{
    private Object key;
    private Object value;
    public SimplePair(Object passed_key,Object passed_value){
        key=passed_key;
        value=passed_value;
    }

    @Override
    public int compareTo(Object o) {
        return ((String)key).compareTo((String)((SimplePair)o).key);
    }
}
