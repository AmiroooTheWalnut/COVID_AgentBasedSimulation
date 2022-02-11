/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.rnd;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class ShamilGroupManager {

    public static Object[] assignGroups(ArrayList<Person> persons, double tracing_percentage, int num_of_day) {
        HashMap<Integer, ArrayList<Integer>> person_group = new HashMap();
//        person_group = {}
        //# fline = open("seed.txt").readline().rstrip()
        //# ranseed = int(fline)
        //# np.random.seed(ranseed)
        //# random.seed(ranseed)
        HashMap<String, ShamilGroup> groupDict = new HashMap();
//        groupDict = {}

        ArrayList transport_free_seats = new ArrayList();
//        transport_free_seats = []

        int n_transport = (ShamilPersonManager.preference_def.get("n_transport")).intValue();
//        n_transport = dfToInt(preferences_df, "n_transport")
        int transport_seat_limit = (ShamilPersonManager.preference_def.get("transport_seat_limit")).intValue();
//        transport_seat_limit = dfToInt(preferences_df, "transport_seat_limit")
        int n_events = (ShamilPersonManager.preference_def.get("n_events")).intValue();
//        n_events = dfToInt(preferences_df, "n_events")
        int quarantine_start = (ShamilPersonManager.preference_def.get("quarantine_start")).intValue();
//        quarantine_start = dfToInt(preferences_df, "quarantine_start")

        for (int i = 0; i < n_transport; i++) {// i in range(n_transport):            
            ArrayList<Integer> temp = new ArrayList();
            for (int j = 0; j < transport_seat_limit; j++) {
                temp.add(i);
            }
            transport_free_seats.addAll(temp);
//            transport_free_seats.extend([i]*transport_seat_limit)
        }

        for (int i = 0; i < persons.size(); i++) {
//        for prsn in persons:

            if (persons.get(i).shamilPersonProperties.isAlive == false) {
//            if not prsn.is_alive:

                continue;
            }
            String group_id = "";

            if (persons.get(i).shamilPersonProperties.currentTask.name.equals("Stay Home")) {
//            if(prsn.current_task.name=="Stay Home"):
                group_id = "F-" + persons.get(i).shamilPersonProperties.familyId;
//                group_id = "F-{}".format(prsn.family_id)
            } else if (persons.get(i).shamilPersonProperties.currentTask.name.equals("Go to Work") || persons.get(i).shamilPersonProperties.currentTask.name.equals("Returns Home")) {
                int selected_transport = (int) (Math.random() * transport_free_seats.size());
//                selected_transport = random.choice(transport_free_seats)

                transport_free_seats.remove(selected_transport);
//                transport_free_seats.remove(selected_transport)

                group_id = "T-" + selected_transport;
//                group_id = "T-{}".format(selected_transport);
            } else if (persons.get(i).shamilPersonProperties.currentTask.name.equals("Work")) {
                group_id = "W-" + persons.get(i).shamilPersonProperties.professionGroupId;
//                group_id = "W-{}".format(prsn.profession_group_id)                

            } else if (persons.get(i).shamilPersonProperties.currentTask.name.equals("Attend Event")) {
                group_id = "E-" + (int) (Math.round(Math.random() * n_events));
//                group_id = "E-{}".format(np.random.randint(0,n_events))
                /*
                effort = 0
                while True:
                    group_id = "E-{}".format(np.random.randint(0,n_events))
                    if(num_of_day >= quarantine_start):
                        if(group_id in groupDict):
                            if(len(groupDict[group_id].persons)<15):
                                break
                        else:
                            break
                    else:
                        break

                    effort +=1
                    if(effort==3):
                        break
                 */

            } else if (persons.get(i).shamilPersonProperties.currentTask.name.equals("Stay Hospital")) {
                group_id = "H";
            } else if (persons.get(i).shamilPersonProperties.currentTask.name.equals("Treat Patients")) {
                group_id = "H";
            }

            if (!groupDict.containsKey(group_id) && group_id.length() > 0) {//group_id not in groupDict):
                groupDict.put(group_id, new ShamilGroup(group_id));
//                groupDict[group_id] = Group(group_id);
            }
            groupDict.get(group_id).persons.add(persons.get(i));
//            groupDict[group_id].addPerson(prsn)
        }

        ArrayList<String> groupDictKeySet = new ArrayList<>(groupDict.keySet());
        for (int i = 0; i < groupDictKeySet.size(); i++) { // grpid in groupDict:
            ShamilGroup grouparr = groupDict.get(groupDictKeySet.get(i));
//            grouparr = groupDict[grpid]
            ArrayList<Person> personarr = grouparr.persons;

            ArrayList<Integer> personid_arr = new ArrayList();
            for (int j = 0; j < personarr.size(); j++) { // pers in personarr:
                double decision_var = Math.random();
                if (decision_var < tracing_percentage && personarr.get(j).shamilPersonProperties.isTraceable) {
                    personid_arr.add(personarr.get(j).shamilPersonProperties.id);
                }
            }

            for (int j = 0; j < personarr.size(); j++) {// prsn in personarr:
                // #print(prsn.id)
                // #person_group[prsn.id] = personarr
                person_group.put(personarr.get(j).shamilPersonProperties.id, personid_arr);//[prsn.id] = personid_arr

            }

            // #print(person_group)
        }

        ArrayList<ShamilGroup> groups = new ArrayList();

        for (int i = 0; i < groupDictKeySet.size(); i++) {// grp_id in groupDict:

            groups.add(groupDict.get(groupDictKeySet.get(i)));
        }

        groupDict = new HashMap();

        for (int i = 0; i < groups.size(); i++) {// grp in groups:

            groups.get(i).updatePersonMapper();

            groups.get(i).updateProximity();

        }

        Object output[] = new Object[2];
        output[0] = groups;
        output[1] = person_group;
        return output;

    }

    public static Object[] assignGroupsSpatial(ArrayList<Region> regions, double tracing_percentage, int num_of_day, boolean debug, MainModel myMainModel) {
        HashMap<Integer, ArrayList<Integer>> person_group = new HashMap();
//        person_group = {}
        //# fline = open("seed.txt").readline().rstrip()
        //# ranseed = int(fline)
        //# np.random.seed(ranseed)
        //# random.seed(ranseed)
        HashMap<String, ShamilGroup> groupDict = new HashMap();
//        groupDict = {}

        ArrayList<Integer> transport_free_seats = new ArrayList();
//        transport_free_seats = []

        int n_transport = (ShamilPersonManager.preference_def.get("n_transport")).intValue();
//        n_transport = dfToInt(preferences_df, "n_transport")
        int transport_seat_limit = (ShamilPersonManager.preference_def.get("transport_seat_limit")).intValue();
//        transport_seat_limit = dfToInt(preferences_df, "transport_seat_limit")
        int n_events = (ShamilPersonManager.preference_def.get("n_events")).intValue();
//        n_events = dfToInt(preferences_df, "n_events")
        int quarantine_start = (ShamilPersonManager.preference_def.get("quarantine_start")).intValue();
//        quarantine_start = dfToInt(preferences_df, "quarantine_start")

        for (int i = 0; i < n_transport; i++) {// i in range(n_transport):            
            ArrayList<Integer> temp = new ArrayList();
            for (int j = 0; j < transport_seat_limit; j++) {
                temp.add(i);
            }
            transport_free_seats.addAll(temp);
//            transport_free_seats.extend([i]*transport_seat_limit)
        }

        for (int r = 0; r < regions.size(); r++) {
            for (int i = 0; i < regions.get(r).residents.size(); i++) {
//        for prsn in persons:

                if (regions.get(r).residents.get(i).shamilPersonProperties.isAlive == false) {
//            if not prsn.is_alive:

                    continue;
                }
                String group_id = "";

//                if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask != null) {
                if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Stay Home")) {
//            if(prsn.current_task.name=="Stay Home"):
                    group_id = "F-" + regions.get(r).residents.get(i).shamilPersonProperties.familyId;
//                group_id = "F-{}".format(prsn.family_id)
                } else if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Go to Work") || regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Returns Home")) {
                    int selected_transport_index = (int) (Math.random() * transport_free_seats.size());
                    int selected_transport = transport_free_seats.get(selected_transport_index);
//                selected_transport = random.choice(transport_free_seats)

                    transport_free_seats.remove(selected_transport);
//                transport_free_seats.remove(selected_transport)

                    group_id = "T-" + selected_transport;
//                group_id = "T-{}".format(selected_transport);
                } else if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Work")) {
                    group_id = "W-" + regions.get(r).residents.get(i).shamilPersonProperties.professionGroupId;
//                group_id = "W-{}".format(prsn.profession_group_id)                

                } else if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Attend Event")) {
                    group_id = "E-" + (int) (Math.round(Math.random() * n_events));
//                group_id = "E-{}".format(np.random.randint(0,n_events))
                    /*
                effort = 0
                while True:
                    group_id = "E-{}".format(np.random.randint(0,n_events))
                    if(num_of_day >= quarantine_start):
                        if(group_id in groupDict):
                            if(len(groupDict[group_id].persons)<15):
                                break
                        else:
                            break
                    else:
                        break

                    effort +=1
                    if(effort==3):
                        break
                     */

                } else if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Stay Hospital")) {
                    group_id = "H";
                } else if (regions.get(r).residents.get(i).shamilPersonProperties.currentTask.name.equals("Treat Patients")) {
                    group_id = "H";
                }

                if (!groupDict.containsKey(group_id) && group_id.length() > 0) {//group_id not in groupDict):
                    groupDict.put(group_id, new ShamilGroup(group_id));
//                groupDict[group_id] = Group(group_id);
                }
                groupDict.get(group_id).persons.add(regions.get(r).residents.get(i));
//            groupDict[group_id].addPerson(prsn)
//                }
            }
        }

        ArrayList<String> groupDictKeySet = new ArrayList<>(groupDict.keySet());
        for (int i = 0; i < groupDictKeySet.size(); i++) { // grpid in groupDict:
            ShamilGroup grouparr = groupDict.get(groupDictKeySet.get(i));
//            grouparr = groupDict[grpid]
            ArrayList<Person> personarr = grouparr.persons;

            ArrayList<Integer> personid_arr = new ArrayList();
            for (int j = 0; j < personarr.size(); j++) { // pers in personarr:
                double decision_var = Math.random();
                if (decision_var < tracing_percentage && personarr.get(j).shamilPersonProperties.isTraceable) {
                    personid_arr.add(personarr.get(j).shamilPersonProperties.id);
                }
            }

            for (int j = 0; j < personarr.size(); j++) {// prsn in personarr:
                // #print(prsn.id)
                // #person_group[prsn.id] = personarr
                person_group.put(personarr.get(j).shamilPersonProperties.id, personid_arr);//[prsn.id] = personid_arr

            }

            // #print(person_group)
        }

        ArrayList<ShamilGroup> groups = new ArrayList();

        for (int i = 0; i < groupDictKeySet.size(); i++) {// grp_id in groupDict:

            groups.add(groupDict.get(groupDictKeySet.get(i)));
        }

//        groupDict = new HashMap();// groupDict = {}
        try {
            int numProcessors = myMainModel.numCPUs;

            AdvancedParallelGroupUpdateEvaluator parallelGroupEval[] = new AdvancedParallelGroupUpdateEvaluator[numProcessors];

            for (int i = 0; i < numProcessors - 1; i++) {
                parallelGroupEval[i] = new AdvancedParallelGroupUpdateEvaluator(myMainModel, groups, (int) Math.floor(i * ((groups.size()) / numProcessors)), (int) Math.floor((i + 1) * ((groups.size()) / numProcessors)));
            }
            parallelGroupEval[numProcessors - 1] = new AdvancedParallelGroupUpdateEvaluator(myMainModel, groups, (int) Math.floor((numProcessors - 1) * ((groups.size()) / numProcessors)), groups.size());

            ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();

            for (int i = 0; i < numProcessors; i++) {
                parallelGroupEval[i].addRunnableToQueue(calls);
            }

            myMainModel.agentEvalPool.invokeAny(calls);
        } catch (InterruptedException ex) {
            Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //SERIAL EVAULATION OF GROUP UPDATE
//        for (int i = 0; i < groups.size(); i++) {// grp in groups:
//
//            if (debug == true) {
//                if (groups.get(i).persons.size() > 50 && groups.get(i).group_name.contains("T")) {
//                    System.out.println("GROUP NAME: " + groups.get(i).group_name);
//                    System.out.println("GROUP SIZE: " + groups.get(i).persons.size());
//
//                    System.out.println("@@@@@@@@@@@");
//                }
//            }
//
//            groups.get(i).updatePersonMapper();
//
//            groups.get(i).updateProximity();
//
//        }
        //SERIAL EVAULATION OF GROUP UPDATE

        Object output[] = new Object[2];
        output[0] = groups;
        output[1] = person_group;
        return output;

    }

    public static void updateActions(ShamilGroup grp) {

        //print('in')
        grp.updateActions();
        //print('in',grp.persons[0])
    }
}
