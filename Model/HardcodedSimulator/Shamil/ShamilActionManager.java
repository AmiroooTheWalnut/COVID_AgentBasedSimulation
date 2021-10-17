/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.randn;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.rnd;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author user
 */
public class ShamilActionManager {

    public static ArrayList<ShamilAction> generateActions(int prerson_id, ArrayList<ShamilTemplateAction> actions_def) {

        ArrayList<ShamilAction> actionsList = new ArrayList();

        double ACTION_OCCURRING_PROBABILITY = ShamilPersonManager.thresholds_df.get("ACTION_OCCURRING_PROBABILITY");

        for (int i = 0; i < actions_def.size(); i++) {

            int cur_time = 0;

            double nxt_tme = randn(actions_def.get(i).min_time_gap, actions_def.get(i).max_time_gap);

            cur_time += nxt_tme;

//            nxt_tme = int(randn(row["min_time_gap"],row["max_time_gap"]))
//            cur_time += nxt_tme
            while (cur_time < 60) {

                double prob = randn(actions_def.get(i).min_prob , actions_def.get(i).max_prob);

//                prob = randn(row["min_prob"],row["max_prob"])
                if (prob < ACTION_OCCURRING_PROBABILITY) {
                    nxt_tme = randn(actions_def.get(i).min_time_gap , actions_def.get(i).max_time_gap);
//                    nxt_tme = int(randn(row["min_time_gap"],row["max_time_gap"]))
                    cur_time += nxt_tme;

                    continue;
                }
                double min_prob_affect = actions_def.get(i).min_prob_affect;
                double max_prob_affect = actions_def.get(i).max_prob_affect;
                double min_effect_others = actions_def.get(i).min_effect_others;
                double max_effect_others = actions_def.get(i).max_effect_others;
                double min_effect_self = actions_def.get(i).min_effect_self;
                double max_effect_self = actions_def.get(i).max_effect_self;
                actionsList.add(new ShamilAction(prerson_id, actions_def.get(i).action, cur_time, min_prob_affect, max_prob_affect, min_effect_others, max_effect_others, min_effect_self, max_effect_self));
//                actionsList.append(Action(prerson_id, row["action"],cur_time,row["min_prob_affect"],row["max_prob_affect"],row["min_effect_others"], row["max_effect_others"], row["min_effect_self"], row["max_effect_self"]))

                nxt_tme = randn(actions_def.get(i).min_time_gap , actions_def.get(i).max_time_gap);
//                nxt_tme = int(randn(row["min_time_gap"],row["max_time_gap"]))
                cur_time += nxt_tme;
            }
        }

        return actionsList;
    }
    
    public static ArrayList<ShamilAction> refineActionList(ArrayList<ShamilAction> actions){

        Collections.sort(actions);
//        actions.sort();

        return actions;
    }

}
