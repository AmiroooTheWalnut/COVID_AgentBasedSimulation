/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author user
 */
public class ShamilDaySimulator {

    public static void dayStart(ArrayList<Person> people, int day) {
        double awareness_start = ShamilPersonManager.preference_def.get("awareness_start");
        double quarantine_start = ShamilPersonManager.preference_def.get("quarantine_start");
        for (int i = 0; i < people.size(); i++) {
            people.get(i).shamilPersonProperties.infectionLevel = 0;
            if (day >= awareness_start) {
                ShamilPersonManager.raiseAwareness(people.get(i));
                ShamilPersonManager.becomeProtected(people.get(i));
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
            }
            
            
            
            
            
            
            
            
            
            
        }
    }

    public static void dayEnd(ArrayList<Person> people, int day) {
        
    }
}
