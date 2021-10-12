/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Region extends Marker {
    public ScheduleList scheduleList=new ScheduleList();
    double workPopulation;
    
    public ArrayList<Long> cBGsIDsInvolved = new ArrayList();
    public ArrayList<CensusBlockGroup> cBGsInvolved = new ArrayList();
    public ArrayList<Double> cBGsPercentageInvolved = new ArrayList();
    
    public int N;
    public int S;
    public int E;
    public int IS;
    public int IAS;
    public int R;
}
