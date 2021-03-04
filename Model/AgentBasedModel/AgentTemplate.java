/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.Model.Structure.Marker;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author user
 */
@Getter @Setter
public class AgentTemplate {
    
    public String agentTypeName;
    
    public ArrayList<AgentPropertyTemplate> agentProperties=new ArrayList();
    
    public BehaviorScript constructor;
    public BehaviorScript destructor;
    public BehaviorScript behavior;
}
