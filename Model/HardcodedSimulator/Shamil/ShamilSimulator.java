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
public class ShamilSimulator {
    
    public static void shamilAgentGeneration(ArrayList<Person> people){
        //SHAMIL'S AGET GENERATION
        ShamilPersonManager.generatePersons(people);
        ShamilPersonManager.assignProfessionGroup(people);
    }
    
    public void runIteration(int day){
        
    }
}
