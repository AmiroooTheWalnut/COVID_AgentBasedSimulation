/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class ShamilPersonProperties {
    public int id;
    public String name;
    public int age;
    public ShamilProfession profession;
    public int familyId;
    public int professionGroupId;
    public ArrayList<ShamilTask> tasks=new ArrayList();
    public ArrayList<ShamilAction> actions=new ArrayList();
    
    public ShamilTask currentTask;
    public boolean isAlive=true;
    public boolean isInfected=false;
    
    public double infectionLevel;
    public Object infectedBy;
    public int infectedByUpdt=0;
    public int infectedDays=0;
    public double awarenessLevel=0;
    public double protectionLevel=0;
    public String state="Not_infected";
    public ShamilProfession initialProfession;
    public int quarantinedDay=-1;
    public boolean isTraceable;
    
    //EXTRA AXULARY FEATURE
    public int familySize;
}
