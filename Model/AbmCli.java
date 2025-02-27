package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.Matching.MatchingData;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        if (args.length > 3) {
            String revArg = args[3].replaceAll("\\s", "");
            parseExceptions(runConfig, revArg);
        }
        test.init(args[2], args[0], runConfig);
    }

    public static void parseExceptions(RunConfig base, String input) {
        if (input.startsWith("_")) {
            input = input.substring(1);
            String[] pairs = input.split(":");
            for (int i = 0; i < pairs.length; i++) {
                String[] varValRaw = pairs[i].split("_");
                String[] varVal = new String[2];
                varVal[0] = varValRaw[0];
                varVal[1] = "";
                for (int m = 1; m < varValRaw.length; m++) {
                    varVal[1] = varVal[1] + varValRaw[m];
                    if (m != varValRaw.length - 1) {
                        varVal[1] = varVal[1] + "_";
                    }
                }
                try {
                    Field field = RunConfig.class.getField(varVal[0]);
                    if (field.getType().getTypeName().equals("java.lang.String")) {
                        field.set(base, varVal[1]);
                    } else if (field.getType().getTypeName().equals("int")) {
                        field.set(base, Integer.valueOf(varVal[1]));
                    } else if (field.getType().getTypeName().equals("boolean")) {
                        field.set(base, Boolean.valueOf(varVal[1]));
                    }

                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
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
            List<Future<Object>> futures=null;
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

                futures = testThreadPool.invokeAll(calls);
            } catch (InterruptedException ex) {
                System.out.println("ERROR IN RUNNING AdvancedParallelTest");
                Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int i = 0; i < futures.size(); i++) {
                Future<Object> a = futures.get(i);
                try {
                    a.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
//                    ex.getCause().printStackTrace();
                    Logger.getLogger(AbmCli.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            testThreadPool.shutdown();
        }
    }

    public void runARun(String datasetRoot, String projectLocation, RunConfig runConfig) {
        mainModel = new MainModel();
        mainModel.datasetDirectory = datasetRoot;
        mainModel.numCPUs = numProcessorsInModel;

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
        
        mainModel.ABM.isMatching = runConfig.isMatching;
        if(mainModel.ABM.isMatching==true){
            mainModel.ABM.matchingData=new MatchingData();
            mainModel.ABM.matchingData.readData(runConfig.matchingFilePath,runConfig.geoFilePath);
            mainModel.ABM.matchingData.parseNAICSRules(runConfig.pOIType1Patterns, runConfig.pOIType2Patterns);
        }

        mainModel.ABM.isReportContactRate = runConfig.isReportContactRate;
        mainModel.ABM.isFuzzyStatus = runConfig.isFuzzyStatus;
        mainModel.ABM.isSaveHistoricalRun = runConfig.isSaveHistoricalRun;
        
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
            mainModel.initModelHardCoded(false, true, runConfig.isParallelBehaviorEvaluation, runConfig.numResidents, numRegions, runConfig.numCPUsInModel, !runConfig.isSpecificRegionInfected, runConfig.isSpecialScenarioActive, infectionIndices,fixedNumInfected, 1);
        } else {
            mainModel.initModelArtificial(false, true, runConfig.isParallelBehaviorEvaluation, runConfig.numResidents, numRegions, runConfig.numCPUsInModel, !runConfig.isSpecificRegionInfected, runConfig.isSpecialScenarioActive, infectionIndices, runConfig.noTessellationNumResidents,fixedNumInfected, 1);
        }
        mainModel.ABM.agents = new CopyOnWriteArrayList(mainModel.ABM.agentsRaw);
        if(mainModel.ABM.isMatching==true){
            mainModel.ABM.matchingData.findPOIs(mainModel);
        }
        mainModel.startTimeNanoSecond = System.nanoTime();
        mainModel.resume(false, runConfig.isParallelBehaviorEvaluation, runConfig.numCPUsInModel, true, runConfig.isSpecialScenarioActive, 1);
    }
}
