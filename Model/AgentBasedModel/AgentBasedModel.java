/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.Model.MainModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author user
 */
@Getter
@Setter
public class AgentBasedModel {

    public transient String filePath;
    public ArrayList<AgentTemplate> agentTemplates = new ArrayList();
    public transient Agent rootAgent;
    public transient ArrayList<Agent> agents;

    public String startTimeString;
    public String endTimeString;

    private transient MainModel myMainModel;

    public transient ZonedDateTime startTime;
    public transient ZonedDateTime currentTime;
    public transient ZonedDateTime endTime;

    public boolean isPatternBasedTime = true;

    public AgentBasedModel(MainModel mainModel) {
        myMainModel = mainModel;
    }

    public void initAgents() {

    }

    public void evaluateAllAgents() {
        if (rootAgent.myTemplate.behavior.isJavaScriptActive == true) {
            myMainModel.javaEvaluationEngine.runScript(rootAgent.myTemplate.behavior.javaScript.script);
        } else {
            myMainModel.pythonEvaluationEngine.runScript(rootAgent.myTemplate.behavior.pythonScript.script);
        }
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).myTemplate.behavior.isJavaScriptActive == true) {
                myMainModel.javaEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.javaScript.script);
            } else {
                myMainModel.pythonEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.pythonScript.script);
            }
        }
    }

    public Agent makeAgent(String agentTemplate) {
        AgentTemplate template = null;
        for (int i = 0; i < agentTemplates.size(); i++) {
            if (agentTemplates.get(i).agentTypeName.equals(agentTemplate)) {
                template = agentTemplates.get(i);
            }
        }
        if (template == null) {
            System.out.println("ERROR: MAKE AGENT: AGENT TEMPLATE NOT FOUND");
            return null;
        } else {
            Agent output = new Agent(template);
            output.myIndex = agents.size();
            agents.add(output);
            if (output.myTemplate.constructor.isJavaScriptActive == true) {
                myMainModel.javaEvaluationEngine.runScript(output.myTemplate.constructor.javaScript.script);
            } else {
                myMainModel.pythonEvaluationEngine.runScript(output.myTemplate.constructor.pythonScript.script);
            }
            return output;
        }
    }

    public void removeAgent(Agent agent) {
        if (agent.myTemplate.destructor.isJavaScriptActive == true) {
            myMainModel.javaEvaluationEngine.runScript(agent.myTemplate.destructor.javaScript.script);
        } else {
            myMainModel.pythonEvaluationEngine.runScript(agent.myTemplate.destructor.pythonScript.script);
        }
        agents.remove(agent.myIndex);
    }

    public void saveModel(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(this);
        BufferedWriter writer;
        try {
            FileWriter out = new FileWriter(path);
            writer = new BufferedWriter(out);
            writer.write(result);

            writer.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadModel(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileReader in;
        try {
            in = new FileReader(path);
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            in.close();

            AgentBasedModel result = gson.fromJson(sb.toString(), AgentBasedModel.class);
            agentTemplates = result.agentTemplates;
            startTimeString = result.startTimeString;
            endTimeString = result.endTimeString;
            if (startTimeString.length() > 0) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(startTimeString);
                startTime = zonedDateTime;
            }
            if (endTimeString.length() > 0) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(endTimeString);
                endTime = zonedDateTime;
            }
            isPatternBasedTime = result.isPatternBasedTime;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
