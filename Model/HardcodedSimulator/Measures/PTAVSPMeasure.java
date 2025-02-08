/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Measures;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.POI;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class PTAVSPMeasure {
    public CensusBlockGroup source1;//s1
    public CensusBlockGroup source2;//s2
    public POI destination;//d
    public double durationOfMeet=1;//tu
    public double freqsNoCoVisit=0;
    public double freqsCoVisit=0;
    public double prob;//p0
}
