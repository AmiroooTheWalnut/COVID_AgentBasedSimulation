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
 * @author user
 */
@Getter @Setter
public class Agent {
    int myIndex;
    
    public AgentTemplate myTemplate;
    
    public Agent(AgentTemplate passed_agentTemplate){
        myTemplate=passed_agentTemplate;
    }
}
