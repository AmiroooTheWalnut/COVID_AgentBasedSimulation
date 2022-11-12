/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class PersonExactProperties {
    public LocationNode exactHomeLocation;
    public LocationNode exactWorkLocation;
    public ArrayList<POI> pOIs=new ArrayList();
    public ArrayList<Double> fromHomeFreqs=new ArrayList();
    public ArrayList<Double> fromWorkFreqs=new ArrayList();
    public ArrayList<Double> fromHomeFreqsCDF=new ArrayList();
    public ArrayList<Double> fromWorkFreqsCDF=new ArrayList();
//    public HashMap<POI, Double> pOIHomeProbabilities=new HashMap();
//    public HashMap<POI, Double> pOIWorkProbabilities=new HashMap();
    public double sumHomeFreqs;
    public double sumWorkFreqs;
}
