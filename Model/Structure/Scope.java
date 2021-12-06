/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class Scope extends Marker implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public ArrayList<CensusTract> censusTracts;

    public transient ArrayList<Tessellation> tessellations;
    
    public transient ArrayList<VDCell> vDCells;
    public transient ArrayList<CBGVDCell> cBGVDCells;
    
    public transient HashMap<Integer, MyPolygons> vDPolygons=new HashMap();//DEPRECIATED! THIS IS TRANSIENT BECAUSE THE SupplementaryCaseStudyData HANDLES IT
    public transient HashMap<Integer, MyPolygons> cBGVDPolygons=new HashMap();//DEPRECIATED! THIS IS TRANSIENT BECAUSE THE SupplementaryCaseStudyData HANDLES IT
    
    public transient HashMap<Long, MyPolygons> cBGPolygons=new HashMap();//DEPRECIATED! THIS IS TRANSIENT BECAUSE THE SupplementaryCaseStudyData HANDLES IT
    
    public transient RegionImageLayer vDRegionLayer;
    public transient RegionImageLayer cBGVDRegionLayer;
    public transient RegionImageLayer cBGRegionLayer;
    
    public CensusBlockGroup findCBG(long censusBlockLong){
        for(int i=0;i<censusTracts.size();i++){
            CensusBlockGroup cBG=censusTracts.get(i).findCensusBlock(censusBlockLong);
            if(cBG!=null){
                return cBG;
            }
        }
        return null;
    }
    
}
