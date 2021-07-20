/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class CBGVDCell extends Marker {
    public ArrayList<Long> cBGsIDsInvolved;
    public transient ArrayList<CensusBlockGroup> cBGsInvolved;
    public ArrayList<Double> cBGsPercentageInvolved;
    public ArrayList<String> placesKeys;
    public transient ArrayList<SafegraphPlace> places;
    public ArrayList<Integer> remainingFreqs;
}
