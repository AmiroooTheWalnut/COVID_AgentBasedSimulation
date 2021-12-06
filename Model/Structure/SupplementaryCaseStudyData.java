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
public class SupplementaryCaseStudyData implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public ArrayList<Tessellation> tessellations;
    
    public ArrayList<VDCell> vDCells;
    public ArrayList<CBGVDCell> cBGVDCells;
    public double shopMergePrecision;
    public double schoolMergePrecision;
    public double templeMergePrecision;

    public HashMap<Long, MyPolygons> cBGPolygons = new HashMap();//DEPRECIATED!
    public HashMap<Integer, MyPolygons> vDPolygons = new HashMap();//DEPRECIATED!
    public HashMap<Integer, MyPolygons> cBGVDPolygons = new HashMap();//DEPRECIATED!
    
//    public RegionImageLayer vDRegionLayer;
//    public RegionImageLayer cBGVDGRegionLayer;
//    public RegionImageLayer cBGRegionLayer;
    
    
    public RegionImageLayer cBGRegionImageLayer;
    public RegionImageLayer vDRegionImageLayer;
    public RegionImageLayer cBGVDRegionImageLayer;
}
