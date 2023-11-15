/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AdvancedParallelGeneratePersonSchedulesForWork extends ParallelProcessor {
    Runnable myRunnable;
    
    public AdvancedParallelGeneratePersonSchedulesForWork(RootArtificial parent, ArrayList<Person> people, int startIndex, int endIndex) {
        super(parent, people, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    parent.generatePersonSchedulesForWork(people.get(i));
                }
            }
        };
    }
    
    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }
}
