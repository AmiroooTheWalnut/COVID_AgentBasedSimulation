/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;

/**
 *
 * @author user
 */
public class PersonExactProperties {
    public LocationNode exactHomeLocation;
    public LocationNode exactWorkLocation;
    public POI[] pOIs;
    public short[] fromHomeFreqs;
    public short[] fromWorkFreqs;
    public short[] fromHomeFreqsCDF;
    public short[] fromWorkFreqsCDF;
//    public HashMap<POI, Double> pOIHomeProbabilities=new HashMap();
//    public HashMap<POI, Double> pOIWorkProbabilities=new HashMap();
    public float sumHomeFreqs;
    public float sumWorkFreqs;
}
