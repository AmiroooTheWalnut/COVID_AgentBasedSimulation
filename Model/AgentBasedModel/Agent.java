/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class Agent {
    public int myIndex;
    
    public AgentTemplate myTemplate;
    
    public Agent(AgentTemplate passed_agentTemplate){
        myTemplate=passed_agentTemplate;
    }
    
    public Object getPropertyValue(String propertyName){
        for(int i=0;i<myTemplate.agentProperties.size();i++){
            if(myTemplate.agentProperties.get(i).propertyName.equalsIgnoreCase(propertyName)){
                return myTemplate.agentProperties.get(i).value;
            }
        }
        return null;
    }
    
    public void setPropertyValue(String propertyName, Object value){
        for(int i=0;i<myTemplate.agentProperties.size();i++){
            if(myTemplate.agentProperties.get(i).propertyName.equalsIgnoreCase(propertyName)){
                myTemplate.agentProperties.get(i).value=value;
            }
        }
    }
}
