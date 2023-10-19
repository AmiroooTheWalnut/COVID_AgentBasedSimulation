/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.AdvancedParallelAgentEvaluator;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.quarantine_days;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.trace_days;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.tracing_percentage;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root.statusEnum;
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
public class ShamilSimulatorController {

    public static ArrayList<HashMap<Integer, ArrayList<Integer>>> daily_groups;
    public static int n_infected_init = 200;

    public static void convertShamilToOurParallel(ArrayList<Person> people, MainModel myMainModel) {
        int numProcessors = myMainModel.numCPUs;
        try {
            AdvancedParallelShamilToOur parallelShamilToOur[] = new AdvancedParallelShamilToOur[numProcessors];

            for (int i = 0; i < numProcessors - 1; i++) {
                parallelShamilToOur[i] = new AdvancedParallelShamilToOur(myMainModel, people, (int) Math.floor(i * ((people.size()) / numProcessors)), (int) Math.floor((i + 1) * ((people.size()) / numProcessors)));
            }
            parallelShamilToOur[numProcessors - 1] = new AdvancedParallelShamilToOur(myMainModel, people, (int) Math.floor((numProcessors - 1) * ((people.size()) / numProcessors)), people.size());

            ArrayList<Callable<Object>> calls = new ArrayList<>();

            for (int i = 0; i < numProcessors; i++) {
                parallelShamilToOur[i].addRunnableToQueue(calls);
            }

            //myMainModel.agentEvalPool.invokeAny(calls);
            myMainModel.agentEvalPool.invokeAll(calls);
        } catch (InterruptedException ex) {
            Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void convertOurToShamilParallel(ArrayList<Person> people, MainModel myMainModel) {
        int numProcessors = myMainModel.numCPUs;
        try {
            AdvancedParallelOurToShamil parallelOurToShamil[] = new AdvancedParallelOurToShamil[numProcessors];

            for (int i = 0; i < numProcessors - 1; i++) {
                parallelOurToShamil[i] = new AdvancedParallelOurToShamil(myMainModel, people, (int) Math.floor(i * ((people.size()) / numProcessors)), (int) Math.floor((i + 1) * ((people.size()) / numProcessors)));
            }
            parallelOurToShamil[numProcessors - 1] = new AdvancedParallelOurToShamil(myMainModel, people, (int) Math.floor((numProcessors - 1) * ((people.size()) / numProcessors)), people.size());

            ArrayList<Callable<Object>> calls = new ArrayList<>();

            for (int i = 0; i < numProcessors; i++) {
                parallelOurToShamil[i].addRunnableToQueue(calls);
            }

            //myMainModel.agentEvalPool.invokeAny(calls);
            myMainModel.agentEvalPool.invokeAll(calls);
        } catch (InterruptedException ex) {
            Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void convertShamilToOur(ArrayList<Person> people) {
//        int inf=0;
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).shamilPersonProperties.currentTask.name.equals("Stay Home")) {
                if (people.get(i).properties.isInTravel == false) {
                    people.get(i).properties.isAtHome = true;
                    people.get(i).properties.isAtWork = false;
                } else {
                    people.get(i).properties.didTravelFromHome = true;
                    people.get(i).properties.didTravelFromWork = false;
                }
            }
            if (people.get(i).shamilPersonProperties.currentTask.name.equals("Go to Work") || people.get(i).shamilPersonProperties.currentTask.name.equals("Work") || people.get(i).shamilPersonProperties.currentTask.name.equals("Returns Home") || people.get(i).shamilPersonProperties.currentTask.name.equals("Treat Patients")) {
                if (people.get(i).properties.isInTravel == false) {
                    people.get(i).properties.isAtHome = false;
                    people.get(i).properties.isAtWork = true;
                } else {
                    people.get(i).properties.didTravelFromHome = false;
                    people.get(i).properties.didTravelFromWork = true;
                }
            }
            for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                switch (people.get(i).insidePeople.get(m).sfpp.state) {
                    case "Not_infected":
                        people.get(i).insidePeople.get(m).fpp.status = statusEnum.SUSCEPTIBLE.ordinal();
                        break;
                    case "Infected_notContagious":
                        people.get(i).insidePeople.get(m).fpp.status = statusEnum.SUSCEPTIBLE.ordinal();
                        break;
                    case "contagious_symptomatic":
                        people.get(i).insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
//                inf+=1;
                        break;
                    case "contagious_asymptomatic":
                        people.get(i).insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
//                inf+=1;
                        break;
                    case "Dead":
                        people.get(i).insidePeople.get(m).fpp.status = statusEnum.DEAD.ordinal();
                        break;
                    case "recovered":
                        people.get(i).insidePeople.get(m).fpp.status = statusEnum.RECOVERED.ordinal();
                        break;
                    default:
                        break;
                }
            }
        }
//        System.out.println("convertOurToShamil INF: "+inf);
    }

    public static void convertShamilToOurPerson(Person person) {
        if (person.shamilPersonProperties.currentTask.name.equals("Stay Home")) {
            if (person.properties.isInTravel == false) {
                person.properties.isAtHome = true;
                person.properties.isAtWork = false;
            } else {
                person.properties.didTravelFromHome = true;
                person.properties.didTravelFromWork = false;
            }
        }
        if (person.shamilPersonProperties.currentTask.name.equals("Go to Work") || person.shamilPersonProperties.currentTask.name.equals("Work") || person.shamilPersonProperties.currentTask.name.equals("Returns Home") || person.shamilPersonProperties.currentTask.name.equals("Treat Patients")) {
            if (person.properties.isInTravel == false) {
                person.properties.isAtHome = false;
                person.properties.isAtWork = true;
            } else {
                person.properties.didTravelFromHome = false;
                person.properties.didTravelFromWork = true;
            }
        }
        for (int m = 0; m < person.insidePeople.size(); m++) {
            switch (person.insidePeople.get(m).sfpp.state) {
                case "Not_infected":
                    person.insidePeople.get(m).fpp.status = statusEnum.SUSCEPTIBLE.ordinal();
                    break;
                case "Infected_notContagious":
                    person.insidePeople.get(m).fpp.status = statusEnum.SUSCEPTIBLE.ordinal();
                    break;
                case "contagious_symptomatic":
                    person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
//                inf+=1;
                    break;
                case "contagious_asymptomatic":
                    person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
//                inf+=1;
                    break;
                case "Dead":
                    person.insidePeople.get(m).fpp.status = statusEnum.DEAD.ordinal();
                    break;
                case "recovered":
                    person.insidePeople.get(m).fpp.status = statusEnum.RECOVERED.ordinal();
                    break;
                default:
                    break;
            }
        }
    }

    public static void convertOurToShamil(ArrayList<Person> people) {
//        int inf=0;
        for (int i = 0; i < people.size(); i++) {
            for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                switch (people.get(i).insidePeople.get(m).fpp.status) {
                    case 0:
                        people.get(i).insidePeople.get(m).sfpp.state = "Not_infected";
//                        people.get(i).insidePeople.get(m).sfpp.isInfected=false;
                        break;
                    case 1:
                        people.get(i).insidePeople.get(m).sfpp.state = "contagious_symptomatic";
                        people.get(i).insidePeople.get(m).sfpp.isInfected = true;
//                inf+=1;
                        break;
                    case 2:
                        people.get(i).insidePeople.get(m).sfpp.state = "contagious_asymptomatic";
                        people.get(i).insidePeople.get(m).sfpp.isInfected = true;
//                inf+=1;
                        break;
                    case 3:
                        people.get(i).insidePeople.get(m).sfpp.state = "recovered";
//                        people.get(i).insidePeople.get(m).sfpp.isInfected=false;
                        break;
                    case 4:
                        people.get(i).insidePeople.get(m).sfpp.state = "Dead";
//                        people.get(i).insidePeople.get(m).sfpp.isInfected=false;
                        break;
                    default:
                        break;
                }
            }
        }
//        System.out.println("convertOurToShamil INF: "+inf);
    }

    public static void convertOurToShamilPerson(Person person) {
//        int inf=0;
        for (int m = 0; m < person.insidePeople.size(); m++) {
            switch (person.insidePeople.get(m).fpp.status) {
                case 0:
                    person.insidePeople.get(m).sfpp.state = "Not_infected";
//                    person.insidePeople.get(m).sfpp.isInfected=false;
                    break;
                case 1:
                    person.insidePeople.get(m).sfpp.state = "contagious_symptomatic";
                    person.insidePeople.get(m).sfpp.isInfected = true;
//                inf+=1;
                    break;
                case 2:
                    person.insidePeople.get(m).sfpp.state = "contagious_asymptomatic";
                    person.insidePeople.get(m).sfpp.isInfected = true;
//                inf+=1;
                    break;
                case 3:
                    person.insidePeople.get(m).sfpp.state = "recovered";
//                    person.insidePeople.get(m).sfpp.isInfected=false;
                    break;
                case 4:
                    person.insidePeople.get(m).sfpp.state = "Dead";
//                    person.insidePeople.get(m).sfpp.isInfected=false;
                    break;
                default:
                    break;
            }
        }
//        System.out.println("convertOurToShamil INF: "+inf);
    }

    public static void runRawShamilSimulation(ArrayList<Person> people, int numDaysToSimulate, MainModel mainModel) {
        shamilAgentGeneration(mainModel, people);
        for (int d = 0; d < numDaysToSimulate; d++) {
            startDay(mainModel, people, d);
            for (int h = 0; h < 24; h++) {
                updateHour(people, null, h, d, false, false, mainModel);
            }
            endDay(mainModel, people, d, false, 0);
        }
    }

    public static void runShamilSimulationOnly(ArrayList<Person> people, int numDaysToSimulate, MainModel mainModel) {
        for (int d = 0; d < numDaysToSimulate; d++) {
            startDay(mainModel, people, d);
            for (int h = 0; h < 24; h++) {
                updateHour(people, null, h, d, false, false, mainModel);
            }
            endDay(mainModel, people, d, false, 0);
        }
    }

    public static void shamilAgentGeneration(MainModel myMainModel, ArrayList<Person> people) {
        //SHAMIL'S AGET GENERATION
        ShamilPersonManager.generatePersons(myMainModel, people);
        ShamilPersonManager.assignProfessionGroup(people);
    }

    /*
    *   Added by Amirooo
     */
    public static void shamilAgentGenerationSpatial(MainModel myMainModel, ArrayList<Region> regions, ArrayList<Person> people) {
        //SHAMIL'S AGET GENERATION
        ShamilPersonManager.generatePersonsSpatial(myMainModel, regions);
        ShamilPersonManager.assignProfessionGroupSpatial(myMainModel, regions, people);
    }

    public static void shamilInitialInfection(MainModel myMainModel, ArrayList<Person> people) {
        ShamilPersonManager.initialInfection(myMainModel, people, n_infected_init);
    }

    public static void startDay(MainModel mainModel, ArrayList<Person> people, int day) {
        int counter = 0;//TEMP
        for (int i = 0; i < people.size(); i++) {
            double rnd = mainModel.ABM.root.rnd.nextDouble();
            if (rnd < ShamilPersonManager.smartphone_owner_percentage) {
                people.get(i).shamilPersonProperties.isTraceable = true;
            } else {
                people.get(i).shamilPersonProperties.isTraceable = false;
            }
            if (people.get(i).shamilPersonProperties.profession == null) {
                System.out.println("PROFESSION IS NULL");
            }
//            if (people.get(i).shamilPersonProperties.profession.name.equals("Hospitalized")) {
//                counter = counter + 1;
//            }
        }
//        System.out.println("num hospitalized " + counter);//TEMP
        ShamilDaySimulator.dayStart(mainModel, people, day);
        boolean lockdown_started = false;
        if (day >= ShamilPersonManager.preference_def.get("quarantine_start")) {
            lockdown_started = true;
        }
        ShamilDaySimulator.generateDailyTasks(mainModel, people, lockdown_started);

        daily_groups = new ArrayList();
    }

    public static void updateHour(ArrayList<Person> people, ArrayList<Region> regions, int hour, int day, boolean isSpatial, boolean debug, MainModel myMainModel) {
        for (int i = 0; i < people.size(); i++) {
//            boolean isAlive = false;
//            for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
//                if (people.get(i).insidePeople.get(m).sfpp.isAlive == true) {
//                    isAlive = true;
//                    break;
//                }
//            }
            if (people.get(i).isExistAlive == true) {
                ShamilPersonManager.updateCurrentTask(people.get(i), hour);
            }
//            if(people.get(i).shamilPersonProperties.currentTask==null){
//                System.out.println("NULL TASK!");
//            }
        }
        ShamilHourSimulator.generateHourlyActions(myMainModel, people, hour);
        Object output[];
        if (isSpatial == true) {
            output = ShamilGroupManager.assignGroupsSpatial(regions, tracing_percentage, day, debug, myMainModel);
        } else {
            output = ShamilGroupManager.assignGroups(myMainModel, people, tracing_percentage, day);
        }

        ArrayList<ShamilGroup> groups = (ArrayList<ShamilGroup>) output[0];
        HashMap<Integer, ArrayList<Integer>> person_group = (HashMap<Integer, ArrayList<Integer>>) output[1];

        //COMMENTED BECAUSE IT'S FOR REPORTING ONLY
//        int event_cnt = 0;
//        int event_going_person_cnt = 0;
//        for (int i = 0; i < groups.size(); i++) {// grp in groups:
//            if ((groups.get(i).group_name).startsWith("E")) {
//                event_cnt += 1;
//                event_going_person_cnt += groups.get(i).persons.size();
//            }
//        }
//        
//        if(event_going_person_cnt>400){
//            System.out.println("Hour: "+hour+" - Events: "+event_cnt+" - Event going people: "+event_going_person_cnt);
//        }
        //COMMENTED BECAUSE IT'S FOR REPORTING ONLY
        daily_groups.add(person_group);

        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).updateActions();
//            System.out.println(groups.get(i).persons.size());
        }

        try {
            int numProcessors = myMainModel.numCPUs;

            AdvancedParallelGroupInteractionEvaluator parallelGroupEval[] = new AdvancedParallelGroupInteractionEvaluator[numProcessors];

            for (int i = 0; i < numProcessors - 1; i++) {
                parallelGroupEval[i] = new AdvancedParallelGroupInteractionEvaluator(myMainModel, groups, (int) Math.floor(i * ((groups.size()) / numProcessors)), (int) Math.floor((i + 1) * ((groups.size()) / numProcessors)));
            }
            parallelGroupEval[numProcessors - 1] = new AdvancedParallelGroupInteractionEvaluator(myMainModel, groups, (int) Math.floor((numProcessors - 1) * ((groups.size()) / numProcessors)), groups.size());

            ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();

            for (int i = 0; i < numProcessors; i++) {
                parallelGroupEval[i].addRunnableToQueue(calls);
            }

//            myMainModel.agentEvalPool.invokeAny(calls);
            myMainModel.agentEvalPool.invokeAll(calls);
        } catch (InterruptedException ex) {
            Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        catch (ExecutionException ex) {
//            Logger.getLogger(ShamilSimulatorController.class.getName()).log(Level.SEVERE, null, ex);
//        }

        //SERIAL EVAULATION OF GROUP INTERACTIONS
//        for (int i = 0; i < groups.size(); i++) {
//            ShamilGroupSimulator.groupInteraction(groups.get(i));
//            
//            if (debug == true) {
//                if (groups.get(i).persons.size() > 50 && groups.get(i).group_name.contains("T")) {
//                    System.out.println("GROUP NAME: " + groups.get(i).group_name);
//                    System.out.println("GROUP SIZE: " + groups.get(i).persons.size());
//
//                    System.out.println("@@@@@@@@@@@");
//                }
//            }
//        }
        //SERIAL EVAULATION OF GROUP INTERACTIONS
        //pickle.dump(daily_groups,open('group_info_day_' + str(day) + '.p','wb'))
    }

    public static void endDay(MainModel mainModel, ArrayList<Person> people, int day, boolean isFuzzyStatus, double pTSFraction) {
//        int counter1 = 0;
//        for (int i = 0; i < people.size(); i++) {
//            if (people.get(i).shamilPersonProperties.profession.name.equals("Hospitalized")) {
//                counter1 = counter1 + 1;
//            }
//        }
//        System.out.println("num hospitalized before end " + counter1);//TEMP
        ShamilDaySimulator.dayEnd(mainModel, people, day, trace_days, quarantine_days, daily_groups, isFuzzyStatus, pTSFraction);
//        int counter2 = 0;
//        for (int i = 0; i < people.size(); i++) {
//            if (people.get(i).shamilPersonProperties.profession.name.equals("Hospitalized")) {
//                counter2 = counter2 + 1;
//            }
//        }
//        System.out.println("num hospitalized after end " + counter2);//TEMP
    }
}
