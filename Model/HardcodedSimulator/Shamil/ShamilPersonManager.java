/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.n_infected_init;
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

    static double smartphone_owner_percentage = 0.75;
//    static double tracing_percentage = 0.3;//ORIGINAL
    static double tracing_percentage = 0.1;
//    static int quarantine_days = 14;//ORIGINAL
    static int quarantine_days = 7;
    static int trace_days = 2;

    static HashMap<String, Double> preference_def = new HashMap<String, Double>() {
        {
            put("min_age", 1d);
            put("max_age", 81d);
            put("min_name_length", 3d);
            put("max_name_length", 7d);
            put("min_family_size", 1d);
            put("max_family_size", 6d);
            put("n_workgroup", 600d);
//            put("n_transport", 2500d);//ORIGINAL
            put("n_transport", 15000d);
//            put("transport_seat_limit", 60d);//ORIGINAL
            put("transport_seat_limit", 30d);
            put("n_events", 100d);
//        put("n_persons",10000d);
//        put("n_infected_init",252d);
//            put("awareness_start", 7d);//ORIGINAL
            put("awareness_start", 0d);
            put("quarantine_start", 0d);
//            put("quarantined_person_ratio", 0.5d);//ORIGINAL
            put("quarantined_person_ratio", 0.1d);
        }
    };

    static ArrayList<ShamilProfession> profession_df = new ArrayList<ShamilProfession>() {
        {
            add(new ShamilProfession("Student", 4, 25, 0.22));
            add(new ShamilProfession("Service", 18, 62, 0.741));
            add(new ShamilProfession("Doctor", 25, 70, 0.019));
            add(new ShamilProfession("Unemployed", 10, 81, 0.02));
            add(new ShamilProfession("Hospitalized", 4, 81, 0));
        }
    };

    static HashMap<String, Double> thresholds_df = new HashMap<String, Double>() {
        {
//            put("ACTION_OCCURRING_PROBABILITY", 0.55);//ORIGINAL
//            put("ACTION_AFFECTING_PROBABILITY", 0.55);//ORIGINAL
            put("ACTION_OCCURRING_PROBABILITY", 0.45);
            put("ACTION_AFFECTING_PROBABILITY", 0.45);
            //put("ACTION_INFECT_THRESHOLD", 0.45);//ORIGINAL
            put("ACTION_INFECT_THRESHOLD", 0.35);
            //put("INFECTION_PROBABILITY", 0.55);//ORIGINAL
            put("INFECTION_PROBABILITY", 0.55);
            put("PROTECTION_LEVEL_THRESH", 0.2);
        }
    };

//    static ArrayList<ShamilTemplateAction> actions_def = new ArrayList<ShamilTemplateAction>() {
//        {
//            add(new ShamilTemplateAction("Work", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Work", "Contaminate Thing", 50, 55, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Work", "Physical Contact", 20, 30, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Work", "Wash Hands", 30, 40, 0.5, 1.0, 0.1, 0.7, 0.0, 0.0, -0.75, -0.1));
//            add(new ShamilTemplateAction("Work", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//            add(new ShamilTemplateAction("Work", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//
//            add(new ShamilTemplateAction("Attend Event", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.605, 0.1, 0.8, 0.0, 0.0));
//            add(new ShamilTemplateAction("Attend Event", "Contaminate Thing", 20, 30, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Attend Event", "Physical Contact", 20, 30, 0.2, 0.8, 0.2, 0.8, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Attend Event", "Wash Hands", 30, 40, 0.4, 1.0, 0.1, 0.7, 0.0, 0.0, -0.75, -0.1));
//            add(new ShamilTemplateAction("Attend Event", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//            add(new ShamilTemplateAction("Attend Event", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//
//            add(new ShamilTemplateAction("Go to Work", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Go to Work", "Contaminate Thing", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Go to Work", "Physical Contact", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Go to Work", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//            add(new ShamilTemplateAction("Go to Work", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//
//            add(new ShamilTemplateAction("Returns Home", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Returns Home", "Contaminate Thing", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Returns Home", "Physical Contact", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Returns Home", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//            add(new ShamilTemplateAction("Returns Home", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
//
//            add(new ShamilTemplateAction("Stay Hospital", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Stay Hospital", "Contaminate Thing", 50, 55, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Stay Hospital", "Physical Contact", 20, 30, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
//
//            add(new ShamilTemplateAction("Treat Patients", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Treat Patients", "Physical Contact", 20, 30, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
//            add(new ShamilTemplateAction("Treat Patients", "Wash Hands", 20, 40, 0.4, 1.0, 0.2, 0.7, 0.0, 0.0, -0.75, -0.1));
//            add(new ShamilTemplateAction("Treat Patients", "Contaminate Thing", 50, 55, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
//
//            add(new ShamilTemplateAction("Stay Home", "Sleep", 1000, 1000, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0));
//            add(new ShamilTemplateAction("Stay Home", "Wash Hands", 30, 40, 0.5, 1.0, 0.2, 0.7, 0.0, 0.0, -0.75, -0.1));
//        }
//    };
    static HashMap<String, ArrayList<ShamilTemplateAction>> actions_def = new HashMap<String, ArrayList<ShamilTemplateAction>>() {
        {
            put("Work", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Work", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Work", "Contaminate Thing", 50, 55, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Work", "Physical Contact", 20, 30, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Work", "Wash Hands", 30, 40, 0.5, 1.0, 0.1, 0.7, 0.0, 0.0, -0.75, -0.1));
                    add(new ShamilTemplateAction("Work", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                    add(new ShamilTemplateAction("Work", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                }
            });
            put("Attend Event", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Attend Event", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.605, 0.1, 0.8, 0.0, 0.0));
                    add(new ShamilTemplateAction("Attend Event", "Contaminate Thing", 20, 30, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Attend Event", "Physical Contact", 20, 30, 0.2, 0.8, 0.2, 0.8, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Attend Event", "Wash Hands", 30, 40, 0.4, 1.0, 0.1, 0.7, 0.0, 0.0, -0.75, -0.1));
                    add(new ShamilTemplateAction("Attend Event", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                    add(new ShamilTemplateAction("Attend Event", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                }
            });
            put("Go to Work", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Go to Work", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Go to Work", "Contaminate Thing", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Go to Work", "Physical Contact", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Go to Work", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                    add(new ShamilTemplateAction("Go to Work", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                }
            });
            put("Returns Home", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Returns Home", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Returns Home", "Contaminate Thing", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Returns Home", "Physical Contact", 20, 30, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Returns Home", "Got Caught in Sneeze", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                    add(new ShamilTemplateAction("Returns Home", "Touch Contaminate Thing", 5, 30, 0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                }
            });
            put("Stay Hospital", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Stay Hospital", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.7, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Stay Hospital", "Contaminate Thing", 50, 55, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Stay Hospital", "Physical Contact", 20, 30, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
                }
            });
            put("Treat Patients", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Treat Patients", "Sneeze", 40, 50, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Treat Patients", "Physical Contact", 20, 30, 0.1, 0.8, 0.1, 0.8, 0.1, 0.705, 0.0, 0.0));
                    add(new ShamilTemplateAction("Treat Patients", "Wash Hands", 20, 40, 0.4, 1.0, 0.2, 0.7, 0.0, 0.0, -0.75, -0.1));
                    add(new ShamilTemplateAction("Treat Patients", "Contaminate Thing", 50, 55, 0.1, 0.7, 0.1, 0.605, 0.1, 0.705, 0.0, 0.0));
                }
            });
            put("Stay Home", new ArrayList<ShamilTemplateAction>() {
                {
                    add(new ShamilTemplateAction("Stay Home", "Sleep", 1000, 1000, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0));
                    add(new ShamilTemplateAction("Stay Home", "Wash Hands", 30, 40, 0.5, 1.0, 0.2, 0.7, 0.0, 0.0, -0.75, -0.1));
                }
            });
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
//            persons.get(i).shamilPersonProperties.profession = profession_df.get(selectedProfession);

            persons.get(i).shamilPersonProperties.profession = new ShamilProfession(profession_df.get(selectedProfession));//ADDED "NEW" TO MAKE SURE A DEEP COPY IS TAKEN
            persons.get(i).shamilPersonProperties.initialProfession = new ShamilProfession(persons.get(i).shamilPersonProperties.profession);

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

    /*
    *   Added by Amirooo
     */
    public static void generatePersonsSpatial(ArrayList<Region> regions) {
        int family_id = 0;
        int family_size_orig = 0;
        int family_size_counter = 0;
        int age = 0;
        int selectedProfession = -1;
        int uniqurCounter = 0;//ADDED BY AMIROOO
        for (int r = 0; r < regions.size(); r++) {
            for (int i = 0; i < regions.get(r).residents.size(); i++) {
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
//            persons.get(i).shamilPersonProperties.profession = profession_df.get(selectedProfession);

                regions.get(r).residents.get(i).shamilPersonProperties.profession = new ShamilProfession(profession_df.get(selectedProfession));//ADDED "NEW" TO MAKE SURE A DEEP COPY IS TAKEN
                regions.get(r).residents.get(i).shamilPersonProperties.initialProfession = new ShamilProfession(regions.get(r).residents.get(i).shamilPersonProperties.profession);

                age = (int) (profession_df.get(selectedProfession).min_age + Math.random() * (profession_df.get(selectedProfession).max_age - profession_df.get(selectedProfession).min_age));
                if (family_size_counter == 0) {
                    family_id = family_id + 1;
                    family_size_orig = (int) Math.round(preference_def.get("min_family_size") + Math.random() * (preference_def.get("max_family_size") - preference_def.get("min_family_size")));
                    family_size_counter = family_size_orig;
                }
                regions.get(r).residents.get(i).shamilPersonProperties.familyId = family_id;
                regions.get(r).residents.get(i).shamilPersonProperties.familySize = family_size_orig;

                regions.get(r).residents.get(i).shamilPersonProperties.id = uniqurCounter;
                uniqurCounter += 1;
                regions.get(r).residents.get(i).shamilPersonProperties.name = rands();
                regions.get(r).residents.get(i).shamilPersonProperties.age = age;
                if (family_size_counter > 0) {
                    family_size_counter = family_size_counter - 1;
                }
            }
        }
    }

    public static void assignProfessionGroupSpatial(ArrayList<Region> regions, ArrayList<Person> persons) {
        ArrayList<Integer> worker_ids = new ArrayList();
        for (int r = 0; r < regions.size(); r++) {
            for (int i = 0; i < regions.get(r).residents.size(); i++) {
                if (regions.get(r).residents.get(i).shamilPersonProperties.profession.name.equals("Service")) {
                    worker_ids.add(regions.get(r).residents.get(i).shamilPersonProperties.id);
                }
            }
        }
        Collections.shuffle(worker_ids);
//        int n_workgroup = (preference_def.get("n_workgroup")).intValue();
//        int n_workgroup = (int) (worker_ids.size() * (preference_def.get("n_workgroup") / 7410d));//ORIGINAL
        int n_workgroup = (int) (worker_ids.size() * (preference_def.get("n_workgroup") / 5000d));//ORIGINAL
        if (n_workgroup == 0) {
            n_workgroup = 1;
        }

        int worker_per_workgroup = (int) (Math.floorDiv(worker_ids.size(), n_workgroup));
        if (worker_per_workgroup == 0) {
            worker_per_workgroup = 1;
        }
        for (int i = 0; i < worker_ids.size(); i++) {
            persons.get(worker_ids.get(i)).shamilPersonProperties.professionGroupId = Math.min(Math.floorDiv(i, worker_per_workgroup), n_workgroup);
        }
//        for (int i = 0; i < persons.size(); i++) {
//            System.out.println(persons.get(i).shamilPersonProperties.professionGroupId);
//        }

//        System.out.println("n_workgroup: " + n_workgroup);
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
        if (n_workgroup == 0) {
            n_workgroup = 1;
        }
        int worker_per_workgroup = (int) (Math.floorDiv(worker_ids.size(), n_workgroup));
        if (worker_per_workgroup == 0) {
            worker_per_workgroup = 1;
        }
        for (int i = 0; i < worker_ids.size(); i++) {
            persons.get(worker_ids.get(i)).shamilPersonProperties.professionGroupId = Math.min(Math.floorDiv(i, worker_per_workgroup), n_workgroup);
        }
    }

    public static void initialInfection(ArrayList<Person> people, int n_infected_init) {
        while (n_infected_init > 0) {

            int person_id = (int) (Math.random() * people.size()); //np.random.randint(0,len(persons))

            while (people.get(person_id).shamilPersonProperties.isInfected == true) {

                person_id = (int) (Math.random() * people.size());

            }

            people.get(person_id).shamilPersonProperties.isInfected = true;
            people.get(person_id).shamilPersonProperties.state = "Infected_notContagious";
            //print('Person {} is initially infected'.format(person_id))
            n_infected_init -= 1;
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
        if (randn(0, 1) > 0.5) {
            person.shamilPersonProperties.awarenessLevel = Math.min(person.shamilPersonProperties.awarenessLevel + randn(0, 1), 1.0);
        }
    }

    public static void becomeProtected(Person person, int numInfection) {//numInfection IS ADDED BY AMIROOO FOR DEBUG
        double randnum = randn(0, 1);
        double extra_protection = 0.0;
//        if(numInfection==0){//ADDED BY AMIROOO
//            System.out.println("!!!!!!!!!");//ADDED BY AMIROOO
//        }//ADDED BY AMIROOO
//        if(person==null){//ADDED BY AMIROOO
//            System.out.println("!!!!!!!!!");//ADDED BY AMIROOO
//        }//ADDED BY AMIROOO
//        if(person.shamilPersonProperties.profession==null){//ADDED BY AMIROOO
//            System.out.println("!!!!!!!!!");//ADDED BY AMIROOO
//        }//ADDED BY AMIROOO
//        if(person.shamilPersonProperties.profession.name==null){//ADDED BY AMIROOO
//            System.out.println("!!!!!!!!!");//ADDED BY AMIROOO
//        }//ADDED BY AMIROOO
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
                person.shamilPersonProperties.protectionLevel = Math.min(randn(0, person.shamilPersonProperties.awarenessLevel), 0.25 + extra_protection);
            }
        }
    }

    public static void updateCurrentTask(Person person, int hour) {
        for (int i = 0; i < person.shamilPersonProperties.tasks.size(); i++) {
            if (person.shamilPersonProperties.tasks.get(i).start_time <= hour && hour < person.shamilPersonProperties.tasks.get(i).end_time) {
                person.shamilPersonProperties.currentTask = person.shamilPersonProperties.tasks.get(i);
                return;
            }
        }
    }

    public static void hospitalize(Person person) {
        if (person.shamilPersonProperties.isInfected == true && !(person.shamilPersonProperties.profession.name.equals("Hospitalized"))) {
            double tmp = Math.random();
            if (tmp > 0.75) {

                person.shamilPersonProperties.profession.name = "Hospitalized";
                //print('Person {} has been hospitalized'.format(self.id))
            }
        }
    }

    public static void die(Person person) {
        if (person.shamilPersonProperties.infectedDays > 15) {
            if (Math.random() > 0.97) {
                person.shamilPersonProperties.isAlive = false;
                //print('Person {} has died'.format(self.id));
            }
        }
    }

    public static double randn(double lo, double hi) {
        //# fline = open("seed.txt").readline().rstrip()
        //# ranseed = int(fline)
        //# np.random.seed(ranseed)
        return Math.random() * (hi - lo) + lo;
    }

}
