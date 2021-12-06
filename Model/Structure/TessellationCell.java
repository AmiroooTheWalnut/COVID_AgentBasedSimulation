/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class TessellationCell extends Marker implements Serializable {

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
    
}
