/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class CensusTract extends Marker implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public int id;
    public transient boolean isLatLonCalculated=false;
    public ArrayList<CensusBlockGroup> censusBlocks;
    
    public void getLatLonSizeFromChildren(){
        float minLat = Float.MAX_VALUE;
        float maxLat = -Float.MAX_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = -Float.MAX_VALUE;
        float latCumulative=0;
        float lonCumulative=0;
        for (int i = 0; i < censusBlocks.size(); i++) {
            float childLat = censusBlocks.get(i).lat;
            float childLon = censusBlocks.get(i).lon;
            if (childLat > maxLat) {
                maxLat = childLat;
            }
            if (childLat < minLat) {
                minLat = childLat;
            }
            if (childLon > maxLon) {
                maxLon = childLon;
            }
            if (childLon < minLon) {
                minLon = childLon;
            }
            latCumulative=latCumulative+childLat;
            lonCumulative=lonCumulative+childLon;
        }
        lat = latCumulative / (float)censusBlocks.size();
        lon = lonCumulative / (float)censusBlocks.size();
        size = Math.max(maxLat - minLat, maxLon - minLon);
    }
    
    public boolean isNewCensusBlockUnique(long input) {
        if (censusBlocks == null) {
            censusBlocks = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < censusBlocks.size(); i++) {
                if (censusBlocks.get(i).id==input) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public CensusBlockGroup findCensusBlock(long input) {
        if (censusBlocks == null) {
            censusBlocks = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < censusBlocks.size(); i++) {
                if (censusBlocks.get(i).id==input) {
                    return censusBlocks.get(i);
                }
            }
        }
        return null;
    }
    
    public CensusBlockGroup findAndInsertCensusBlock(long input) {
        if (censusBlocks == null) {
            censusBlocks = new ArrayList();
            CensusBlockGroup temp = new CensusBlockGroup();
            temp.id = input;
            censusBlocks.add(temp);
            return censusBlocks.get(0);
        } else {
            for (int i = 0; i < censusBlocks.size(); i++) {
                if (censusBlocks.get(i).id==input) {
                    return censusBlocks.get(i);
                }
            }
        }
        CensusBlockGroup temp = new CensusBlockGroup();
        temp.id = input;
        censusBlocks.add(temp);
        return censusBlocks.get(censusBlocks.size()-1);
    }
    
    
    
}
