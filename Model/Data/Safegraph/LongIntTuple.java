/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class LongIntTuple {
    public long key;
    public int value;
    public LongIntTuple(long passed_key,int passed_value){
        key=passed_key;
        value=passed_value;
    }
}
