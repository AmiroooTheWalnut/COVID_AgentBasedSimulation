package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AbmCli {

    public MainModel mainModel;
    public int numProcessorsInModel;
    public int numProcessorsInTests;

    public ExecutorService testThreadPool;

    public int currentNumRun = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        AbmCli test = new AbmCli();

        RunConfig runConfig = RunConfig.loadModel(args[1]);
        test.init(args[2], args[0], runConfig);
    }

    public void init(String datasetRoot, String projectLocation, RunConfig runConfig) {
        if (runConfig.numCPUsInModel > 0) {
            numProcessorsInModel = runConfig.numCPUsInModel;
        } else {
            numProcessorsInModel = Runtime.getRuntime().availableProcessors() / 2;
        }
        if (runConfig.isParallelTests == false) {
            Timer refreshSimulationDialogTimer = new Timer();
            refreshSimulationDialogTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mainModel != null) {
                        if (mainModel.isRunning == false) {
                            if (currentNumRun < runConfig.numRuns) {
                                runARun(datasetRoot, projectLocation, runConfig);
                                currentNumRun = currentNumRun + 1;
                            }
                        }
                    } else {
                        if (currentNumRun < runConfig.numRuns) {
                            runARun(datasetRoot, projectLocation, runConfig);
                            currentNumRun = currentNumRun + 1;
                        }
                    }
                }
            }, 0, 1000);
        } else {
            if (runConfig.numCPUsInTest > 0) {
                numProcessorsInTests = runConfig.numCPUsInTest;
            } else {
                numProcessorsInTests = Runtime.getRuntime().availableProcessors() / 2;
            }
            testThreadPool = Executors.newFixedThreadPool(numProcessorsInTests);
            try {
                AdvancedParallelTest[] parallelTest = new AdvancedParallelTest[runConfig.numRuns];

                for (int i = 0; i < runConfig.numRuns - 1; i++) {
                    parallelTest[i] = new AdvancedParallelTest(this, datasetRoot, projectLocation, runConfig, numProcessorsInModel, -1, -1);
                }
                parallelTest[runConfig.numRuns - 1] = new AdvancedParallelTest(this, datasetRoot, projectLocation, runConfig, numProcessorsInModel, -1, -1);

                ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();

                for (int i = 0; i < runConfig.numRuns; i++) {
                    parallelTest[i].addRunnableToQueue(calls);
                }

                testThreadPool.invokeAll(calls);
            } catch (InterruptedException ex) {
                Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void runARun(String datasetRoot, String projectLocation, RunConfig runConfig) {
        mainModel = new MainModel();
        mainModel.datasetDirectory = datasetRoot;
        mainModel.numCPUs = numProcessorsInModel;

        mainModel.initAgentBasedModel(false);
        mainModel.initData();

        try {
            File geoDataFile = new File(datasetRoot+"/ProcessedGeoData.bin");
            if (geoDataFile.exists()) {
                AllGISData geoData = MainModel.loadAllGISDataKryo(datasetRoot+"/ProcessedGeoData.bin");
                mainModel.allGISData = geoData;
                System.out.println("Geographical data loaded");
            } else {
                System.out.println("<html>No processed geographical data detected.<br/>You can preprocess the data.</html>");
            }
        } catch (Exception ex) {
            System.out.println("Error in reading GIS data!");
        }

        try {
            File casesDataFile = new File(datasetRoot+"/ProcessedCasesData.bin");
            if (casesDataFile.exists()) {
                CovidCsseJhu casesData = MainModel.loadCasesDataKryo(datasetRoot+"/ProcessedCasesData.bin");
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
        //mainModel.javaEvaluationEngine.connectToConsole(jTextArea1);
        //mainModel.pythonEvaluationEngine.connectToConsole(jTextArea2);
        mainModel.loadAndConnectSupplementaryCaseStudyDataKryo(datasetRoot+"/Safegraph/" + mainModel.ABM.studyScope + "/supplementaryGIS.bin");
//          myParent.mainModel.allGISData.loadScopeCBGPolygons((Scope)(myParent.mainModel.ABM.studyScopeGeography));//THIS IS NOW IN SUPPLAMENTARY DATA
        ArrayList<Integer> infectionIndices = new ArrayList();
        if (runConfig.isSpecialScenarioActive == false) {
            if (runConfig.isSpecificRegionInfected) {
                String[] indices = runConfig.infectedRegionIndicesString.split(",");
                for (int i = 0; i < indices.length; i++) {
                    infectionIndices.add(Integer.valueOf(indices[i]));
                }
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
        mainModel.initModelHardCoded(true, runConfig.isParallelBehaviorEvaluation, runConfig.numResidents, numRegions, runConfig.numCPUsInModel, !runConfig.isSpecificRegionInfected, runConfig.isSpecialScenarioActive, infectionIndices);
        mainModel.startTimeNanoSecond = System.nanoTime();
        mainModel.resume(runConfig.isParallelBehaviorEvaluation, runConfig.numCPUsInModel, true, runConfig.isSpecialScenarioActive);
    }
}
