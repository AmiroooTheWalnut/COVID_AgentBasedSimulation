/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class ClustererManager {
    public ArrayList<ClustererInfo> activeClustererNames=new ArrayList();
    
    public static class ClustererInfo{
        public String clustererName="";
        public boolean isClusterRangeSupported;
        public int minCluster=-1;
        public int maxCluster=-1;
        public int fixedNumCluster=-1;
    }
}
