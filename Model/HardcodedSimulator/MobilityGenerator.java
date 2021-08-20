/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Country;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Structure.State;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class MobilityGenerator {
    
    ArrayList<PersonGenerated> peopleGenerated;
    
    public void generateMobility(MainModel mainModel){
        int numPopulation=1000;
        if (mainModel.ABM.studyScopeGeography instanceof Country) {
            
        }else if (mainModel.ABM.studyScopeGeography instanceof State) {
            
        }else if (mainModel.ABM.studyScopeGeography instanceof County) {
            
        }else if (mainModel.ABM.studyScopeGeography instanceof City) {
            City scope=((City)(mainModel.ABM.studyScopeGeography));
            int population=scope.population;
            for(int i=0;i<numPopulation;i++){
                int selectedPop=(int)(Math.floor(Math.random()*population));
                int sumPop=0;
                for(int j=0;j<scope.censusTracts.size();j++){
                    sumPop=sumPop+scope.censusTracts.get(j).population;
                    if(sumPop>selectedPop){
                        PersonGenerated person=new PersonGenerated();
                        peopleGenerated.add(person);
                    }
                }
            }
        }
    }
}
