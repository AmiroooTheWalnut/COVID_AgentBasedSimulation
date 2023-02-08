/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

/**
 *
 * @author user
 */
public class ScheduleListExact {
    public int regionIndex=-1;
    public POI[] pOIs;
    public short[] fromHomeFreqs;
    public short[] fromWorkFreqs;
    public short[] fromHomeFreqsCDF;
    public short[] fromWorkFreqsCDF;
    public float sumHomeFreqs=0;
    public float sumWorkFreqs=0;
}
