/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.Model.MainModel;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author user
 */
public class ParallelAgentEvaluator extends ParallelProcessor {

    public ParallelAgentEvaluator(MainModel parent, CopyOnWriteArrayList<Agent> data, int startIndex, int endIndex) {
        super(parent, data, startIndex, endIndex);

        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Agent currentEvaluatingAgent[] = new Agent[1];
                try {
//                System.out.println(myStartIndex);
//                System.out.println(myEndIndex);
                    for (int i = myStartIndex; i < myEndIndex; i++) {
                        currentEvaluatingAgent[0] = data.get(i);
                        if (data.get(i).myTemplate.behavior.isJavaScriptActive == true) {
                            //myMainModel.javaEvaluationEngine.runScript(agents.get(i).myTemplate.behavior.javaScript.script);

                            parent.javaEvaluationEngine.runParsedScript(data.get(i), data.get(i).myTemplate.behavior.javaScript.parsedScript);
                        } else {
                            parent.pythonEvaluationEngine.runScript(data.get(i).myTemplate.behavior.pythonScript);
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
        });

    }

}
