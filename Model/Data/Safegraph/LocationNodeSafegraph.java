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
 * @author user
 */
public class LocationNodeSafegraph {
    public LocationNode node;
    public String placeKey;
    public ArrayList<String> placeKeys;
    public SafegraphPlace place;
    public ArrayList<SafegraphPlace> places;
    //public double remainingVisits;
}
