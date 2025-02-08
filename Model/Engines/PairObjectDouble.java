/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.Engines;

import java.util.Comparator;

/**
 *
 * @author user
 */
public class PairObjectDouble {

    private Object key;
    private Object data;
    private double value;
    
    public PairObjectDouble(Object k,Object d,double v){
        key=k;
        data=d;
        value=v;
    }

    public Object getData() {
        return data;
    }

    public Object getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }

    public static Comparator<PairObjectDouble> dataSrtingComparator = new Comparator<PairObjectDouble>(){
        @Override
        public int compare(PairObjectDouble o1, PairObjectDouble o2) {
            return ((String)o1.data).compareTo((String)o2.data);
        }    
    };
    
    public static Comparator<PairObjectDouble> valueComparator = new Comparator<PairObjectDouble>(){
        @Override
        public int compare(PairObjectDouble o1, PairObjectDouble o2) {
            if (o1.value == o2.value) {
            return 0;
        } else if (o1.value > o2.value) {
            return 1;
        } else {
            return -1;
        }
        }    
    };

}
