/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Region extends Marker {
    public transient ScheduleList scheduleList=new ScheduleList();
    public transient double workPopulation;
    
    public transient ArrayList<Long> cBGsIDsInvolved = new ArrayList();
    public transient ArrayList<CensusBlockGroup> cBGsInvolved = new ArrayList();
    public transient ArrayList<Double> cBGsPercentageInvolved = new ArrayList();
    
    public transient ArrayList<Person> residents=new ArrayList();
    public transient ArrayList<Person> workers=new ArrayList();
    
    public MyPolygon polygon;
    
    public int N;
    public int S;
    public int IS;
    public int IAS;
    public int R;
}
