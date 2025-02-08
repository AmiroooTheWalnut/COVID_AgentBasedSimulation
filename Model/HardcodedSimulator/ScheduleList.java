/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ScheduleList {
    public ArrayList<PatternsRecordProcessed> originalDestinations=new ArrayList();
    public ArrayList<Double> originalFrequencies=new ArrayList();
    
    public double originalSumFrequencies=0;
    
    //FREQUENCIES STORED AFTER COLD RUN
    ArrayList<PatternsRecordProcessed> destinations=new ArrayList();
    ArrayList<Double> frequencies=new ArrayList();
}
