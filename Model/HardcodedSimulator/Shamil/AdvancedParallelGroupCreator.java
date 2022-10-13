/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import COVID_AgentBasedSimulation.Model.MainModel;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 *
 * @author user
 */
public class AdvancedParallelGroupCreator extends ParallelProcessor {
    Runnable myRunnable;
    
    public AdvancedParallelGroupCreator(MainModel parent, ArrayList<Region> regions, List<Integer> transport_free_seats, int n_events, ConcurrentHashMap<String, ShamilGroup> groupDict, int startIndex, int endIndex) {
        super(parent, regions, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    ShamilGroupManager.createGroupRegion(parent, regions.get(i), transport_free_seats, n_events, groupDict);
                }
            }
        };
    }
    
    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }
}
