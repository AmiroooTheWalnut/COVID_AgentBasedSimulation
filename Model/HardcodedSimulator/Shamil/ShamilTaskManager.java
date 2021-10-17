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
public class ShamilTaskManager {
    
    public static ArrayList<ShamilTask> generateTasks(ShamilProfession profession) {
        
        ArrayList<ShamilTask> taskList = new ArrayList();
        
        for (int index = 0; index < profession.tasks_df.size(); index++) {
            double prob = randn(profession.tasks_df.get(index).min_prob, profession.tasks_df.get(index).max_prob);
            
            if (prob < 0.5) {
                
                continue;
            }
            double start_time = Math.round(randn(profession.tasks_df.get(index).min_start_time,profession.tasks_df.get(index).max_start_time));
            double duration = Math.round(randn(profession.tasks_df.get(index).min_duration,profession.tasks_df.get(index).max_duration));
            
            taskList.add(new ShamilTask(profession.tasks_df.get(index).task, start_time, start_time + duration));

//            start_time = round(randn(row["min_start_time"],row["max_start_time"]))
//            duration = round(randn(row["min_duration"],row["max_duration"]))
//            
//            taskList.append(Task(row["task"], start_time, start_time+duration))
        }
        taskList = refineTaskDuration(taskList);
        
        return taskList;
    }

    // for now a simplified, very rudimentary interval covering has been done
    public static ArrayList<ShamilTask> refineTaskDuration(ArrayList<ShamilTask> tasks) {

        // fixing same start time
        Collections.sort(tasks);
//        tasks.sort()

        double last_start_time = -1;
        
        for (int i = 0; i < tasks.size(); i++) {
            
            if (last_start_time == tasks.get(i).start_time) {
                
                tasks.get(i).start_time += 1;
                tasks.get(i).end_time = Math.min(tasks.get(i).end_time + 1, 23);
            }
            
            last_start_time = tasks.get(i).start_time;
        }

        // solving overlaps
        ArrayList<ShamilTask> newTasks = new ArrayList();
        
        int i = 0;
        
        while (i < tasks.size()) {
            if (tasks.get(i).start_time >= tasks.get(i).end_time) {
                i += 1;
                continue;
            } else if ((i < (tasks.size() - 1)) && (tasks.get(i).end_time > tasks.get(i + 1).start_time)) {
                newTasks.add(new ShamilTask(tasks.get(i).name, tasks.get(i).start_time, tasks.get(i + 1).start_time));

//                newTasks.append(Task(tasks[i].name,tasks[i].start_time,tasks[i+1].start_time))
                tasks.set(i, new ShamilTask(tasks.get(i).name, tasks.get(i + 1).end_time, tasks.get(i).end_time));

//                tasks[i] = Task(tasks[i].name,tasks[i+1].end_time,tasks[i].end_time)
                ShamilTask task1 = tasks.get(i);
                ShamilTask task2 = tasks.get(i + 1);
                tasks.set(i, task2);
                tasks.set(i + 1, task1);
//                (tasks[i],tasks[i+1]) = (tasks[i+1],tasks[i])
                
                continue;
            } else {
                newTasks.add(new ShamilTask(tasks.get(i).name, tasks.get(i).start_time, tasks.get(i).end_time));

//                newTasks.append(Task(tasks[i].name,tasks[i].start_time,tasks[i].end_time));
                i += 1;
                continue;
            }
        }

        // filling up gaps
        newTasks.get(newTasks.size()-1).end_time=24;//ADDED BY AMIROOO
        for (int j = 1; j < newTasks.size(); j++) {// i in range(1,len(tasks)):
            newTasks.get(j - 1).end_time = newTasks.get(j).start_time;//EDITED BY AMIROOO
//            tasks[i-1].end_time = tasks[i].start_time;
        }
        
        return newTasks;
    }
}
