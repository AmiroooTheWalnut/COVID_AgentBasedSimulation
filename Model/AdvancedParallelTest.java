/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AdvancedParallelTest extends ParallelProcessor {

    Runnable myRunnable;

    public AdvancedParallelTest(AbmCli parent, String datasetRoot, String projectLocation, RunConfig runConfig, int numProcessors, int startIndex, int endIndex) {
        super(parent, runConfig, startIndex, endIndex);

        myRunnable = new Runnable() {
            @Override
            public void run() {
                MainModel mainModel = new MainModel();
                mainModel.datasetDirectory = datasetRoot;
                mainModel.numCPUs = numProcessors;

                mainModel.initAgentBasedModel(false);
                mainModel.initData();

                try {
                    File geoDataFile = new File(datasetRoot + "/ProcessedGeoData.bin");
                    if (geoDataFile.exists()) {
                        AllGISData geoData = MainModel.loadAllGISDataKryo(datasetRoot + "/ProcessedGeoData.bin");
                        mainModel.allGISData = geoData;
                        System.out.println("Geographical data loaded");
                    } else {
                        System.out.println("<html>No processed geographical data detected.<br/>You can preprocess the data.</html>");
                    }
                } catch (Exception ex) {
                    System.out.println("Error in reading GIS data!");
                }

                try {
                    File casesDataFile = new File(datasetRoot + "/ProcessedCasesData.bin");
                    if (casesDataFile.exists()) {
                        CovidCsseJhu casesData = MainModel.loadCasesDataKryo(datasetRoot + "/ProcessedCasesData.bin");
                        mainModel.covidCsseJhu = casesData;
                        System.out.println("Cases data loaded");
                    } else {
                        System.out.println("<html>No processed cases data detected.<br/>You can preprocess the data.</html>");
                    }
                } catch (Exception ex) {
                    System.out.println("Error in reading cases data!");
                }

                mainModel.ABM.loadModel(projectLocation);
                mainModel.ABM.filePath = projectLocation;

                mainModel.ABM.isReportContactRate = runConfig.isReportContactRate;
                mainModel.ABM.isFuzzyStatus = runConfig.isFuzzyStatus;
                //mainModel.javaEvaluationEngine.connectToConsole(jTextArea1);
                //mainModel.pythonEvaluationEngine.connectToConsole(jTextArea2);
                mainModel.loadAndConnectSupplementaryCaseStudyDataKryo(datasetRoot + "/Safegraph/" + mainModel.ABM.studyScope + "/supplementaryGIS.bin");
//          myParent.mainModel.allGISData.loadScopeCBGPolygons((Scope)(myParent.mainModel.ABM.studyScopeGeography));//THIS IS NOW IN SUPPLAMENTARY DATA
                ArrayList<Integer> infectionIndices = new ArrayList();
                int fixedNumInfected=-1;
                if (runConfig.isSpecialScenarioActive == false) {
                    if (runConfig.isSpecificRegionInfected) {
                        String[] indices = runConfig.infectedRegionIndicesString.split(",");
                        for (int i = 0; i < indices.length; i++) {
                            infectionIndices.add(Integer.valueOf(indices[i]));
                        }
                    }else{
                        fixedNumInfected=runConfig.fixedNumInfected;
                    }
                } else {
                    infectionIndices.add(runConfig.CBGIndexToInfect);
                }
                mainModel.scenario.scenarioName = runConfig.scenarioName;
                int numRegions = -1;
                if (mainModel.scenario.scenarioName.contains("VDFNC_") || mainModel.scenario.scenarioName.contains("RMCBG_")) {
                    String values[] = mainModel.scenario.scenarioName.split("_");
                    numRegions = Integer.parseInt(values[1]);
                }
                mainModel.simulationDelayTime = -1;
                if (runConfig.isArtificial == false) {
                    mainModel.initModelHardCoded(false, true, runConfig.isParallelBehaviorEvaluation, runConfig.numResidents, numRegions, runConfig.numCPUsInModel, !runConfig.isSpecificRegionInfected, runConfig.isSpecialScenarioActive, infectionIndices,fixedNumInfected);
                } else {
                    mainModel.initModelArtificial(false, true, runConfig.isParallelBehaviorEvaluation, runConfig.numResidents, numRegions, runConfig.numCPUsInModel, !runConfig.isSpecificRegionInfected, runConfig.isSpecialScenarioActive, infectionIndices, runConfig.noTessellationNumResidents,fixedNumInfected);
                }
                mainModel.ABM.agents= new CopyOnWriteArrayList(mainModel.ABM.agentsRaw);
                mainModel.startTimeNanoSecond = System.nanoTime();
                mainModel.resume(false, runConfig.isParallelBehaviorEvaluation, runConfig.numCPUsInModel, true, runConfig.isSpecialScenarioActive);
            }
        };
    }

    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }

}
