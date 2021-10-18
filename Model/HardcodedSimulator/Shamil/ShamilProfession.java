/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class ShamilProfession {

    public String name;
    public int min_age;
    public int max_age;
    public double percentage;

    public ArrayList<ShamilTemplateTask> tasks_df = new ArrayList();

    public ShamilProfession(ShamilProfession copied) {
        this.name = copied.name;
        this.min_age = copied.min_age;
        this.max_age = copied.max_age;
        this.percentage = copied.percentage;
    }

    public ShamilProfession(String passed_name, int passed_min_age, int passed_max_age, double passed_percentage) {
        name = passed_name;
        min_age = passed_min_age;
        max_age = passed_max_age;
        percentage = passed_percentage;
        switch (name) {
            case "Service":
                ShamilTemplateTask task = new ShamilTemplateTask("Stay Home", 0, 0, 7, 8, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Go to Work", 7, 8, 1, 2, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Work", 8, 10, 8, 9, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Returns Home", 16, 19, 1, 2, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Stay Home", 17, 21, 10, 10, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Attend Event", 12, 14, 2, 4, 0.3, 0.9);
                tasks_df.add(task);
                break;
            case "Driver":
                task = new ShamilTemplateTask("Stay Home", 0, 0, 6, 7, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Go to Work", 6, 7, 1, 1, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Treat Patients", 7, 8, 8, 12, 0.4, 1);//COULD BE A MISTAKE IN NAMING IN THE BASE PAPER
                tasks_df.add(task);
                task = new ShamilTemplateTask("Returns Home", 15, 20, 1, 1, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Stay Home", 16, 21, 10, 10, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Attend Event", 12, 14, 2, 4, 0.3, 0.6);
                tasks_df.add(task);
                break;
            case "Doctor":
                task = new ShamilTemplateTask("Stay Home", 0, 0, 7, 8, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Go to Work", 7, 8, 1, 2, 0.8, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Treat Patients", 8, 9, 8, 10, 0.8, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Returns Home", 16, 19, 1, 2, 0.8, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Stay Home", 17, 21, 10, 10, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Attend Event", 12, 14, 2, 4, 0.3, 0.7);
                tasks_df.add(task);
                break;
            case "Student":
                task = new ShamilTemplateTask("Stay Home", 0, 0, 7, 8, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Go to Work", 7, 8, 1, 1, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Work", 8, 9, 6, 7, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Returns Home", 14, 16, 1, 1, 0.4, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Stay Home", 15, 27, 10, 10, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Attend Event", 12, 14, 2, 4, 0.4, 0.9);
                tasks_df.add(task);
                break;
            case "Unemployed":
                task = new ShamilTemplateTask("Stay Home", 0, 0, 12, 13, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Go to Work", 12, 13, 1, 1, 0.3, 0.55);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Returns Home", 15, 18, 1, 1, 0.3, 0.55);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Stay Home", 16, 17, 10, 10, 1, 1);
                tasks_df.add(task);
                task = new ShamilTemplateTask("Attend Event", 13, 14, 2, 4, 0.35, 0.6);
                tasks_df.add(task);
                break;
            case "Hospitalized":
                task = new ShamilTemplateTask("Stay Hospital", 0, 0, 24, 24, 1, 1);
                tasks_df.add(task);
                break;
            case "NoOutingAllowed":
                task = new ShamilTemplateTask("Stay Home", 0, 0, 24, 24, 1, 1);
                tasks_df.add(task);
                break;
        }
    }

}
