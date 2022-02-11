package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AbmCli {

    public MainModel mainModel;
    public int numProcessors;

    public int currentNumRun = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        AbmCli test = new AbmCli();

        RunConfig runConfig = RunConfig.loadModel(args[1]);
        test.init(args[0], runConfig);
    }

    public void init(String projectLocation, RunConfig runConfig) {
        if (runConfig.numCPUs > 0) {
            numProcessors = runConfig.numCPUs;
        } else {
            numProcessors = Runtime.getRuntime().availableProcessors() / 2;
        }
        Timer refreshSimulationDialogTimer = new Timer();
        refreshSimulationDialogTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mainModel != null) {
                    if (mainModel.isRunning == false) {
                        if (currentNumRun < runConfig.numRuns) {
                            runARun(projectLocation, runConfig);
                            currentNumRun = currentNumRun + 1;
                        }
                    }
                } else {
                    if (currentNumRun < runConfig.numRuns) {
                        runARun(projectLocation, runConfig);
                        currentNumRun = currentNumRun + 1;
                    }
                }
            }
        }, 0, 1000);
    }

    public void runARun(String projectLocation, RunConfig runConfig) {
        mainModel = new MainModel();
        mainModel.numCPUs = numProcessors;

        mainModel.initAgentBasedModel(false);
        mainModel.initData();

        try {
            File geoDataFile = new File("./datasets/ProcessedGeoData.bin");
            if (geoDataFile.exists()) {
                AllGISData geoData = MainModel.loadAllGISDataKryo("./datasets/ProcessedGeoData.bin");
                mainModel.allGISData = geoData;
                System.out.println("Geographical data loaded");
            } else {
                System.out.println("<html>No processed geographical data detected.<br/>You can preprocess the data.</html>");
            }
        } catch (Exception ex) {
            System.out.println("Error in reading GIS data!");
        }

        try {
            File casesDataFile = new File("./datasets/ProcessedCasesData.bin");
            if (casesDataFile.exists()) {
                CovidCsseJhu casesData = MainModel.loadCasesDataKryo("./datasets/ProcessedCasesData.bin");
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
        mainModel.loadAndConnectSupplementaryCaseStudyDataKryo("./datasets/Safegraph/" + mainModel.ABM.studyScope + "/supplementaryGIS.bin");
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
        mainModel.initModelHardCoded(true, runConfig.isParallelBehaviorEvaluation, runConfig.numResidents, numRegions, runConfig.numCPUs, !runConfig.isSpecificRegionInfected, runConfig.isSpecialScenarioActive, infectionIndices);
        mainModel.resume(runConfig.isParallelBehaviorEvaluation, runConfig.numCPUs, true, runConfig.isSpecialScenarioActive);
    }
}
