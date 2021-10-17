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
public class ShamilTemplateAction {

    public String task;
    public String action;
    public int min_time_gap;
    public int max_time_gap;
    public double min_prob_affect;
    public double max_prob_affect;
    public double min_prob;
    public double max_prob;
    public double min_effect_others;
    public double max_effect_others;
    public double min_effect_self;
    public double max_effect_self;

    public ShamilTemplateAction(String passed_task, String passed_action, int passed_min_time_gap, int passed_max_time_gap, double passed_min_prob_affect, double passed_max_prob_affect, double passed_min_prob, double passed_max_prob, double passed_min_effect_others, double passed_max_effect_others, double passed_min_effect_self, double passed_max_effect_self) {
        task = passed_task;
        action = passed_action;
        min_time_gap = passed_min_time_gap;
        max_time_gap = passed_max_time_gap;
        min_prob_affect = passed_min_prob_affect;
        max_prob_affect = passed_max_prob_affect;
        min_prob = passed_min_prob;
        max_prob = passed_max_prob;
        min_effect_others = passed_min_effect_others;
        max_effect_others = passed_max_effect_others;
        min_effect_self = passed_min_effect_self;
        max_effect_self = passed_max_effect_self;
    }
}
