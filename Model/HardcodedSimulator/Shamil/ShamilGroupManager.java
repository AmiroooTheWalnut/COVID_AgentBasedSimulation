/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.rnd;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class ShamilGroupManager {

    public static Object[] assignGroups(ArrayList<Person> persons, double tracing_percentage, int num_of_day) {
        HashMap<Integer,ArrayList<Integer>> person_group = new HashMap();
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

    public static void updateActions(ShamilGroup grp) {

        //print('in')
        grp.updateActions();
        //print('in',grp.persons[0])
    }
}