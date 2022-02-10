/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class ShamilHourSimulator {

    public static void generateHourlyActions(ArrayList<Person> people, int hour) {

        for (int i = 0; i < people.size(); i++) {

            ShamilTask current_task = people.get(i).shamilPersonProperties.currentTask;
//            if(current_task!=null){
                people.get(i).shamilPersonProperties.actions = ShamilActionManager.generateActions(people.get(i).shamilPersonProperties.id, ShamilPersonManager.actions_def.get(people.get(i).shamilPersonProperties.currentTask.name));
//            }
//            prsn.setActions(ActionManager.generateActions(prsn.id, actions_df[actions_df["task"]==current_task.name], thresholds_df))
//            if(print_opt):
//                print('=== {} === {} === {} ==='.format(hour, prsn.profession, current_task))
//                ActionManager.printActions(prsn.actions)
        }
    }
}
