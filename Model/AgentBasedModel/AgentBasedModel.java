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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class AgentBasedModel {

    public transient String filePath;
    public ArrayList<AgentTemplate> agentTemplates = new ArrayList();
    public transient Agent rootAgent;
    public transient CopyOnWriteArrayList<Agent> agents;

    public String startTimeString;
    public String endTimeString;

    public String studyScope;
    public transient Object studyScopeGeography;

    private transient MainModel myMainModel;

    public transient Agent currentEvaluatingAgent[] = new Agent[1];
    public transient Agent oldCurrentEvaluatingAgent[] = new Agent[1];

    public transient ZonedDateTime startTime;
    public transient ZonedDateTime currentTime;
    public transient ZonedDateTime endTime;

    public boolean isPatternBasedTime = true;

    public AgentBasedModel(MainModel mainModel) {
        myMainModel = mainModel;
    }

    public void evaluateAllAgents() {
        try {
            currentEvaluatingAgent[0] = rootAgent;
            if (rootAgent.myTemplate.behavior.isJavaScriptActive == true) {
                //myMainModel.javaEvaluationEngine.runScript(rootAgent.myTemplate.behavior.javaScript.script);

                myMainModel.javaEvaluationEngine.runParsedScript(rootAgent.myTemplate.behavior.javaScript.parsedScript);

            } else {
                myMainModel.pythonEvaluationEngine.runScript(rootAgent.myTemplate.behavior.pythonScript);
            }
            for (int i = 0; i < agents.size(); i++) {
                currentEvaluatingAgent[0] = agents.get(i);
                if (agents.get(i).myTemplate.behavior.isJavaScriptActive == true) {
                    //myMainModel.javaEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.javaScript.script);

                    myMainModel.javaEvaluationEngine.runParsedScript(agents.get(i).myTemplate.behavior.javaScript.parsedScript);
                } else {
                    myMainModel.pythonEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.pythonScript);
                }
            }
        } catch (Exception ex) {
            System.out.println("ERROR ON AGENT TYPE:");
            System.out.println(currentEvaluatingAgent[0].myTemplate.agentTypeName);
            System.out.println("ERROR ON AGENT INDEX:");
            System.out.println(currentEvaluatingAgent[0].myIndex);
            ex.printStackTrace();
        }
    }

    public Agent getCurrentAgent() {
        return currentEvaluatingAgent[0];
    }

    public Agent makeAgent(String agentTemplate) {
        try {
            AgentTemplate template = null;
            for (int i = 0; i < agentTemplates.size(); i++) {
                if (agentTemplates.get(i).agentTypeName.equals(agentTemplate)) {
                    template = agentTemplates.get(i);
                    break;
                }
            }
            if (template == null) {
                System.out.println("ERROR: MAKE AGENT: AGENT TEMPLATE NOT FOUND");
                return null;
            } else {
                AgentTemplate copiedTemplate = new AgentTemplate(template);
                copiedTemplate.constructor = template.constructor;
                copiedTemplate.behavior = template.behavior;
                copiedTemplate.destructor = template.destructor;
                Agent output = new Agent(copiedTemplate);
                output.myIndex = agents.size();
                agents.add(output);
                oldCurrentEvaluatingAgent[0] = currentEvaluatingAgent[0];
                currentEvaluatingAgent = new Agent[1];
                currentEvaluatingAgent[0] = output;
                if (output.myTemplate.constructor.isJavaScriptActive == true) {
                    //myMainModel.javaEvaluationEngine.runScript(output.myTemplate.constructor.javaScript.script);

                    myMainModel.javaEvaluationEngine.runParsedScript(output.myTemplate.constructor.javaScript.parsedScript);
                } else {
                    myMainModel.pythonEvaluationEngine.runScript(output.myTemplate.constructor.pythonScript);
                }
                currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];
                return output;
            }
        } catch (Exception ex) {

        }
        currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];
        return null;
    }

    public void removeAgent(Agent agent) {
        if (agent.myTemplate.destructor.isJavaScriptActive == true) {
            //myMainModel.javaEvaluationEngine.runScript(agent.myTemplate.destructor.javaScript.script);

            myMainModel.javaEvaluationEngine.runParsedScript(agent.myTemplate.destructor.javaScript.parsedScript);
        } else {
            myMainModel.pythonEvaluationEngine.runScript(agent.myTemplate.destructor.pythonScript);
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
            studyScope = result.studyScope;
            calculateStudyScopeGeography();
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

    public void calculateStudyScopeGeography() {
        if (myMainModel.allGISData != null) {
            if (myMainModel.allGISData.countries != null) {
                String sections[] = studyScope.split("_");
                int countryIndex = -1;
                int stateIndex = -1;
                int countyIndex = -1;
                if (sections.length == 1) {
                    for (int i = 0; i < myMainModel.allGISData.countries.size(); i++) {
                        if (sections[0].equals(myMainModel.allGISData.countries.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(i);
                            countryIndex = i;
                        }
                    }
                }
                if (sections.length == 2) {
                    for (int i = 0; i < myMainModel.allGISData.countries.size(); i++) {
                        if (sections[0].equals(myMainModel.allGISData.countries.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(i);
                            countryIndex = i;
                        }
                    }
                    for (int i = 0; i < myMainModel.allGISData.countries.get(countryIndex).states.size(); i++) {
                        if (sections[1].equals(myMainModel.allGISData.countries.get(countryIndex).states.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(countryIndex).states.get(i);
                            stateIndex = i;
                        }
                    }
                }
                if (sections.length == 3) {
                    for (int i = 0; i < myMainModel.allGISData.countries.size(); i++) {
                        if (sections[0].equals(myMainModel.allGISData.countries.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(i);
                            countryIndex = i;
                        }
                    }
                    for (int i = 0; i < myMainModel.allGISData.countries.get(countryIndex).states.size(); i++) {
                        if (sections[1].equals(myMainModel.allGISData.countries.get(countryIndex).states.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(countryIndex).states.get(i);
                            stateIndex = i;
                        }
                    }
                    for (int i = 0; i < myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.size(); i++) {
                        if (sections[2].equals(myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(i);
                            countyIndex = i;
                        }
                    }
                }
                if (sections.length == 4) {
                    for (int i = 0; i < myMainModel.allGISData.countries.size(); i++) {
                        if (sections[0].equals(myMainModel.allGISData.countries.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(i);
                            countryIndex = i;
                        }
                    }
                    for (int i = 0; i < myMainModel.allGISData.countries.get(countryIndex).states.size(); i++) {
                        if (sections[1].equals(myMainModel.allGISData.countries.get(countryIndex).states.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(countryIndex).states.get(i);
                            stateIndex = i;
                        }
                    }
                    for (int i = 0; i < myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.size(); i++) {
                        if (sections[2].equals(myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(i);
                            countyIndex = i;
                        }
                    }
                    for (int i = 0; i < myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(countyIndex).cities.size(); i++) {
                        if (sections[3].equals(myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(countyIndex).cities.get(i).name)) {
                            studyScopeGeography = myMainModel.allGISData.countries.get(countryIndex).states.get(stateIndex).counties.get(countyIndex).cities.get(i);
                        }
                    }
                }
            } else {
                System.out.println("GEOGRAPHY DATA IS UNAVAILABLE TO GET CASE STUDY'S GEOGRAPHY. calculateStudyScopeGeography SKIPPED!");
            }
        } else {
            System.out.println("GEOGRAPHY DATA IS UNAVAILABLE TO GET CASE STUDY'S GEOGRAPHY. calculateStudyScopeGeography SKIPPED!");
        }
    }
}
