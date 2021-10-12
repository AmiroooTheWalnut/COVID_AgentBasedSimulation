/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author user
 */
public class ShamilPersonManager {

    static Random rnd = new Random();

    static HashMap<String, Double> preference_def = new HashMap<String, Double>() {
        {
            put("min_age", 1d);
            put("max_age", 81d);
            put("min_name_length", 3d);
            put("max_name_length", 7d);
            put("min_family_size", 1d);
            put("max_family_size", 6d);
            put("n_workgroup", 600d);
            put("n_transport", 2500d);
            put("transport_seat_limit", 60d);
            put("n_events", 100d);
//        put("n_persons",10000d);
//        put("n_infected_init",252d);
            put("awareness_start", 7d);
            put("quarantine_start", 27d);
            put("quarantined_person_ratio", 0.5d);
        }
    };

    static ArrayList<ShamilProfession> profession_df = new ArrayList<ShamilProfession>() {
        {
            add(new ShamilProfession("Student", 4, 25, 0.22));
            add(new ShamilProfession("Service", 18, 62, 0.741));
            add(new ShamilProfession("Doctor", 25, 70, 0.019));
            add(new ShamilProfession("Unemployed", 10, 81, 0.02));
        }
    };

    public static void generatePersons(ArrayList<Person> persons) {
        int family_id = 0;
        int family_size_orig = 0;
        int family_size_counter = 0;
        int age = 0;
        int selectedProfession = -1;
        for (int i = 0; i < persons.size(); i++) {
            double professionRand = Math.random();
            if (professionRand < 0.22) {
                selectedProfession = 0;
            } else if (professionRand < 0.22 + 0.741) {
                selectedProfession = 1;
            } else if (professionRand < 0.22 + 0.741 + 0.019) {
                selectedProfession = 2;
            } else {
                selectedProfession = 3;
            }
            persons.get(i).shamilPersonProperties.profession = profession_df.get(selectedProfession);
            age = (int) (profession_df.get(selectedProfession).min_age + Math.random() * (profession_df.get(selectedProfession).max_age - profession_df.get(selectedProfession).min_age));
            if (family_size_counter == 0) {
                family_id = family_id + 1;
                family_size_orig = (int) Math.round(preference_def.get("min_family_size") + Math.random() * (preference_def.get("max_family_size") - preference_def.get("min_family_size")));
                family_size_counter = family_size_orig;
            }
            persons.get(i).shamilPersonProperties.familyId = family_id;
            persons.get(i).shamilPersonProperties.familySize = family_size_orig;

            persons.get(i).shamilPersonProperties.id = i;
            persons.get(i).shamilPersonProperties.name = rands();
            persons.get(i).shamilPersonProperties.age = age;
            if (family_size_counter > 0) {
                family_size_counter = family_size_counter - 1;
            }
        }
    }

    public static void assignProfessionGroup(ArrayList<Person> persons) {
        ArrayList<Integer> worker_ids = new ArrayList();
        for (int i = 0; i < persons.size(); i++) {
            if (persons.get(i).shamilPersonProperties.profession.name.equals("Service")) {
                worker_ids.add(persons.get(i).shamilPersonProperties.id);
            }
        }
        Collections.shuffle(worker_ids);
//        int n_workgroup = (preference_def.get("n_workgroup")).intValue();
        int n_workgroup = (int) (worker_ids.size() * (preference_def.get("n_workgroup") / 7410d));
        int worker_per_workgroup = (int) (Math.floorDiv(worker_ids.size(), n_workgroup));
        if (worker_per_workgroup == 0) {
            worker_per_workgroup = 1;
        }
        for (int i = 0; i < worker_ids.size(); i++) {
            persons.get(worker_ids.get(i)).shamilPersonProperties.professionGroupId = Math.min(Math.floorDiv(i, worker_per_workgroup), n_workgroup);
        }
    }

    public static String rands() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static void raiseAwareness(Person person) {
        if (rnd.nextGaussian() > 0.5) {
            person.shamilPersonProperties.awarenessLevel = Math.min(person.shamilPersonProperties.awarenessLevel + rnd.nextGaussian(), 1.0);
        }
    }

    public static void becomeProtected(Person person) {
        double randnum = rnd.nextGaussian();
        double extra_protection = 0.0;
        if (person.shamilPersonProperties.profession.name.equals("Hospitalized") || person.shamilPersonProperties.profession.name.equals("Doctor")) {
            if (randnum > 0.7) {
                person.shamilPersonProperties.protectionLevel = 0.5 + extra_protection;
            } else if (randnum > 0.3) {
                person.shamilPersonProperties.protectionLevel = 0.25 + extra_protection;
            } else {
                person.shamilPersonProperties.protectionLevel = 0.1 + extra_protection;
            }
        } else {
            if (randnum > 0.7) {
                person.shamilPersonProperties.protectionLevel = Math.min(rnd.nextGaussian()*person.shamilPersonProperties.awarenessLevel, 0.25 + extra_protection);
            }
        }
    }

}
