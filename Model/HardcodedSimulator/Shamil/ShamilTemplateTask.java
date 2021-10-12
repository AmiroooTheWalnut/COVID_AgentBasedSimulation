/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

/**
 *
 * @author user
 */
public class ShamilTemplateTask {

    public String task;
    public int min_start_time;
    public int max_start_time;
    public int min_duration;
    public int max_duration;
    public double min_prob;
    public double max_prob;

    public ShamilTemplateTask(String passed_task, int passed_min_start_time, int passed_max_start_time, int passed_min_duration, int passed_max_duration, double passed_min_prob, double passed_max_prob) {
        task = passed_task;
        min_start_time = passed_min_start_time;
        max_start_time = passed_max_start_time;
        min_duration = passed_min_duration;
        max_duration = passed_max_duration;
        min_prob = passed_min_prob;
        max_prob = passed_max_prob;
    }
}
