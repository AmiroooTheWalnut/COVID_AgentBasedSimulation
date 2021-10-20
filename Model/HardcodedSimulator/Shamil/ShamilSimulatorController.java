/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.quarantine_days;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.trace_days;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.tracing_percentage;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root.statusEnum;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class ShamilSimulatorController {

    public static ArrayList<HashMap<Integer, ArrayList<Integer>>> daily_groups;
    public static int n_infected_init = 200;

    public static void convertShamilToOur(ArrayList<Person> people) {
//        int inf=0;
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).shamilPersonProperties.currentTask.name.equals("Stay Home")) {
                people.get(i).properties.isAtHome = true;
                people.get(i).properties.isAtWork = false;
            }
            if (people.get(i).shamilPersonProperties.currentTask.name.equals("Go to Work") || people.get(i).shamilPersonProperties.currentTask.name.equals("Work") || people.get(i).shamilPersonProperties.currentTask.name.equals("Returns Home") || people.get(i).shamilPersonProperties.currentTask.name.equals("Treat Patients")) {
                people.get(i).properties.isAtHome = false;
                people.get(i).properties.isAtWork = true;
            }
            switch (people.get(i).shamilPersonProperties.state) {
                case "Not_infected":
                    people.get(i).properties.status = statusEnum.SUSCEPTIBLE.ordinal();
                    break;
                case "Infected_notContagious":
                    people.get(i).properties.status = statusEnum.SUSCEPTIBLE.ordinal();
                    break;
                case "contagious_symptomatic":
                    people.get(i).properties.status = statusEnum.INFECTED_SYM.ordinal();
//                inf+=1;
                    break;
                case "contagious_asymptomatic":
                    people.get(i).properties.status = statusEnum.INFECTED_ASYM.ordinal();
//                inf+=1;
                    break;
                case "Dead":
                    people.get(i).properties.status = statusEnum.DEAD.ordinal();
                    break;
                case "recovered":
                    people.get(i).properties.status = statusEnum.RECOVERED.ordinal();
                    break;
                default:
                    break;
            }
        }
//        System.out.println("convertOurToShamil INF: "+inf);
    }

    public static void convertOurToShamil(ArrayList<Person> people) {
//        int inf=0;
        for (int i = 0; i < people.size(); i++) {
            switch (people.get(i).properties.status) {
                case 0:
                    people.get(i).shamilPersonProperties.state = "Not_infected";
                    break;
                case 1:
                    people.get(i).shamilPersonProperties.state = "contagious_symptomatic";
                    people.get(i).shamilPersonProperties.isInfected=true;
//                inf+=1;
                    break;
                case 2:
                    people.get(i).shamilPersonProperties.state = "contagious_asymptomatic";
                    people.get(i).shamilPersonProperties.isInfected=true;
//                inf+=1;
                    break;
                case 3:
                    people.get(i).shamilPersonProperties.state = "recovered";
                    break;
                case 4:
                    people.get(i).shamilPersonProperties.state = "Dead";
                    break;
                default:
                    break;
            }
        }
//        System.out.println("convertOurToShamil INF: "+inf);
    }

    public static void runRawShamilSimulation(ArrayList<Person> people, int numDaysToSimulate) {
        shamilAgentGeneration(people);
        for (int d = 0; d < numDaysToSimulate; d++) {
            startDay(people, d);
            for (int h = 0; h < 24; h++) {
                updateHour(people, h, d);
            }
            endDay(people, d);
        }
    }

    public static void runShamilSimulationOnly(ArrayList<Person> people, int numDaysToSimulate) {
        for (int d = 0; d < numDaysToSimulate; d++) {
            startDay(people, d);
            for (int h = 0; h < 24; h++) {
                updateHour(people, h, d);
            }
            endDay(people, d);
        }
    }

    public static void shamilAgentGeneration(ArrayList<Person> people) {
        //SHAMIL'S AGET GENERATION
        ShamilPersonManager.generatePersons(people);
        ShamilPersonManager.assignProfessionGroup(people);
    }

    public static void shamilInitialInfection(ArrayList<Person> people) {
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
            if (people.get(i).shamilPersonProperties.profession == null) {
                System.out.println("!!!!!!!!!!!");
            }
        }

        ShamilDaySimulator.dayStart(people, day);
        boolean lockdown_started = false;
        if (day >= ShamilPersonManager.preference_def.get("quarantine_start")) {
            lockdown_started = true;
        }
        ShamilDaySimulator.generateDailyTasks(people, lockdown_started);

        daily_groups = new ArrayList();
    }

    public static void updateHour(ArrayList<Person> people, int hour, int day) {
        for (int i = 0; i < people.size(); i++) {
            ShamilPersonManager.updateCurrentTask(people.get(i), hour);
        }
        ShamilHourSimulator.generateHourlyActions(people, hour);

        Object output[] = ShamilGroupManager.assignGroups(people, tracing_percentage, day);

        ArrayList<ShamilGroup> groups = (ArrayList<ShamilGroup>) output[0];
        HashMap<Integer, ArrayList<Integer>> person_group = (HashMap<Integer, ArrayList<Integer>>) output[1];

        int event_cnt = 0;
        int event_going_person_cnt = 0;
        for (int i = 0; i < groups.size(); i++) {// grp in groups:
            if ((groups.get(i).group_name).startsWith("E")) {
                event_cnt += 1;
                event_going_person_cnt += groups.get(i).persons.size();
            }
        }

        daily_groups.add(person_group);

        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).updateActions();
        }

        for (int i = 0; i < groups.size(); i++) {
            ShamilGroupSimulator.groupInteraction(groups.get(i));
        }

        //pickle.dump(daily_groups,open('group_info_day_' + str(day) + '.p','wb'))
    }

    public static void endDay(ArrayList<Person> people, int day) {
        ShamilDaySimulator.dayEnd(people, day, trace_days, quarantine_days, daily_groups);
    }
}
