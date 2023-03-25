/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Measures;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.POI;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;

/**
 *
 * @author user
 */
public class ADVMeasure {
    public CensusBlockGroup source;//s
    public POI destination;//d
    public double averageDurationOfVisits;//D
    public int numVisits=0;
    public double sumDuration;
}
