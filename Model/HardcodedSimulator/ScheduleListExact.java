/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class ScheduleListExact {
    public int regionIndex=-1;
    public ArrayList<POI> pOIs=new ArrayList();
    public ArrayList<Double> fromHomeFreqs=new ArrayList();
    public ArrayList<Double> fromWorkFreqs=new ArrayList();
    public ArrayList<Double> fromHomeFreqsCDF=new ArrayList();
    public ArrayList<Double> fromWorkFreqsCDF=new ArrayList();
    public double sumHomeFreqs=0;
    public double sumWorkFreqs=0;
}
