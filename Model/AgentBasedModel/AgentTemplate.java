/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.Model.Structure.Marker;
import groovy.lang.Binding;
import groovy.lang.Script;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class AgentTemplate {
    
    public AgentTemplate(){
        statusNames=new ArrayList();
        statusNames.add("DEFAULT_UNKNOWN");
        statusValues=new ArrayList();
        statusValues.add(-1);
    }
    
    public AgentTemplate(AgentTemplate copied){
        this.agentTypeName=copied.agentTypeName;
        ArrayList<AgentPropertyTemplate> temp=new ArrayList(copied.agentProperties);
        for(int i=0;i<temp.size();i++){
            AgentPropertyTemplate tempP = new AgentPropertyTemplate(temp.get(i));
            temp.set(i, tempP);
        }
        this.agentProperties=temp;
        
        this.constructor=copied.constructor;
        this.destructor=copied.destructor;
        this.behavior=copied.behavior;
        
//        Binding tempBinding=new Binding();
//        this.constructor.javaScript.parsedScript.setBinding(tempBinding);
//        tempBinding=new Binding();
//        this.destructor.javaScript.parsedScript.setBinding(tempBinding);
//        tempBinding=new Binding();
//        this.behavior.javaScript.parsedScript.setBinding(tempBinding);
        
        statusNames=new ArrayList();
        statusNames.add("DEFAULT_UNKNOWN");
        statusValues=new ArrayList();
        statusValues.add(-1);
    }
    
    public String agentTypeName;
    
    public ArrayList<String> statusNames;
    public ArrayList<Integer> statusValues;
    
    public ArrayList<AgentPropertyTemplate> agentProperties=new ArrayList();
    
    public BehaviorScript constructor;
    public BehaviorScript destructor;
    public BehaviorScript behavior;
    
    public boolean canRunBehaviorParallel=false;
    
}
