/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.GUI;

/**
 *
 * @author user
 */
public class TupleIntFloat implements Comparable<TupleIntFloat> {
    public int index;
    public float value;
    
    public TupleIntFloat(int passed_index, float passed_value){
        index=passed_index;
        value=passed_value;
    }

    @Override
    public int compareTo(TupleIntFloat arg0) {
        if (value == arg0.value)
            return 0;
        else if (value > arg0.value)
            return 1;
        else
            return -1;
    }
}
