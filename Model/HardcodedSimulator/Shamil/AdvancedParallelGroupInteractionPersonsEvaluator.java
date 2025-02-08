/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.MainModel;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AdvancedParallelGroupInteractionPersonsEvaluator extends ParallelProcessor {
    public Runnable myRunnable;
    
    public AdvancedParallelGroupInteractionPersonsEvaluator(MainModel mainModel, ShamilGroup grp, ShamilAction action, List<Person> persons, Person acting_person, int acting_person_id, Double ACTION_AFFECTING_PROBABILITY, Double ACTION_INFECT_THRESHOLD, int startIndex, int endIndex) {
        super(mainModel, grp, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    ShamilGroupSimulator.groupInteractionPersons(mainModel, grp, action, persons.get(i), acting_person, acting_person_id, ACTION_AFFECTING_PROBABILITY, ACTION_INFECT_THRESHOLD);
                }
            }
        };
    }

    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }
}
