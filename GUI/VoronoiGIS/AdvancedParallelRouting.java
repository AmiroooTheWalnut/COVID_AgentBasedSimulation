/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.GUI.VoronoiGIS;

import COVID_AgentBasedSimulation.Model.MainModel;
import esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel;
import esmaieeli.gisFastLocationOptimization.Simulation.FacilityLocation;
import esmaieeli.gisFastLocationOptimization.Simulation.Routing;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AdvancedParallelRouting extends ParallelProcessor {

    Runnable myRunnable;

    public AdvancedParallelRouting(MainFramePanel parent, ArrayList<ArrayList<Double>> data, int startIndex, int endIndex,String facilityName, int facilityIndex, FacilityLocation[] facilities, ArrayList<Integer> sampleIndices, int trafficLayerIndex, int CPUIndex) {
        super(parent, data, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    parent.allData.resetParallel(CPUIndex, -1);
                    Routing routingToOthers = new Routing(parent.allData, trafficLayerIndex, CPUIndex);
                    routingToOthers.findPath(facilities[facilityIndex].nodeLocation,parent.allData.all_Nodes[sampleIndices.get(i)]);
                    double distance = routingToOthers.pathDistance;
                    if(Double.isInfinite(distance)){
                        System.out.println("PROBLEM on node: "+sampleIndices.get(i)+" "+facilityName+" "+facilityIndex+" facility location node: "+facilities[facilityIndex].nodeLocation.id);
                    }
                    data.get(facilityIndex).set(i, distance);
                    System.out.println(facilityName+": " + facilityIndex + " Total "+facilityName+"s: " + facilities.length + " Processed locally: " + (100-((myEndIndex-i)*100)/(myEndIndex-myStartIndex)) + " total local length: "+(myEndIndex-myStartIndex)+" Total length: "+sampleIndices.size());
                }
            }
        };
    }

    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }
}
