/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

/**
 *
 * @author user
 */
public class ShamilTask implements Comparable<ShamilTask> {

    public String name;
    public double start_time;
    public double end_time;

    public ShamilTask(String passed_name, double passed_start_time, double passed_end_time) {
        name = passed_name;
        start_time = passed_start_time;
        end_time = passed_end_time;
    }

    @Override
    public int compareTo(ShamilTask arg0) {
        if (start_time == arg0.start_time) {
            return 0;
        } else if (start_time > arg0.start_time) {
            return 1;
        } else {
            return -1;
        }
    }
}
