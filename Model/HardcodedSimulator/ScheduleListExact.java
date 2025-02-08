/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ScheduleListExact {
    public int regionIndex=-1;
    public POI[] pOIs;
    public short[] fromHomeFreqs;
    public short[] fromWorkFreqs;
    public float[] fromHomeFreqsCDF;
    public float[] fromWorkFreqsCDF;
    public float sumHomeFreqs=0;
    public float sumWorkFreqs=0;
}
