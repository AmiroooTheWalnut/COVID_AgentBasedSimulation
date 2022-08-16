/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root;
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
            for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                if (people.get(i).insidePeople.get(m).sfpp.isInfected == true && !people.get(i).insidePeople.get(m).sfpp.state.equals("recovered") && people.get(i).insidePeople.get(m).sfpp.isAlive == true) {
                    sumInfected = sumInfected + 1;
                }
            }
        }

        for (int i = 0; i < people.size(); i++) {
            for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                people.get(i).insidePeople.get(m).sfpp.infectionLevel = 0;
                if (day >= awareness_start) {
                    ShamilPersonManager.raiseAwareness(people.get(i));
                    ShamilPersonManager.becomeProtected(people.get(i), sumInfected);
                }
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
            } else {
                // isolated- no task - empty list
                people.get(i).shamilPersonProperties.tasks.clear();
            }
        }
    }

    public static void dayEnd(ArrayList<Person> persons, int num_of_day, int trace_days, int quarantine_days, ArrayList<HashMap<Integer, ArrayList<Integer>>> groupdetails, boolean isFuzzyStatus, double pTSFraction) {
// # fline = open("seed.txt").readline().rstrip()
        // # ranseed = int(fline)
        // # np.random.seed(ranseed)

        double INFECTION_PROBABILITY = thresholds_df.get("INFECTION_PROBABILITY");
//        INFECTION_PROBABILITY = dfToFloat(thresholds_df,"INFECTION_PROBABILITY")        
        double avgTravelsPerDay = 0;
        for (int i = 0; i < persons.size(); i++) { // prsn in persons:

            Person prsn = persons.get(i);
            avgTravelsPerDay = avgTravelsPerDay + prsn.numTravelsInDay;
            prsn.numTravelsInDay = 0;
//            String initialProfessionName=prsn.shamilPersonProperties.profession.name;
            int numInf=0;
            for (int m = 0; m < prsn.insidePeople.size(); m++) {
                if (prsn.insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_ASYM.ordinal() || prsn.insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_SYM.ordinal()) {
                    numInf+=1;
                }
            }
            boolean isInfected=false;
            if(numInf/prsn.insidePeople.size()>0.5){
                isInfected=true;
            }

            if (prsn.shamilPersonProperties.quarantinedDay > -1) {
                prsn.shamilPersonProperties.quarantinedDay += 1;
                if (prsn.shamilPersonProperties.quarantinedDay == quarantine_days && isInfected == false) {
                    prsn.shamilPersonProperties.quarantinedDay = -1;
                    prsn.shamilPersonProperties.profession = new ShamilProfession(prsn.shamilPersonProperties.initialProfession);
                }
            }
//            String endProfessionName1=prsn.shamilPersonProperties.profession.name;
//            if(endProfessionName1.equals("Hospitalized") && !initialProfessionName.equals("Hospitalized")){
//                 System.out.println("!!!!!!!!!!!!!!!!!!");
//            }
            for (int O = 0; O < prsn.insidePeople.size(); O++) {
            if (prsn.insidePeople.get(O).sfpp.isInfected == false) {

                if (prsn.insidePeople.get(O).sfpp.infectionLevel >= INFECTION_PROBABILITY) {
                    prsn.insidePeople.get(O).sfpp.isInfected = true;
                    prsn.insidePeople.get(O).sfpp.state = "Infected_notContagious";//.setState('Infected_notContagious')
//                    System.out.println("INFECTED BY SHAMIL");
                    // #print("\n\n\nNew Guy: {} by {} - Profession: {}".format(prsn.id, prsn.infected_by, prsn.profession))

                    // #if(prsn.profession=='Isolated'):
                    //     #print("Bad luck dude! Days: {} TaskCnt: {} QDAY: {}".format(prsn.infected_days, len(prsn.tasks), prsn.quarantined_day))
                    //     #print(prsn.tasks);
                }
            } else {

                String prsn_state = prsn.insidePeople.get(O).sfpp.state;//.getState()
                prsn.insidePeople.get(O).sfpp.infectedDays += 1;

                if (prsn_state.equals("contagious_symptomatic")) {
                    ShamilPersonManager.hospitalize(prsn, i);
//                    prsn.hospitalize();
                }

                ShamilPersonManager.die(prsn);
//                prsn.die()

                if (prsn_state.equals("Dead")) {
                    // #person dead do nothing but immunity and stuff deal later
                    //int dummy = 1;// NOT NEEDED
                } else {
                    if (prsn.insidePeople.get(O).sfpp.isAlive == false) {
                        prsn.insidePeople.get(O).sfpp.state = "Dead";
                        prsn.insidePeople.get(O).sfpp.isInfected = false;//ADDED BY AMIROOO
                    } else if (prsn.insidePeople.get(O).sfpp.infectedDays >= Math.round(Math.max(50, 50 + 15 * Math.random())) && prsn.insidePeople.get(O).sfpp.state.equals("recovered")) {//ADDED BY AMIROOO WAS 60
                        prsn.insidePeople.get(O).sfpp.state = "Not_infected";
                        prsn.insidePeople.get(O).sfpp.isInfected = false;
                        prsn.shamilPersonProperties.quarantinedDay = -1;
                    } else if (prsn.insidePeople.get(O).sfpp.infectedDays >= Math.round(Math.max(14, 14 + 10 * Math.random())) && (prsn.insidePeople.get(O).sfpp.state.equals("contagious_symptomatic") || prsn.insidePeople.get(O).sfpp.state.equals("contagious_asymptomatic"))) {//ADDED BY AMIROOO WAS 18
                        prsn.insidePeople.get(O).sfpp.state = "recovered";//ADDED BY AMIROOO
                        prsn.insidePeople.get(O).sfpp.isInfected = false;//ADDED BY AMIROOO
                    } else if (prsn.insidePeople.get(O).sfpp.infectedDays >= Math.round(Math.max(5, 5 + 2 * Math.random())) && prsn.insidePeople.get(O).sfpp.state.equals("contagious_asymptomatic")) {//EDITTED BY AMIROOO IT WAS 6
                        if (Math.random() > 0.5) {//ADDED BY AMIROOO
                            prsn.insidePeople.get(O).sfpp.state = "contagious_symptomatic";
                            prsn.insidePeople.get(O).sfpp.isInfected = true;
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
                                                if (grparray != null) {
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
                        }
                    } else if (prsn.insidePeople.get(O).sfpp.infectedDays >= Math.round(Math.max(3, 3 + 2 * Math.random())) && prsn.insidePeople.get(O).sfpp.state.equals("Infected_notContagious")) {//EDITTED BY AMIROOO IT WAS 4
                        prsn.insidePeople.get(O).sfpp.state = "contagious_asymptomatic";
                        prsn.insidePeople.get(O).sfpp.isInfected = true;
                    }
                }
            }
            }
//            String endProfessionName=prsn.shamilPersonProperties.profession.name;
//            if(endProfessionName.equals("Hospitalized") && !initialProfessionName.equals("Hospitalized")){
//                 System.out.println("!!!!!!!!!!!!!!!!!!");
//            }
        }
        avgTravelsPerDay = avgTravelsPerDay / (double) (persons.size());
        System.out.println("Average travels per day per agent: " + avgTravelsPerDay);
    }
}
