/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.Model.MainModel;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class Agent {

    public int myIndex;

    public AgentTemplate myTemplate;
    
    public MainModel myModelRoot;

    /*
    * HARD CODED
    */
    public String myType;
    public ArrayList<String> statusNames;
    public ArrayList<Integer> statusValues;
    public float lat;
    public float lon;

    public Agent(AgentTemplate passed_agentTemplate) {
        myTemplate = passed_agentTemplate;
    }

    /*
    * HARD CODED
    */
    public Agent() {
        statusNames = new ArrayList();
        statusNames.add("DEFAULT_UNKNOWN");
        statusValues = new ArrayList();
        statusValues.add(-1);
    }

    /*
    * HARD CODED
    */
    public void constructor(MainModel modelRoot) {

    }

    /*
    * HARD CODED
    */
    public void behavior() {
        
    }
    
    /*
    * HARD CODED
    */
    public void setStatusNames(ArrayList<String> passed_statusNames){
        statusNames=passed_statusNames;
    }
    
    /*
    * HARD CODED
    */
    public void setStatusValues(ArrayList<Integer> passed_statusValues){
        statusValues=passed_statusValues;
    }

    public Object getPropertyValue(String propertyName) {
        for (int i = 0; i < myTemplate.agentProperties.size(); i++) {
            if (myTemplate.agentProperties.get(i).propertyName.equalsIgnoreCase(propertyName)) {
                return myTemplate.agentProperties.get(i).value;
            }
        }
        return null;
    }

    public void setPropertyValue(String propertyName, Object value) {
        for (int i = 0; i < myTemplate.agentProperties.size(); i++) {
            if (myTemplate.agentProperties.get(i).propertyName.equalsIgnoreCase(propertyName)) {
                myTemplate.agentProperties.get(i).value = value;
            }
        }
    }
}
