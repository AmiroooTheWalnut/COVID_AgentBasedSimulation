/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import de.fhpotsdam.unfolding.UnfoldingMap;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class MapSourse {
    public UnfoldingMap map;
    public String name;

    public MapSourse(UnfoldingMap passed_map, String passed_name) {
        this.map = passed_map;
        this.name = passed_name;
    }
}
