/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class PersonExactProperties {
    public LocationNode exactHomeLocation;
    public LocationNode exactWorkLocation;
    public POI[] pOIs;
    public short[] fromHomeFreqs;
    public short[] fromWorkFreqs;
    public float[] fromHomeFreqsCDF;
    public float[] fromWorkFreqsCDF;
//    public HashMap<POI, Double> pOIHomeProbabilities=new HashMap();
//    public HashMap<POI, Double> pOIWorkProbabilities=new HashMap();
    public float sumHomeFreqs;
    public float sumWorkFreqs;
}
