/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Tessellation {
    public String scenarioName;
    public ArrayList<TessellationCell> cells=new ArrayList();
    public RegionImageLayer regionImageLayer=new RegionImageLayer();
}
