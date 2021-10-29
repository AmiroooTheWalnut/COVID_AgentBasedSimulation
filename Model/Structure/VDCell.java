/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class VDCell extends Marker implements Serializable {

    static final long serialVersionUID = softwareVersion;
    
    public int myIndex;

    public ArrayList<Long> cBGsIDsInvolved = new ArrayList();
    public transient ArrayList<CensusBlockGroup> cBGsInvolved = new ArrayList();
    public ArrayList<Double> cBGsPercentageInvolved = new ArrayList();
    public ArrayList<String> shopPlacesKeys = new ArrayList();
    public transient ArrayList<SafegraphPlace> shopPlaces = new ArrayList();
    public ArrayList<String> schoolPlacesKeys = new ArrayList();
    public transient ArrayList<SafegraphPlace> schoolPlaces = new ArrayList();
    public ArrayList<String> templePlacesKeys = new ArrayList();
    public transient ArrayList<SafegraphPlace> templePlaces = new ArrayList();
    public transient ArrayList<Integer> remainingFreqs = new ArrayList();

//    public void calcCentroid() {
//        float centroidLat=0;
//        float centroidLon=0;
//        if (cBGsInvolved.size() > 0) {
//            for (int i = 0; i < cBGsInvolved.size(); i++) {
//                centroidLat = centroidLat + cBGsInvolved.get(i).lat*cBGsPercentageInvolved.get(i).floatValue();
//                centroidLon = centroidLon + cBGsInvolved.get(i).lon*cBGsPercentageInvolved.get(i).floatValue();
//            }
//        }
//        this.lat=centroidLat;
//        this.lon=centroidLon;
//    }
}
