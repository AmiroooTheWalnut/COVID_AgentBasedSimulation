/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.quarantine_days;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.trace_days;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.tracing_percentage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class ShamilSimulatorController {

    public static ArrayList<HashMap<Integer,ArrayList<Integer>>> daily_groups;
    public static int n_infected_init=200;
    
    public static void runRawShamilSimulation(ArrayList<Person> people, int numDaysToSimulate){
        shamilAgentGeneration(people);
        for(int d=0;d<numDaysToSimulate;d++){
            startDay(people,d);
            for(int h=0;h<24;h++){
                updateHour(people,h,d);
            }
            endDay(people,d);
        }
    }
    
    public static void runShamilSimulationOnly(ArrayList<Person> people, int numDaysToSimulate){
        for(int d=0;d<numDaysToSimulate;d++){
            startDay(people,d);
            for(int h=0;h<24;h++){
                updateHour(people,h,d);
            }
            endDay(people,d);
        }
    }
    
    public static void shamilAgentGeneration(ArrayList<Person> people) {
        //SHAMIL'S AGET GENERATION
        ShamilPersonManager.generatePersons(people);
        ShamilPersonManager.assignProfessionGroup(people);
    }
    
    public static void shamilInitialInfection(ArrayList<Person> people){
        ShamilPersonManager.initialInfection(people, n_infected_init);
    }
    
    

    public static void startDay(ArrayList<Person> people, int day) {
        for (int i = 0; i < people.size(); i++) {
            double rnd = Math.random();
            if (rnd < ShamilPersonManager.smartphone_owner_percentage) {
                people.get(i).shamilPersonProperties.isTraceable = true;
            } else {
                people.get(i).shamilPersonProperties.isTraceable = false;
            }
            if(people.get(i).shamilPersonProperties.profession==null){
                System.out.println("!!!!!!!!!!!");
            }
        }

        ShamilDaySimulator.dayStart(people, day);
        boolean lockdown_started = false;
        if (day >= ShamilPersonManager.preference_def.get("quarantine_start")) {
            lockdown_started = true;
        }
        ShamilDaySimulator.generateDailyTasks(people, lockdown_started);
        
        daily_groups=new ArrayList();
    }

    public static void updateHour(ArrayList<Person> people, int hour, int day) {
        for (int i = 0; i < people.size(); i++) {
            ShamilPersonManager.updateCurrentTask(people.get(i), hour);
        }
        ShamilHourSimulator.generateHourlyActions(people, hour);

        Object output[] = ShamilGroupManager.assignGroups(people, tracing_percentage, day);

        ArrayList<ShamilGroup> groups = (ArrayList<ShamilGroup>) output[0];
        HashMap<Integer,ArrayList<Integer>> person_group = (HashMap<Integer,ArrayList<Integer>>) output[1];

        int event_cnt = 0;
        int event_going_person_cnt = 0;
        for (int i = 0; i < groups.size(); i++) {// grp in groups:
            if ((groups.get(i).group_name).startsWith("E")) {
                event_cnt += 1;
                event_going_person_cnt += groups.get(i).persons.size();
            }
        }
        
        daily_groups.add(person_group);
        
        for(int i=0;i<groups.size();i++){
            groups.get(i).updateActions();
        }
        
        for(int i=0;i<groups.size();i++){
            ShamilGroupSimulator.groupInteraction(groups.get(i));
        }
        
        //pickle.dump(daily_groups,open('group_info_day_' + str(day) + '.p','wb'))
    }
    
    public static void endDay(ArrayList<Person> people, int day){
        ShamilDaySimulator.dayEnd(people, day, trace_days, quarantine_days, daily_groups);
    }
}
