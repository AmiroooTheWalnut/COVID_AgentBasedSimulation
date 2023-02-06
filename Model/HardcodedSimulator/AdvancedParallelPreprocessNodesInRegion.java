/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 *
 * @author user
 */
public class AdvancedParallelPreprocessNodesInRegion extends ParallelProcessor {
    Runnable myRunnable;
    
    public AdvancedParallelPreprocessNodesInRegion(RootArtificial parent, ArrayList<Region> regions, int cbgLayerIndex, int baseLayerIndex, LayerDefinition cbgLayer, LayerDefinition baseLayer, int startIndex, int endIndex) {
        super(parent, regions, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    parent.preprocessNodesInRegion(regions.get(i), cbgLayerIndex, baseLayerIndex, cbgLayer, baseLayer);
                }
            }
        };
    }
    
    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }
}
