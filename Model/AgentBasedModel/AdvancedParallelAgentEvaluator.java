package COVID_AgentBasedSimulation.Model.AgentBasedModel;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.MainModel;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author user
 */
public class AdvancedParallelAgentEvaluator extends ParallelProcessor {

    Runnable myRunnable;

    public AdvancedParallelAgentEvaluator(MainModel parent, CopyOnWriteArrayList<Agent> data, int startIndex, int endIndex, boolean isHardCoded) {
        super(parent, data, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if (isHardCoded == false) {
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
                } else {
                    Agent currentEvaluatingAgent[] = new Agent[1];
                    try {
//                System.out.println(myStartIndex);
//                System.out.println(myEndIndex);
                        for (int i = myStartIndex; i < myEndIndex; i++) {
                            currentEvaluatingAgent[0] = data.get(i);
                            currentEvaluatingAgent[0].behavior();
                        }
                    } catch (Exception ex) {
                        System.out.println("ERROR ON AGENT TYPE:");
                        System.out.println(currentEvaluatingAgent[0].myType);
                        System.out.println("ERROR ON AGENT INDEX:");
                        System.out.println(currentEvaluatingAgent[0].myIndex);
                        ex.printStackTrace();
                    }
                }

            }
        };
    }

    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }

}
