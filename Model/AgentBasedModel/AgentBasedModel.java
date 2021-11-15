/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root;
import COVID_AgentBasedSimulation.Model.MainModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.Callable;
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
    public transient Root root;
    public transient Agent rootAgent;
    public transient CopyOnWriteArrayList<Agent> agents;

    public String startTimeString;
    public String endTimeString;

    public String studyScope = "FullData";
    public transient Object studyScopeGeography;

    public boolean isReportContactRate = true;

    public boolean isOurABMActive = false;
    public boolean isShamilABMActive = false;
    public boolean isAirQualityActive = false;

    private transient MainModel myMainModel;

//    public transient Agent currentEvaluatingAgent[] = new Agent[1];
//    public transient Agent oldCurrentEvaluatingAgent[] = new Agent[1];
    public transient ZonedDateTime startTime;
    public transient ZonedDateTime currentTime;
    public transient ZonedDateTime endTime;

    public boolean isPatternBasedTime = true;

    public AgentBasedModel(MainModel mainModel) {
        myMainModel = mainModel;
//        currentTime.geth
//        FileWriter fileWritter = new FileWriter("",true);
    }

    public void evaluateAllAgents(int numCPU, boolean isHardCoded) {
        if (isHardCoded == false) {
            Agent currentEvaluatingAgent[] = new Agent[1];
            try {
                currentEvaluatingAgent[0] = rootAgent;
                if (rootAgent.myTemplate.behavior.isJavaScriptActive == true) {
                    //myMainModel.javaEvaluationEngine.runScript(rootAgent.myTemplate.behavior.javaScript.script);

                    myMainModel.javaEvaluationEngine.runParsedScript(rootAgent, rootAgent.myTemplate.behavior.javaScript.parsedScript);

                } else {
                    myMainModel.pythonEvaluationEngine.runScript(rootAgent.myTemplate.behavior.pythonScript);
                }
//                    for (int i = 0; i < agents.size(); i++) {
//                        currentEvaluatingAgent[0] = agents.get(i);
//                        if (agents.get(i).myTemplate.behavior.isJavaScriptActive == true) {
//                            //myMainModel.javaEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.javaScript.script);
//
//                            myMainModel.javaEvaluationEngine.runParsedScript(agents.get(i), agents.get(i).myTemplate.behavior.javaScript.parsedScript);
//                        } else {
//                            myMainModel.pythonEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.pythonScript);
//                        }
//                    }

                int numProcessors = numCPU;
//                    if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//                        numProcessors = Runtime.getRuntime().availableProcessors();
//                    }
                AdvancedParallelAgentEvaluator parallelAgentEval[] = new AdvancedParallelAgentEvaluator[numProcessors];

                for (int i = 0; i < numProcessors - 1; i++) {
                    parallelAgentEval[i] = new AdvancedParallelAgentEvaluator(myMainModel, agents, (int) Math.floor(i * ((agents.size()) / numProcessors)), (int) Math.floor((i + 1) * ((agents.size()) / numProcessors)), isHardCoded);
                }
                parallelAgentEval[numProcessors - 1] = new AdvancedParallelAgentEvaluator(myMainModel, agents, (int) Math.floor((numProcessors - 1) * ((agents.size()) / numProcessors)), agents.size(), isHardCoded);

                ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();
                
                for (int i = 0; i < numProcessors; i++) {
                    parallelAgentEval[i].addRunnableToQueue(calls);
                }
                
                myMainModel.agentEvalPool.invokeAny(calls);
                
            //\/\/\/ OLD DESIGN WITH THREADS
//                for (int i = 0; i < numProcessors; i++) {
//                    parallelAgentEval[i].myThread.start();
//                }
//                for (int i = 0; i < numProcessors; i++) {
//                    try {
//                        parallelAgentEval[i].myThread.join();
////                        System.out.println("thread " + i + "finished for records: " + parallelAgentEval[i].myStartIndex + " | " + parallelAgentEval[i].myEndIndex);
//                    } catch (InterruptedException ie) {
//                        System.out.println(ie.toString());
//                    }
//                }
            //^^^ OLD DESIGN WITH THREADS

            } catch (Exception ex) {
                System.out.println("ERROR ON AGENT TYPE:");
                System.out.println(currentEvaluatingAgent[0].myTemplate.agentTypeName);
                System.out.println("ERROR ON AGENT INDEX:");
                System.out.println(currentEvaluatingAgent[0].myIndex);
                ex.printStackTrace();
            }
        } else {
            Agent currentEvaluatingAgent[] = new Agent[1];
            try {
                currentEvaluatingAgent[0] = root;

                currentEvaluatingAgent[0].behavior();

//                if (rootAgent.myTemplate.behavior.isJavaScriptActive == true) {
//                    //myMainModel.javaEvaluationEngine.runScript(rootAgent.myTemplate.behavior.javaScript.script);
//
//                    myMainModel.javaEvaluationEngine.runParsedScript(rootAgent, rootAgent.myTemplate.behavior.javaScript.parsedScript);
//
//                } else {
//                    myMainModel.pythonEvaluationEngine.runScript(rootAgent.myTemplate.behavior.pythonScript);
//                }
//                    for (int i = 0; i < agents.size(); i++) {
//                        currentEvaluatingAgent[0] = agents.get(i);
//                        currentEvaluatingAgent[0].behavior();
//                        
////                        if (agents.get(i).myTemplate.behavior.isJavaScriptActive == true) {
////                            //myMainModel.javaEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.javaScript.script);
////
////                            myMainModel.javaEvaluationEngine.runParsedScript(agents.get(i), agents.get(i).myTemplate.behavior.javaScript.parsedScript);
////                        } else {
////                            myMainModel.pythonEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.pythonScript);
////                        }
//                    }
//                for (int i = 0; i < agents.size(); i++) {
//                    
//                }
                int numProcessors = numCPU;
//                    if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//                        numProcessors = Runtime.getRuntime().availableProcessors();
//                    }
                AdvancedParallelAgentEvaluator parallelAgentEval[] = new AdvancedParallelAgentEvaluator[numProcessors];

                for (int i = 0; i < numProcessors - 1; i++) {
                    parallelAgentEval[i] = new AdvancedParallelAgentEvaluator(myMainModel, agents, (int) Math.floor(i * ((agents.size()) / numProcessors)), (int) Math.floor((i + 1) * ((agents.size()) / numProcessors)), isHardCoded);
                }
                parallelAgentEval[numProcessors - 1] = new AdvancedParallelAgentEvaluator(myMainModel, agents, (int) Math.floor((numProcessors - 1) * ((agents.size()) / numProcessors)), agents.size(), isHardCoded);

                ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();
                
                for (int i = 0; i < numProcessors; i++) {
                    parallelAgentEval[i].addRunnableToQueue(calls);
                }
                
                myMainModel.agentEvalPool.invokeAny(calls);
                
            //\/\/\/ OLD DESIGN WITH THREADS
//                for (int i = 0; i < numProcessors; i++) {
//                    parallelAgentEval[i].myThread.start();
//                }
//                for (int i = 0; i < numProcessors; i++) {
//                    try {
//                        parallelAgentEval[i].myThread.join();
////                        System.out.println("thread " + i + "finished for records: " + parallelAgentEval[i].myStartIndex + " | " + parallelAgentEval[i].myEndIndex);
//                    } catch (InterruptedException ie) {
//                        System.out.println(ie.toString());
//                    }
//                }
            //^^^ OLD DESIGN WITH THREADS

            } catch (Exception ex) {
                System.out.println("ERROR ON AGENT TYPE:");
                System.out.println(currentEvaluatingAgent[0].myType);
                System.out.println("ERROR ON AGENT INDEX:");
                System.out.println(currentEvaluatingAgent[0].myIndex);
                ex.printStackTrace();
            }
        }

    }

    public Agent makeAgentByType(String agentTemplate) {
//        switch (agentTemplate) {
//            case "Person":
//                AgentTemplate template = null;
//                for (int i = 0; i < agentTemplates.size(); i++) {
//                    if (agentTemplates.get(i).agentTypeName.equals(agentTemplate)) {
//                        template = agentTemplates.get(i);
//                        Person output = new Person();
//                        output.statusNames = template.statusNames;
//                        output.statusValues = template.statusValues;
//                        output.myIndex = agents.size();
//                        agents.add(output);
//                        output.constructor(myMainModel);
//                        return output;
//                    }
//                }
//            case "CBG":
//                CBG cbg = new CBG();
////                template = null;
//                for (int i = 0; i < agentTemplates.size(); i++) {
//                    if (agentTemplates.get(i).agentTypeName.equals(agentTemplate)) {
////                        template = agentTemplates.get(i);
//                        cbg.statusNames = agentTemplates.get(i).statusNames;
//                        cbg.statusValues = agentTemplates.get(i).statusValues;
//                    }
//                }
//                cbg.myIndex = agents.size();
//                agents.add(cbg);
//                cbg.constructor(myMainModel);
//                return cbg;
//            case "VD":
//                VD vd = new VD();
////                template = null;
//                for (int i = 0; i < agentTemplates.size(); i++) {
//                    if (agentTemplates.get(i).agentTypeName.equals(agentTemplate)) {
////                        template = agentTemplates.get(i);
//                        vd.statusNames = agentTemplates.get(i).statusNames;
//                        vd.statusValues = agentTemplates.get(i).statusValues;
//                    }
//                }
//                vd.myIndex = agents.size();
//                agents.add(vd);
//                vd.constructor(myMainModel);
//                return vd;
//            case "CBGVD":
//                CBGVD cbgvd = new CBGVD();
////                template = null;
//                for (int i = 0; i < agentTemplates.size(); i++) {
//                    if (agentTemplates.get(i).agentTypeName.equals(agentTemplate)) {
////                        template = agentTemplates.get(i);
//                        cbgvd.statusNames = agentTemplates.get(i).statusNames;
//                        cbgvd.statusValues = agentTemplates.get(i).statusValues;
//                    }
//                }
//                cbgvd.myIndex = agents.size();
//                agents.add(cbgvd);
//                cbgvd.constructor(myMainModel);
//                return cbgvd;
//            default: // Optional
//                Agent output_default = new Agent();
//                output_default.myIndex = agents.size();
//                agents.add(output_default);
//                output_default.constructor(myMainModel);
//                return output_default;
//        }

        return null;
    }

    public Agent makeAgent(String agentTemplate) {
//        Agent currentEvaluatingAgent[] = new Agent[1];
//        Agent oldCurrentEvaluatingAgent[] = new Agent[1];
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
//                copiedTemplate.constructor = template.constructor;
//                copiedTemplate.behavior = template.behavior;
//                copiedTemplate.destructor = template.destructor;
                copiedTemplate.statusNames = template.statusNames;
                copiedTemplate.statusValues = template.statusValues;
                Agent output = new Agent(copiedTemplate);
                output.myIndex = agents.size();
                agents.add(output);
//                oldCurrentEvaluatingAgent[0] = self;
//                currentEvaluatingAgent = new Agent[1];
//                currentEvaluatingAgent[0] = output;
                output.myTemplate.constructor.javaScript.myShell = new GroovyShell(new Binding());
                output.myTemplate.behavior.javaScript.myShell = new GroovyShell(new Binding());
                output.myTemplate.destructor.javaScript.myShell = new GroovyShell(new Binding());

                output.myTemplate.constructor.javaScript.parsedScript = output.myTemplate.constructor.javaScript.myShell.parse(output.myTemplate.constructor.javaScript.script);
                output.myTemplate.behavior.javaScript.parsedScript = output.myTemplate.behavior.javaScript.myShell.parse(output.myTemplate.behavior.javaScript.script);
                output.myTemplate.destructor.javaScript.parsedScript = output.myTemplate.destructor.javaScript.myShell.parse(output.myTemplate.destructor.javaScript.script);

                output.myTemplate.constructor.javaScript.parsedScript.setBinding(new Binding());
                output.myTemplate.constructor.javaScript.parsedScript.getBinding().setVariable("modelRoot", myMainModel);
                output.myTemplate.constructor.javaScript.parsedScript.getBinding().setVariable("currentAgent", output);
                output.myTemplate.behavior.javaScript.parsedScript.setBinding(new Binding());
                output.myTemplate.behavior.javaScript.parsedScript.getBinding().setVariable("modelRoot", myMainModel);
                output.myTemplate.behavior.javaScript.parsedScript.getBinding().setVariable("currentAgent", output);
                output.myTemplate.destructor.javaScript.parsedScript.setBinding(new Binding());
                output.myTemplate.destructor.javaScript.parsedScript.getBinding().setVariable("modelRoot", myMainModel);
                output.myTemplate.destructor.javaScript.parsedScript.getBinding().setVariable("currentAgent", output);

                if (output.myTemplate.constructor.isJavaScriptActive == true) {
                    //myMainModel.javaEvaluationEngine.runScript(output.myTemplate.constructor.javaScript.script);

                    myMainModel.javaEvaluationEngine.runParsedScript(output, output.myTemplate.constructor.javaScript.parsedScript);
                } else {
                    myMainModel.pythonEvaluationEngine.runScript(output.myTemplate.constructor.pythonScript);
                }
//                currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];

//                if(output.myTemplate.agentTypeName.equals("Person")){
//                    System.out.println(output.myIndex);
//                    System.out.println("!!!!");
//                }
                return output;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
//        currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];
        return null;
    }

    public Root makeRootAgentHardCoded() {
        //        Agent currentEvaluatingAgent[] = new Agent[1];
//        Agent oldCurrentEvaluatingAgent[] = new Agent[1];
        try {
            AgentTemplate template = null;
            for (int i = 0; i < agentTemplates.size(); i++) {
                if (agentTemplates.get(i).agentTypeName.equals("root")) {
                    template = agentTemplates.get(i);
                    break;
                }
            }
            if (template == null) {
                System.out.println("ERROR: MAKE AGENT: AGENT TEMPLATE NOT FOUND");
                return null;
            } else {
                Root output = new Root(myMainModel);
                output.myIndex = agents.size();
                output.statusNames = template.statusNames;
                output.statusValues = template.statusValues;
                agents.add(output);

                return output;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
        return null;
    }

    public Agent makeRootAgent() {
//        Agent currentEvaluatingAgent[] = new Agent[1];
//        Agent oldCurrentEvaluatingAgent[] = new Agent[1];
        try {
            AgentTemplate template = null;
            for (int i = 0; i < agentTemplates.size(); i++) {
                if (agentTemplates.get(i).agentTypeName.equals("root")) {
                    template = agentTemplates.get(i);
                    break;
                }
            }
            if (template == null) {
                System.out.println("ERROR: MAKE AGENT: AGENT TEMPLATE NOT FOUND");
                return null;
            } else {

                AgentTemplate copiedTemplate = new AgentTemplate(template);
//                copiedTemplate.constructor = template.constructor;
//                copiedTemplate.behavior = template.behavior;
//                copiedTemplate.destructor = template.destructor;
                copiedTemplate.statusNames = template.statusNames;
                copiedTemplate.statusValues = template.statusValues;
                Agent output = new Agent(copiedTemplate);
                output.myIndex = agents.size();
                agents.add(output);
//                oldCurrentEvaluatingAgent[0] = self;
//                currentEvaluatingAgent = new Agent[1];
//                currentEvaluatingAgent[0] = output;
                output.myTemplate.constructor.javaScript.myShell = new GroovyShell(new Binding());
                output.myTemplate.behavior.javaScript.myShell = new GroovyShell(new Binding());
                output.myTemplate.destructor.javaScript.myShell = new GroovyShell(new Binding());

                output.myTemplate.constructor.javaScript.parsedScript = output.myTemplate.constructor.javaScript.myShell.parse(output.myTemplate.constructor.javaScript.script);
                output.myTemplate.behavior.javaScript.parsedScript = output.myTemplate.behavior.javaScript.myShell.parse(output.myTemplate.behavior.javaScript.script);
                output.myTemplate.destructor.javaScript.parsedScript = output.myTemplate.destructor.javaScript.myShell.parse(output.myTemplate.destructor.javaScript.script);

                output.myTemplate.constructor.javaScript.parsedScript.setBinding(new Binding());
                output.myTemplate.constructor.javaScript.parsedScript.getBinding().setVariable("modelRoot", myMainModel);
                output.myTemplate.constructor.javaScript.parsedScript.getBinding().setVariable("currentAgent", output);
                output.myTemplate.behavior.javaScript.parsedScript.setBinding(new Binding());
                output.myTemplate.behavior.javaScript.parsedScript.getBinding().setVariable("modelRoot", myMainModel);
                output.myTemplate.behavior.javaScript.parsedScript.getBinding().setVariable("currentAgent", output);
                output.myTemplate.destructor.javaScript.parsedScript.setBinding(new Binding());
                output.myTemplate.destructor.javaScript.parsedScript.getBinding().setVariable("modelRoot", myMainModel);
                output.myTemplate.destructor.javaScript.parsedScript.getBinding().setVariable("currentAgent", output);

                return output;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
//        currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];
        return null;
    }

    public void removeAgent(Agent agent) {
        if (agent.myTemplate.destructor.isJavaScriptActive == true) {
            //myMainModel.javaEvaluationEngine.runScript(agent.myTemplate.destructor.javaScript.script);

            myMainModel.javaEvaluationEngine.runParsedScript(agent, agent.myTemplate.destructor.javaScript.parsedScript);
        } else {
            myMainModel.pythonEvaluationEngine.runScript(agent.myTemplate.destructor.pythonScript);
        }
        agents.remove(agent.myIndex);
    }

    public void saveModel(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(this);
        //System.out.println(result);
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
            if (result.agentTemplates != null) {
                agentTemplates = result.agentTemplates;
            }
            if (result.startTimeString != null) {
                startTimeString = result.startTimeString;
            }
            if (result.endTimeString != null) {
                endTimeString = result.endTimeString;
            }
            if (result.studyScope != null) {
                studyScope = result.studyScope;
            }

            calculateStudyScopeGeography();
            if (startTimeString != null) {
                if (startTimeString.length() > 0) {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(startTimeString);
                    startTime = zonedDateTime;
                }
            }
            if (endTimeString != null) {
                if (endTimeString.length() > 0) {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(endTimeString);
                    endTime = zonedDateTime;
                }
            }
            isOurABMActive = result.isOurABMActive;
            isShamilABMActive = result.isShamilABMActive;
            isAirQualityActive = result.isAirQualityActive;
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
                if (studyScope != null) {
                    if (studyScope.equals("FullData")) {
                        studyScopeGeography = myMainModel.allGISData.countries.get(0);
                    } else {
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
