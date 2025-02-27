/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.randn;
//import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.rnd;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ShamilGroupSimulator {

    public static ShamilGroup groupInteraction(MainModel mainModel, ShamilGroup grp) {

        Double ACTION_AFFECTING_PROBABILITY = ShamilPersonManager.thresholds_df.get("ACTION_AFFECTING_PROBABILITY");
        Double ACTION_INFECT_THRESHOLD = ShamilPersonManager.thresholds_df.get("ACTION_INFECT_THRESHOLD");

//        ACTION_AFFECTING_PROBABILITY = dfToFloat(thresholds_df,"ACTION_AFFECTING_PROBABILITY")
//        ACTION_INFECT_THRESHOLD = dfToFloat(thresholds_df,"ACTION_INFECT_THRESHOLD")
        int next_timestamp = 10;

        for (int i = 0; i < grp.actions.size(); i++) { // actn in grp.actions:
            ShamilAction actn = grp.actions.get(i);
            if (actn.timestamp >= next_timestamp) {

                grp.updateProximity(mainModel);

                next_timestamp += 10;
            }

            int acting_person_id = actn.person_id;
            Person acting_person = grp.persons.get(grp.person_mapper.get(acting_person_id));
//            acting_person = grp.persons[grp.person_mapper[acting_person_id]];

            for (int j = 0; j < grp.persons.size(); j++) {// prsn in grp.persons:
                for (int m = 0; m < grp.persons.get(j).insidePeople.size(); m++) {
                    if (randn(mainModel, actn.min_prob_affect, actn.max_prob_affect) > ACTION_AFFECTING_PROBABILITY) {
//                if(randn(actn.min_prob_affect,actn.max_prob_affect)>ACTION_AFFECTING_PROBABILITY):
                        if (grp.persons.get(j).shamilPersonProperties.id == acting_person_id) {
                            if (actn.min_effect_self >= 0) {

                                double infection = (1 - grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(mainModel, actn.min_effect_self, actn.max_effect_self);

                                // #print(infection)
                                if (infection > ACTION_INFECT_THRESHOLD) {
                                    grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel = Math.max(Math.min(grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel + infection, 1), 0);
                                    // #print("\n" + str(actn.name) + " the infection case for self infection is - " + str(prsn.infection_level));
//                                    System.out.println("INF INC 1");
//                                    System.out.println("INF INC 1: "+grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel);
                                    mainModel.ABM.shamilInf1.incrementAndGet();// += 1;
                                }
                            } else {

                                double infection = (1 - grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(mainModel, actn.min_effect_self, actn.max_effect_self);
                                // #print("the infection case is - " + str(infection))
                                double infection_pos = (-1) * infection;

                                if (infection_pos > ACTION_INFECT_THRESHOLD) {
                                    grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel = Math.max(Math.min(grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel + infection, 1), 0);
//                                    System.out.println("INF INC 2");
//                                    System.out.println("INF INC 2: "+grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel);
                                    mainModel.ABM.shamilInf2.incrementAndGet();// += 1;
                                }
                            }
                        } else {
                            int insidePersonIndex = (int) ((mainModel.ABM.root.rnd.nextDouble() * (acting_person.insidePeople.size() - 1)));
//                            for (int n = 0; n < acting_person.insidePeople.size(); n++) {
                            if (acting_person.insidePeople.get(insidePersonIndex).sfpp.state.equals("contagious_asymptomatic") || acting_person.insidePeople.get(insidePersonIndex).sfpp.state.equals("contagious_symptomatic")) {

                                if (grp.persons.get(j).insidePeople.get(m).sfpp.isInfected == false) {

                                    // #print("the people - " + str(acting_person_id) + " " +str(actn.person_id) + " " + str(prsn.id))
                                    double proximit = grp.getProximity(acting_person_id, grp.persons.get(j).shamilPersonProperties.id);
                                    // #proximit = 1

                                    double infection = (1 - grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(mainModel, actn.min_effect_others, actn.max_effect_others);
//                                infection = (1-grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(actn.min_effect_others,actn.max_effect_others)
                                    // #print("infection before = " + str(infection) + " proximity - " + str(proximit))

                                    infection = infection * proximit;

                                    if (infection > ACTION_INFECT_THRESHOLD) {
                                        grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel = Math.max(Math.min(grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel + infection, 1), 0);
                                        grp.persons.get(j).shamilPersonProperties.infectedBy = acting_person_id;
                                        grp.persons.get(j).shamilPersonProperties.infectedByUpdt += 1;
//                                            System.out.println("INF INC 3");
//                                            System.out.println("INF INC 3: "+grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel);
                                        mainModel.ABM.shamilInf3.incrementAndGet();// += 1;
                                    }
                                }
                            }
//                            }
                        }
                    }
                }
            }
        }

        return grp;
    }

    public static ShamilGroup groupInteractionParallel(MainModel mainModel, ShamilGroup grp) {
        Double ACTION_AFFECTING_PROBABILITY = ShamilPersonManager.thresholds_df.get("ACTION_AFFECTING_PROBABILITY");
        Double ACTION_INFECT_THRESHOLD = ShamilPersonManager.thresholds_df.get("ACTION_INFECT_THRESHOLD");

//        ACTION_AFFECTING_PROBABILITY = dfToFloat(thresholds_df,"ACTION_AFFECTING_PROBABILITY")
//        ACTION_INFECT_THRESHOLD = dfToFloat(thresholds_df,"ACTION_INFECT_THRESHOLD")
        int next_timestamp = 10;

        for (int i = 0; i < grp.actions.size(); i++) { // actn in grp.actions:
            ShamilAction actn = grp.actions.get(i);
            if (actn.timestamp >= next_timestamp) {

                grp.updateProximity(mainModel);

                next_timestamp += 10;
            }

            int acting_person_id = actn.person_id;
            Person acting_person = grp.persons.get(grp.person_mapper.get(acting_person_id));
//            acting_person = grp.persons[grp.person_mapper[acting_person_id]];

            int numProcessors = mainModel.numCPUs;
            try {
                AdvancedParallelGroupInteractionPersonsEvaluator parallelGroupInteractionPersonsEvaluator[] = new AdvancedParallelGroupInteractionPersonsEvaluator[numProcessors];

                for (int p = 0; p < numProcessors - 1; p++) {
                    parallelGroupInteractionPersonsEvaluator[p] = new AdvancedParallelGroupInteractionPersonsEvaluator(mainModel, grp, actn, grp.persons, acting_person, acting_person_id, ACTION_AFFECTING_PROBABILITY, ACTION_INFECT_THRESHOLD, (int) Math.floor(p * ((grp.persons.size()) / numProcessors)), (int) Math.floor((p + 1) * ((grp.persons.size()) / numProcessors)));
                }
                parallelGroupInteractionPersonsEvaluator[numProcessors - 1] = new AdvancedParallelGroupInteractionPersonsEvaluator(mainModel, grp, actn, grp.persons, acting_person, acting_person_id, ACTION_AFFECTING_PROBABILITY, ACTION_INFECT_THRESHOLD, (int) Math.floor((numProcessors - 1) * ((grp.persons.size()) / numProcessors)), grp.persons.size());

                ArrayList<Callable<Object>> calls = new ArrayList<>();

                for (int p = 0; p < numProcessors; p++) {
                    parallelGroupInteractionPersonsEvaluator[p].addRunnableToQueue(calls);
                }

                //myMainModel.agentEvalPool.invokeAny(calls);
                List<Future<Object>> futures = mainModel.groupInteractionEvalPool.invokeAll(calls);
                for (int n = 0; n < futures.size(); n++) {
                    futures.get(n).get();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(ShamilGroupSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return grp;
    }

    public static void groupInteractionPersons(MainModel mainModel, ShamilGroup grp, ShamilAction action, Person person, Person acting_person, int acting_person_id, Double ACTION_AFFECTING_PROBABILITY, Double ACTION_INFECT_THRESHOLD) {
        for (int m = 0; m < person.insidePeople.size(); m++) {
            if (person.isExistAlive == true) {
                if (person.insidePeople.get(m).sfpp.isAlive == true) {
                    if (randn(mainModel, action.min_prob_affect, action.max_prob_affect) > ACTION_AFFECTING_PROBABILITY) {
//                if(randn(actn.min_prob_affect,actn.max_prob_affect)>ACTION_AFFECTING_PROBABILITY):
                        if (person.shamilPersonProperties.id == acting_person_id) {
                            if (action.min_effect_self >= 0) {

                                double infection = (1 - person.shamilPersonProperties.protectionLevel) * randn(mainModel, action.min_effect_self, action.max_effect_self);

                                // #print(infection)
                                if (infection > ACTION_INFECT_THRESHOLD) {
                                    person.insidePeople.get(m).sfpp.infectionLevel = Math.max(Math.min(person.insidePeople.get(m).sfpp.infectionLevel + infection, 1), 0);
                                    // #print("\n" + str(actn.name) + " the infection case for self infection is - " + str(prsn.infection_level));
//                                    System.out.println("INF INC 1");
//                                    System.out.println("INF INC 1: "+grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel);
                                    mainModel.ABM.shamilInf1.incrementAndGet();// += 1;
                                }
                            } else {

                                double infection = (1 - person.shamilPersonProperties.protectionLevel) * randn(mainModel, action.min_effect_self, action.max_effect_self);
                                // #print("the infection case is - " + str(infection))
                                double infection_pos = (-1) * infection;

                                if (infection_pos > ACTION_INFECT_THRESHOLD) {
                                    person.insidePeople.get(m).sfpp.infectionLevel = Math.max(Math.min(person.insidePeople.get(m).sfpp.infectionLevel + infection, 1), 0);
//                                    System.out.println("INF INC 2");
//                                    System.out.println("INF INC 2: "+grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel);
                                    mainModel.ABM.shamilInf2.incrementAndGet();// += 1;
                                }
                            }
                        } else {
                            int insidePersonIndex = (int) ((mainModel.ABM.root.rnd.nextDouble() * (acting_person.insidePeople.size() - 1)));
//                            for (int n = 0; n < acting_person.insidePeople.size(); n++) {
                            if (acting_person.insidePeople.get(insidePersonIndex).sfpp.state.equals("contagious_asymptomatic") || acting_person.insidePeople.get(insidePersonIndex).sfpp.state.equals("contagious_symptomatic")) {

                                if (person.insidePeople.get(m).sfpp.isInfected == false) {

                                    // #print("the people - " + str(acting_person_id) + " " +str(actn.person_id) + " " + str(prsn.id))
                                    double proximit = grp.getProximity(acting_person_id, person.shamilPersonProperties.id);
                                    // #proximit = 1

                                    double infection = (1 - person.shamilPersonProperties.protectionLevel) * randn(mainModel, action.min_effect_others, action.max_effect_others);
//                                infection = (1-grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(actn.min_effect_others,actn.max_effect_others)
                                    // #print("infection before = " + str(infection) + " proximity - " + str(proximit))

                                    infection = infection * proximit;

                                    if (infection > ACTION_INFECT_THRESHOLD) {
                                        person.insidePeople.get(m).sfpp.infectionLevel = Math.max(Math.min(person.insidePeople.get(m).sfpp.infectionLevel + infection, 1), 0);
                                        person.shamilPersonProperties.infectedBy = acting_person_id;
                                        person.shamilPersonProperties.infectedByUpdt += 1;
//                                            System.out.println("INF INC 3");
//                                            System.out.println("INF INC 3: "+grp.persons.get(j).insidePeople.get(m).sfpp.infectionLevel);
                                        mainModel.ABM.shamilInf3.incrementAndGet();// += 1;
                                    }
                                }
                            }
//                            }
                        }
                    }
                }
            }
        }
    }
}
