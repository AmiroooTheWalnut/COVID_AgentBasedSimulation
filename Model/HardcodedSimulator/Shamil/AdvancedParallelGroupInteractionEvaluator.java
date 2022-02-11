/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilGroupSimulator;
import COVID_AgentBasedSimulation.Model.MainModel;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 *
 * @author user
 */
public class AdvancedParallelGroupInteractionEvaluator extends ParallelProcessor {
    Runnable myRunnable;

    public AdvancedParallelGroupInteractionEvaluator(MainModel parent, ArrayList<ShamilGroup> groups, int startIndex, int endIndex) {
        super(parent, groups, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    ShamilGroupSimulator.groupInteraction(groups.get(i));
                }
            }
        };
    }

    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }
}
