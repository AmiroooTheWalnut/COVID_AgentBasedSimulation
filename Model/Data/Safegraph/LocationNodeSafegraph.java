/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class LocationNodeSafegraph implements Comparable<LocationNodeSafegraph> {

    public LocationNode node;
    public String placeKey;
    public ArrayList<String> placeKeys;
    public SafegraphPlace place;
    public ArrayList<SafegraphPlace> places;
    public int numVisits = -1;
    //public double remainingVisits;

    @Override
    public int compareTo(LocationNodeSafegraph o) {
        if (numVisits == o.numVisits) {
            return 0;
        } else if (numVisits > o.numVisits) {
            return 1;
        } else {
            return -1;
        }
    }
}
