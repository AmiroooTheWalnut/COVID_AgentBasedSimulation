/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class ShamilGroup {

    public String group_name;
    public ArrayList<Person> persons;
    public HashMap<Integer, Integer> person_mapper;
    public ArrayList<ShamilAction> actions;
    public double[][] proximity = null;

    public ShamilGroup(String passed_group_name) {
        group_name = passed_group_name;
        persons = new ArrayList();
        person_mapper = new HashMap();
        actions = new ArrayList();
//        proximity = null;
    }

    public void updatePersonMapper() {
        person_mapper = new HashMap();
        for (int i = 0; i < persons.size(); i++) {// i in range(len(self.persons)){
            person_mapper.put(persons.get(i).shamilPersonProperties.id, i);//[self.persons[i].id] = i
        }
    }

    public void updateProximity() {
//        System.out.println("GROUP SIZE: "+persons.size());
//        System.out.println("GROUP NAME: "+group_name);
//        if(persons.size()>100){
//            System.out.println("@@@@@@@@@@@");
//        }
        boolean full_random_proximity = false;
        // # fline = open("seed.txt").readline().rstrip()
        // # ranseed = int(fline)
        // # np.random.seed(ranseed)
        // #max_proximity = dfToFloat(preferences_df,"max_proximity")
        // #min_proximity = dfToFloat(preferences_df,"min_proximity")

        double max_proximity = 1;
        double min_proximity = 0.5;

        //System.out.println(persons.size());
        if (full_random_proximity == true) {
            proximity = new double[persons.size()][persons.size()];
            for (int i = 0; i < persons.size(); i++) {
                for (int j = 0; j < persons.size(); j++) {
                    proximity[i][j] = ShamilPersonManager.rnd.nextGaussian();
                }
            }

//            proximity = np.random.randn(len(self.persons),len(self.persons))        
            for (int i = 0; i < persons.size(); i++) {
                for (int j = 0; j < persons.size(); j++) {
                    if (proximity[i][j] > max_proximity) {
                        proximity[i][j] = max_proximity;
                    }
                    if (proximity[i][j] < min_proximity) {
                        proximity[i][j] = min_proximity;
                    }
                }
            }
//            proximity = np.clip(self.proximity, min_proximity, max_proximity)
            return;
        }

        proximity = new double[persons.size()][persons.size()];
        /*
        for (int i = 0; i < persons.size(); i++) {
            for (int j = 0; j < persons.size(); j++) {
                proximity[i][j] = 0;
            }
        }
         */
//        self.proximity = np.zeros((len(self.persons), len(self.persons)))

        int tot_locs = (persons.size() * (persons.size() - 1)) / 2;
        int reduction_step = (int) Math.round(tot_locs * 0.1);
        int next_reduction = reduction_step;

        ArrayList<Integer> random_locs = new ArrayList();
        int counter = 0;

        ArrayList<int[]> locs = new ArrayList();

        for (int i = 0; i < persons.size(); i++) {
            for (int j = i + 1; j < persons.size(); j++) {
                int[] temp = new int[2];
                temp[0] = i;
                temp[1] = j;
                locs.add(temp);
                random_locs.add(counter);
            }
        }

//        for i in range(len(self.persons)):
//
//            for j in range(i+1,len(self.persons)):
//                
//                locs.append([i,j])
        // #max_proximity = dfToFloat(preferences_df,"max_proximity")
        // #min_proximity = dfToFloat(preferences_df,"min_proximity")
//        int tot_locs = locs.size();
//        int reduction_step = (int) Math.round(tot_locs * 0.1);
//        int next_reduction = reduction_step;
//        tot_locs = len(locs)
//        reduction_step = int(tot_locs*0.1)
//        next_reduction = reduction_step
//        ArrayList<Integer> random_locs = new ArrayList();
//        for (int i = 0; i < tot_locs; i++) {
//            random_locs.add(i);
//        }
        Collections.shuffle(random_locs);

//        random_locs = list(range(tot_locs))
//        np.random.shuffle(random_locs)
        double proximity_range = max_proximity - min_proximity;
        double reduction_amount = proximity_range * 0.1;
//        proximity_range = (max_proximity-min_proximity)
//        reduction_amount = proximity_range*0.1

        for (int i = 0; i < tot_locs; i++) {
            if (i == next_reduction) {
                // #max_proximity -= 0.1
                max_proximity -= reduction_amount;
                next_reduction += reduction_step;
            }

            double rand_proximity = Math.random() * reduction_amount;

            double var1 = Math.max(max_proximity - rand_proximity, 0);
            double var2 = Math.max(max_proximity - rand_proximity, 0);

            // #print("var1 - " + str(var1) + " var2 - " + str(var2))
            // #var1 = 1
            // #var2 = 1
            proximity[locs.get(random_locs.get(i))[0]][locs.get(random_locs.get(i))[1]] = var1;
            proximity[locs.get(random_locs.get(i))[1]][locs.get(random_locs.get(i))[0]] = var2;

//            self.proximity[locs[random_locs[i]][0]][locs[random_locs[i]][1]] = var1
//            self.proximity[locs[random_locs[i]][1]][locs[random_locs[i]][0]] = var2
        }
    }

    public void updateActions() {

        clearActions();

        for (int i = 0; i < persons.size(); i++) {// prsn in self.persons:

            addActions(persons.get(i).shamilPersonProperties.actions);
        }

        refineActions();
    }

    public void clearActions() {
        actions.clear();
    }

    public void addActions(ArrayList<ShamilAction> actns) {
        actions.addAll(actns);
    }

    public void refineActions() {
        actions = ShamilActionManager.refineActionList(actions);
    }

    public double getProximity(int p1, int p2) {

        return proximity[person_mapper.get(p1)][person_mapper.get(p2)];
    }

}
