/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class Region extends Marker implements Serializable {
    
    static final long serialVersionUID = softwareVersion;
    
    public transient ScheduleList scheduleList=new ScheduleList();
    public transient ScheduleListExact scheduleListExact=new ScheduleListExact();
    public transient double workPopulation;
    
    public ArrayList<Long> cBGsIDsInvolved = new ArrayList();
    public transient ArrayList<CensusBlockGroup> cBGsInvolved = new ArrayList();
    public ArrayList<Double> cBGsPercentageInvolved = new ArrayList();
    
    public transient ArrayList<Person> residents=new ArrayList();
    public transient ArrayList<Person> workers=new ArrayList();
    
    public transient HashMap<Integer, Region> neighbors=new HashMap();
    
    public ArrayList<MyPolygons> polygons=new ArrayList();
    
    public ArrayList<RegionSnapshot> hourlyRegionSnapshot=new ArrayList();
    
    public transient ArrayList<LocationNode> locationNodes=new ArrayList();
    public transient ArrayList<Integer> locationNodeHomeFreqs=new ArrayList();
    public transient ArrayList<Integer> locationNodeWorkFreqs=new ArrayList();
    public transient int sumHomeFreqs;
    public transient int sumWorkFreqs;
    
    public transient int debugNumTravelPTAVSP=0;
    
    public transient int myIndex;
    
    public Region(int passed_index){
        myIndex=passed_index;
    }
    
    public Region(){
        
    }
    
}
