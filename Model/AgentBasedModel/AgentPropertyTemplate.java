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
public class AgentPropertyTemplate {
    
    public AgentPropertyTemplate(){
        
    }
    
    public AgentPropertyTemplate(AgentPropertyTemplate copied){
        propertyName=copied.propertyName;
        propertyType=copied.propertyType;
        value=copied.value;
    }
    
    public String propertyName;
    public String propertyType;
    public transient Object value;
}
