/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

/**
 *
 * @author user
 */
public class ShamilAction implements Comparable<ShamilAction>{

    public int person_id;
    public String name;
    public int timestamp;
    public double min_prob_affect;
    public double max_prob_affect;
    public double min_effect_others;
    public double max_effect_others;
    public double min_effect_self;
    public double max_effect_self;

    public ShamilAction(int passed_person_id, String passed_name, int passed_timestamp, double passed_min_prob_affect, double passed_max_prob_affect, double passed_min_effect_others, double passed_max_effect_others, double passed_min_effect_self, double passed_max_effect_self) {
        person_id = passed_person_id;
        name = passed_name;
        timestamp = passed_timestamp;
        min_prob_affect = passed_min_prob_affect;
        max_prob_affect = passed_max_prob_affect;
        min_effect_others = passed_min_effect_others;
        max_effect_others = passed_max_effect_others;
        min_effect_self = passed_min_effect_self;
        max_effect_self = passed_max_effect_self;

    }

    @Override
    public int compareTo(ShamilAction arg0) {
        if (timestamp == arg0.timestamp) {
            return 0;
        } else if (timestamp > arg0.timestamp) {
            return 1;
        } else {
            return -1;
        }
    }
}
