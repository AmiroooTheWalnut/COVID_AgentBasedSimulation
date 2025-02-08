/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
//import lombok.Getter;
//import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class LongIntTuple implements Serializable{
    static final long serialVersionUID = softwareVersion;
    
    public long key;
    public int value;
    public LongIntTuple(long passed_key,int passed_value){
        key=passed_key;
        value=passed_value;
    }
    
    public long getKey(){
        return key;
    }
}
