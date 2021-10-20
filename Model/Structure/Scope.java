/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Scope extends Marker implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public ArrayList<CensusTract> censusTracts;

    public transient ArrayList<VDCell> vDCells;
    public transient ArrayList<CBGVDCell> cBGVDCells;
    
    public transient ArrayList<MyPolygon> vDPolygons;// transient FOR NOW. AFTER TESTING IT WILL BE REMOVED
    public transient ArrayList<MyPolygon> cBGVDPolygons;// transient FOR NOW. AFTER TESTING IT WILL BE REMOVED
    
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
