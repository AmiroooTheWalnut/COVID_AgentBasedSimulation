/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class JavaScript {
    public transient Binding myBinding;
    public transient GroovyShell myShell;
    public transient Script parsedScript;
    public String script;
}
