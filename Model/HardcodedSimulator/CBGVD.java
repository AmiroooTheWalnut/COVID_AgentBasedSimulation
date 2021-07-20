/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.Structure.CBGVDCell;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class CBGVD extends Agent{
    public CBGVDCell vdVal;
    
    public int N;
    public int S;
    public int E;
    public int IS;
    public int IAS;
    public int R;
    
    public CBGVD(){
        myType="CBGVD";
    }
}
