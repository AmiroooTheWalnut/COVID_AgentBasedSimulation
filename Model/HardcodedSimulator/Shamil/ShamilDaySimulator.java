/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.thresholds_df;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class ShamilDaySimulator {

    public static void dayStart(ArrayList<Person> people, int day) {
        double awareness_start = ShamilPersonManager.preference_def.get("awareness_start");
        double quarantine_start = ShamilPersonManager.preference_def.get("quarantine_start");

        int sumInfected = 0;
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).shamilPersonProperties.isInfected == true && !people.get(i).shamilPersonProperties.state.equals("recovered") && people.get(i).shamilPersonProperties.isAlive == true) {
                sumInfected = sumInfected + 1;
            }
        }

        for (int i = 0; i < people.size(); i++) {
            people.get(i).shamilPersonProperties.infectionLevel = 0;
            if (day >= awareness_start) {
                ShamilPersonManager.raiseAwareness(people.get(i));
                ShamilPersonManager.becomeProtected(people.get(i), sumInfected);
            }
        }

        int service_holder_cnt = 0;
        ArrayList<Integer> service_holders = new ArrayList();
        int stdnt_cnt = 0;
        ArrayList<Integer> stdnts = new ArrayList();
        double quarantined_person_ratio = ShamilPersonManager.preference_def.get("quarantined_person_ratio");
        if (day == quarantine_start) {
            for (int i = 0; i < people.size(); i++) {
                if (people.get(i).shamilPersonProperties.profession.name.equals("Service")) {
                    service_holder_cnt += 1;
                    service_holders.add(people.get(i).shamilPersonProperties.id);
                } else if (people.get(i).shamilPersonProperties.profession.name.equals("Student")) {
                    stdnt_cnt += 1;
                    stdnts.add(people.get(i).shamilPersonProperties.id);
                }
            }
        }
        int quarantined_person_cnt = (int) (service_holder_cnt * quarantined_person_ratio);
        Collections.shuffle(service_holders);

        for (int i = 0; i < quarantined_person_cnt; i++) {
//            persons[service_holders[i]].profession = "Unemployed"
            people.get(service_holders.get(i)).shamilPersonProperties.profession = ShamilPersonManager.profession_df.get(3);
        }
        for (int i = 0; i < stdnt_cnt; i++) {
//            persons[stdnts[i]].profession = "Unemployed"
            people.get(stdnts.get(i)).shamilPersonProperties.profession = ShamilPersonManager.profession_df.get(3);
        }
    }

    public static void generateDailyTasks(ArrayList<Person> people, boolean lockdown_started) {
        for (int i = 0; i < people.size(); i++) {
            ShamilProfession prof_pers = people.get(i).shamilPersonProperties.profession;
            if (prof_pers.name.equals("Unemployed")) {
                double prob = Math.random();
                if (lockdown_started == true && prob < 0.7) {
                    people.get(i).shamilPersonProperties.tasks = ShamilTaskManager.generateTasks(new ShamilProfession("NoOutingAllowed", 0, 0, 0));
                } else {
                    people.get(i).shamilPersonProperties.tasks = ShamilTaskManager.generateTasks(ShamilPersonManager.profession_df.get(3));
                }
            } else if (prof_pers.name.equals("Doctor")) {
                people.get(i).shamilPersonProperties.tasks = ShamilTaskManager.generateTasks(ShamilPersonManager.profession_df.get(2));
//                prsn.setTasks(TaskManager.generateTasks(tasks_df[tasks_df["profession"]=="Doctor"]))
            } else if (prof_pers.name.equals("Student")) {
                people.get(i).shamilPersonProperties.tasks = ShamilTaskManager.generateTasks(ShamilPersonManager.profession_df.get(0));
//                prsn.setTasks(TaskManager.generateTasks(tasks_df[tasks_df["profession"]=="Student"]))
            } else if (prof_pers.name.equals("Service")) {
                people.get(i).shamilPersonProperties.tasks = ShamilTaskManager.generateTasks(ShamilPersonManager.profession_df.get(1));
//                prsn.setTasks(TaskManager.generateTasks(tasks_df[tasks_df["profession"]=="Service"]))
            } else if (prof_pers.name.equals("Hospitalized")) {//ADDED BY AMIROOO
                people.get(i).shamilPersonProperties.tasks = ShamilTaskManager.generateTasks(ShamilPersonManager.profession_df.get(4));
            }else{
                // isolated- no task - empty list
                people.get(i).shamilPersonProperties.tasks.clear();
            }
        }
    }

    public static void dayEnd(ArrayList<Person> persons, int num_of_day, int trace_days, int quarantine_days, ArrayList<HashMap<Integer, ArrayList<Integer>>> groupdetails) {
        // # fline = open("seed.txt").readline().rstrip()
        // # ranseed = int(fline)
        // # np.random.seed(ranseed)

        double INFECTION_PROBABILITY = thresholds_df.get("INFECTION_PROBABILITY");
//        INFECTION_PROBABILITY = dfToFloat(thresholds_df,"INFECTION_PROBABILITY")        

        for (int i = 0; i < persons.size(); i++) { // prsn in persons:
            Person prsn = persons.get(i);
            if (prsn.shamilPersonProperties.quarantinedDay > -1) {
                prsn.shamilPersonProperties.quarantinedDay += 1;
                if (prsn.shamilPersonProperties.quarantinedDay == quarantine_days && prsn.shamilPersonProperties.isInfected == false) {
                    prsn.shamilPersonProperties.quarantinedDay = -1;
                    prsn.shamilPersonProperties.profession = prsn.shamilPersonProperties.initialProfession;
                }
            }

            if (prsn.shamilPersonProperties.isInfected == false) {

                if (prsn.shamilPersonProperties.infectionLevel >= INFECTION_PROBABILITY) {
                    prsn.shamilPersonProperties.isInfected = true;
                    prsn.shamilPersonProperties.state = "Infected_notContagious";//.setState('Infected_notContagious')
                    // #print("\n\n\nNew Guy: {} by {} - Profession: {}".format(prsn.id, prsn.infected_by, prsn.profession))

                    // #if(prsn.profession=='Isolated'):
                    //     #print("Bad luck dude! Days: {} TaskCnt: {} QDAY: {}".format(prsn.infected_days, len(prsn.tasks), prsn.quarantined_day))
                    //     #print(prsn.tasks);
                }
            } else {

                String prsn_state = prsn.shamilPersonProperties.state;//.getState()
                prsn.shamilPersonProperties.infectedDays += 1;

                if (prsn_state.equals("contagious_symptomatic")) {
                    ShamilPersonManager.hospitalize(prsn);
//                    prsn.hospitalize();
                }

                ShamilPersonManager.die(prsn);
//                prsn.die()

                if (prsn_state.equals("Dead")) {
                    // #person dead do nothing but immunity and stuff deal later
                    //int dummy = 1;// NOT NEEDED
                } else {
                    if (prsn.shamilPersonProperties.isAlive == false) {
                        prsn.shamilPersonProperties.state = "Dead";
                        prsn.shamilPersonProperties.isInfected = false;//ADDED BY AMIROOO
                    } else if (prsn.shamilPersonProperties.infectedDays >= Math.round(Math.max(50, 50+15*Math.random())) && prsn.shamilPersonProperties.state.equals("recovered")) {//ADDED BY AMIROOO WAS 60
                        prsn.shamilPersonProperties.state = "Not_infected";
                        prsn.shamilPersonProperties.isInfected = false;
                        prsn.shamilPersonProperties.quarantinedDay = -1;
                    } else if (prsn.shamilPersonProperties.infectedDays >= Math.round(Math.max(14, 14+10*Math.random())) && (prsn.shamilPersonProperties.state.equals("contagious_symptomatic") || prsn.shamilPersonProperties.state.equals("contagious_asymptomatic"))) {//ADDED BY AMIROOO WAS 18
                        prsn.shamilPersonProperties.state = "recovered";//ADDED BY AMIROOO
                        prsn.shamilPersonProperties.isInfected = false;//ADDED BY AMIROOO
                    } else if (prsn.shamilPersonProperties.infectedDays >= Math.round(Math.max(5, 5+2*Math.random())) && prsn.shamilPersonProperties.state.equals("contagious_asymptomatic")) {//EDITTED BY AMIROOO IT WAS 6
                        if (Math.random() > 0.5) {//ADDED BY AMIROOO
                            prsn.shamilPersonProperties.state = "contagious_symptomatic";
                            prsn.shamilPersonProperties.isInfected=true;
                            // # elif(prsn.infected_days==1):
                            // #     prsn.setState('contagious_symptomatic')
                            // #contact trace
                            if (prsn.shamilPersonProperties.isTraceable == true) {
                                int TRACING_STARTS_ON_DAY = 21;
                                if (num_of_day >= TRACING_STARTS_ON_DAY) {
                                    int trace_start = Math.max(1, (num_of_day - trace_days + 1));
                                    int rangeday = num_of_day + 1;
                                    for (int numday = trace_start; numday < rangeday; numday++) {// numday in range(trace_start,rangeday):
//                                    filename = 'group_info_day_' + str(numday) + '.p'                    
//                                    groupfile = open(filename,'rb')
//                                    groupdetails = pickle.load(groupfile)
//                                    groupfile.close()

                                        for (int k = 0; k < groupdetails.size(); k++) { // hourgroup in groupdetails:
                                            double record_found_prob = Math.random();
                                            double PROBABILITY_OF_RECORD_EXISTING = 0.9;

//                                        record_found_prob = np.random.rand()
//                                        PROBABILITY_OF_RECORD_EXISTING = 0.9
                                            if (record_found_prob < PROBABILITY_OF_RECORD_EXISTING) {
                                                ArrayList<Integer> grparray = groupdetails.get(k).get(prsn.shamilPersonProperties.id); //[prsn.id]
                                                for (int m = 0; m < grparray.size(); m++) { // contactperson_id in grparray:
                                                    int contactperson_id = grparray.get(m);

                                                    // #contactperson.initial_profession = contactperson.profession
                                                    Person contactperson = persons.get(contactperson_id); //[contactperson_id]
                                                    double isolated_prob = Math.random();
                                                    if (isolated_prob < 0.5) {
                                                        contactperson.shamilPersonProperties.profession.name = "Isolated";
                                                    } else {
                                                        contactperson.shamilPersonProperties.profession.name = "Unemployed";
                                                    }
                                                    contactperson.shamilPersonProperties.quarantinedDay = 0;
                                                    // #print("{} has been tracked down!".format(contactperson.id))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (prsn.shamilPersonProperties.infectedDays >= Math.round(Math.max(3, 3+2*Math.random())) && prsn.shamilPersonProperties.state.equals("Infected_notContagious")) {//EDITTED BY AMIROOO IT WAS 4
                        prsn.shamilPersonProperties.state = "contagious_asymptomatic";
                        prsn.shamilPersonProperties.isInfected=true;
                    }
                }
            }
        }
    }
}
