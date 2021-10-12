package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Engines.PythonEvaluationEngine;
import COVID_AgentBasedSimulation.Model.Engines.JavaEvaluationEngine;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentTemplate;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.BehaviorScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.JavaScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.PythonScript;
import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Safegraph;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.SupplementaryCaseStudyData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import org.objenesis.strategy.StdInstantiatorStrategy;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class MainModel extends Dataset {

    public static final long softwareVersion = 1L;
    static final long serialVersionUID = softwareVersion;

    public Safegraph safegraph;
    public AllGISData allGISData;
    public SupplementaryCaseStudyData supplementaryCaseStudyData;
    public CovidCsseJhu covidCsseJhu;

//    public ArrayList<Person> people;
    public AgentBasedModel ABM;

    public JavaEvaluationEngine javaEvaluationEngine;
    public PythonEvaluationEngine pythonEvaluationEngine;

    public Timer simulationTimer;
    public TimerTask runTask;
    public int simulationDelayTime = 5000;
    public boolean isFastForward = false;

    public int currentMonth = -1;

    public boolean isStartBySafegraph = false;

    public transient int numCPUs = 1;

    public boolean isPause = false;
    public boolean isRunning = false;

    public int newSimulationDelayTime = -2;

    public String scenario = "CBG";
    
    public double sparsifyFraction=1;
    public int lastMonthLoaded;//NOT USED
    public int lastYearLoaded;//NOT USED

    private Thread thread;

    public void startScriptEngines() {
        javaEvaluationEngine = new JavaEvaluationEngine(this);
        pythonEvaluationEngine = new PythonEvaluationEngine(this);
    }

    public void initData() {
        initSafegraph();
        allGISData = new AllGISData();
        allGISData.setDatasetTemplate();
        covidCsseJhu = new CovidCsseJhu();
        covidCsseJhu.setDatasetTemplate();
    }

    public void loadAndConnectSupplementaryCaseStudyDataKryo(String passed_file_path) {
        if (ABM.studyScopeGeography instanceof City) {
            if (allGISData != null) {
                City city = (City) (ABM.studyScopeGeography);
                SupplementaryCaseStudyData scsd = loadSupplementaryCaseStudyDataKryo(passed_file_path);
                for (int i = 0; i < scsd.vDCells.size(); i++) {
                    scsd.vDCells.get(i).cBGsInvolved = new ArrayList();
                    float avgLat = 0;
                    float avgLon = 0;
                    int counter = 0;
                    for (int j = 0; j < scsd.vDCells.get(i).cBGsIDsInvolved.size(); j++) {
                        CensusBlockGroup cbg = allGISData.findCensusBlockGroup(scsd.vDCells.get(i).cBGsIDsInvolved.get(j));
                        if (cbg == null) {
                            System.out.println("SVERE ERROR WHILE CONNECTING SUPPLEMENTARY DATA: CBG IS NULL!");
                        } else {
                            avgLat = avgLat + cbg.lat;
                            avgLon = avgLon + cbg.lon;
                            counter += 1;
                            scsd.vDCells.get(i).cBGsInvolved.add(cbg);
                            scsd.vDCells.get(i).population += cbg.population * scsd.vDCells.get(i).cBGsPercentageInvolved.get(j);
                        }
                    }
                    scsd.vDCells.get(i).lat = avgLat / ((float) counter);
                    scsd.vDCells.get(i).lon = avgLon / ((float) counter);
                }
                for (int i = 0; i < scsd.cBGVDCells.size(); i++) {
                    scsd.cBGVDCells.get(i).cBGsInvolved = new ArrayList();
                    float avgLat = 0;
                    float avgLon = 0;
                    int counter = 0;
                    for (int j = 0; j < scsd.cBGVDCells.get(i).cBGsIDsInvolved.size(); j++) {
                        CensusBlockGroup cbg = allGISData.findCensusBlockGroup(scsd.cBGVDCells.get(i).cBGsIDsInvolved.get(j));
                        if (cbg == null) {
                            System.out.println("SVERE ERROR WHILE CONNECTING SUPPLEMENTARY DATA: CBG IS NULL!");
                        } else {
                            avgLat = avgLat + cbg.lat;
                            avgLon = avgLon + cbg.lon;
                            counter += 1;
                            scsd.cBGVDCells.get(i).cBGsInvolved.add(cbg);
                            scsd.cBGVDCells.get(i).population += cbg.population * scsd.cBGVDCells.get(i).cBGsPercentageInvolved.get(j);
                        }
                    }
                    scsd.cBGVDCells.get(i).lat = avgLat / ((float) counter);
                    scsd.cBGVDCells.get(i).lon = avgLon / ((float) counter);
                }
                city.vDCells = scsd.vDCells;
                city.cBGVDCells = scsd.cBGVDCells;
            }
        } else {
            System.out.println("HALT! ONLY CITY SCOPE IS IMPLEMENTED!");
        }
    }

    public static SupplementaryCaseStudyData loadSupplementaryCaseStudyDataKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.SupplementaryCaseStudyData.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CBGVDCell.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.VDCell.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.lang.String.class);
        kryo.register(java.lang.Long.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Input input;
        try {
            input = new Input(new FileInputStream(passed_file_path));
            SupplementaryCaseStudyData supplementaryCaseStudyData = kryo.readObject(input, SupplementaryCaseStudyData.class);
            input.close();

            return supplementaryCaseStudyData;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void saveSupplementaryCaseStudyDataKryo(String passed_file_path, SupplementaryCaseStudyData supplementaryCaseStudyData) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.SupplementaryCaseStudyData.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CBGVDCell.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.VDCell.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.lang.String.class);
        kryo.register(java.lang.Long.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, supplementaryCaseStudyData);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setDatasetTemplate() {
        datasetTemplate = new DatasetTemplate();
        datasetTemplate.name = "SimulationVariables";

        try {
            RecordTemplate temp = new RecordTemplate();
            temp.name = this.getClass().getField("startTime").getName() + "(" + this.getClass().getField("startTime").getGenericType().getTypeName() + ")";
            datasetTemplate.recordTemplates.add(temp);

            temp = new RecordTemplate();
            temp.name = this.getClass().getField("endTime").getName() + "(" + this.getClass().getField("endTime").getGenericType().getTypeName() + ")";
            datasetTemplate.recordTemplates.add(temp);

            temp = new RecordTemplate();
            temp.name = this.getClass().getField("currentTime").getName() + "(" + this.getClass().getField("currentTime").getGenericType().getTypeName() + ")";
            datasetTemplate.recordTemplates.add(temp);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initAgentBasedModel() {
        ABM = new AgentBasedModel(this);
        startScriptEngines();
//        agentBasedModel.agents=new ArrayList();
        ABM.agentTemplates = new ArrayList();
        AgentTemplate rootAgentTemplate = new AgentTemplate();
        rootAgentTemplate.agentTypeName = "root";
        rootAgentTemplate.constructor = new BehaviorScript();
        rootAgentTemplate.constructor.javaScript = new JavaScript();
        rootAgentTemplate.constructor.javaScript.script = "";
        rootAgentTemplate.constructor.pythonScript = new PythonScript();
        rootAgentTemplate.constructor.pythonScript.script = "";
        rootAgentTemplate.constructor.isJavaScriptActive = true;

        rootAgentTemplate.behavior = new BehaviorScript();
        rootAgentTemplate.behavior.javaScript = new JavaScript();
        rootAgentTemplate.behavior.javaScript.script = "";
        rootAgentTemplate.behavior.pythonScript = new PythonScript();
        rootAgentTemplate.behavior.pythonScript.script = "";
        rootAgentTemplate.behavior.isJavaScriptActive = true;

        rootAgentTemplate.destructor = new BehaviorScript();
        rootAgentTemplate.destructor.javaScript = new JavaScript();
        rootAgentTemplate.destructor.javaScript.script = "";
        rootAgentTemplate.destructor.pythonScript = new PythonScript();
        rootAgentTemplate.destructor.pythonScript.script = "";
        rootAgentTemplate.destructor.isJavaScriptActive = true;
        ABM.agentTemplates.add(rootAgentTemplate);
    }

    public void initModelHardCoded(boolean isParallelLoadingData, boolean isParallelBehaviorEvaluation, int numCPUs) {
        int month = ABM.startTime.getMonthValue();
        currentMonth = month;
        String monthString = String.valueOf(month);
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        String dateName = ABM.startTime.getYear() + "_" + monthString;
        safegraph.clearPatternsPlaces();
        System.gc();
        safegraph.loadPatternsPlacesSet(dateName, allGISData, ABM.studyScope, isParallelLoadingData, numCPUs);
        ABM.agents = new CopyOnWriteArrayList();

        ABM.currentTime = ABM.startTime;

//        ABM.rootAgent = ABM.makeRootAgentHardCoded();
        
        ABM.root=new Root(this);

        if (scenario.equals("CBG")) {
            ABM.root.constructor(this, 20, "CBG", -1);
        } else if (scenario.equals("VD")) {
            ABM.root.constructor(this, 20, "VD", -1);
        } else if (scenario.equals("CBGVD")) {
            ABM.root.constructor(this, 20, "CBGVD", -1);
        } else if (scenario.equals("ABSVD")) {
            ABM.root.constructor(this, 20, "ABSVD", -1);
        }
        
        
//        if (scenario.equals("CBG")) {
//            ((Root) (ABM.rootAgent)).constructorCBG(this, sparsifyFraction);
//        } else if (scenario.equals("VD")) {
//            ((Root) (ABM.rootAgent)).constructorVD(this);
//        } else if (scenario.equals("CBGVD")) {
//            ((Root) (ABM.rootAgent)).constructorCBGVD(this);
//        } else if (scenario.equals("ABSVD")) {
//            ((Root) (ABM.rootAgent)).constructorABSVD(this);
//        }

        resetTimerTask(isParallelBehaviorEvaluation, numCPUs, true);
    }

    public void initModel(boolean isParallelLoadingData, boolean isParallelBehaviorEvaluation, int numCPUs) {
        //\/\/\/ THIS IS FOR ERROR CHECKING ONLY!
        javaEvaluationEngine.parseAllScripts(ABM.agentTemplates);
        //^^^ THIS IS FOR ERROR CHECKING ONLY!
        int month = ABM.startTime.getMonthValue();
        currentMonth = month;
        String monthString = String.valueOf(month);
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        String dateName = ABM.startTime.getYear() + "_" + monthString;
        safegraph.clearPatternsPlaces();
        System.gc();
        safegraph.loadPatternsPlacesSet(dateName, allGISData, ABM.studyScope, isParallelLoadingData, numCPUs);
        ABM.agents = new CopyOnWriteArrayList();

        ABM.currentTime = ABM.startTime;

        ABM.rootAgent = ABM.makeRootAgent();

        pythonEvaluationEngine.saveAllPythonScripts(ABM.agentTemplates);
        if (ABM.rootAgent.myTemplate.constructor.isJavaScriptActive == true) {
            //myMainModel.javaEvaluationEngine.runScript(output.myTemplate.constructor.javaScript.script);

            javaEvaluationEngine.runParsedScript(ABM.rootAgent, ABM.rootAgent.myTemplate.constructor.javaScript.parsedScript);
        } else {
            pythonEvaluationEngine.runScript(ABM.rootAgent.myTemplate.constructor.pythonScript);
        }
//                currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];

//        if (ABM.rootAgent.myTemplate.agentTypeName.equals("Person")) {
//            System.out.println(ABM.rootAgent.myIndex);
//            System.out.println("!!!!");
//        }
//        ABM.rootAgent = new Agent(ABM.agentTemplates.get(0));
        resetTimerTask(isParallelBehaviorEvaluation, numCPUs, false);

//        if (ABM.rootAgent.myTemplate.constructor.isJavaScriptActive == true) {
//
//            //javaEvaluationEngine.runScript(ABM.rootAgent.myTemplate.constructor.javaScript.script);
//            javaEvaluationEngine.runParsedScript(ABM.rootAgent, ABM.rootAgent.myTemplate.constructor.javaScript.parsedScript);
//        } else {
//            pythonEvaluationEngine.runScript(ABM.rootAgent.myTemplate.constructor.pythonScript);
//        }
    }

    public void resetTimerTask(boolean isParallel, int numCPUs, boolean isHardCoded) {
        runTask = new TimerTask() {
            @Override
            public void run() {
                int month = ABM.startTime.getMonthValue();
                if (currentMonth != month) {
                    String monthString = String.valueOf(month);
                    if (monthString.length() < 2) {
                        monthString = "0" + monthString;
                    }
                    String yearStr = String.valueOf(ABM.startTime.getYear());
                    safegraph.clearPatternsPlaces();
                    System.gc();
                    safegraph.requestDataset(allGISData, ABM.studyScope, yearStr, monthString, true, numCPUs);
                    currentMonth = month;
                }
                ABM.evaluateAllAgents(isParallel, numCPUs, isHardCoded);
                ABM.currentTime = ABM.currentTime.plusMinutes(1);
            }
        };
    }

    public void fastForward(boolean isParallel, int numCPUs, boolean isHardCoded) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isPause == false) {
                    int month = ABM.startTime.getMonthValue();
                    if (currentMonth != month) {
                        String monthString = String.valueOf(month);
                        if (monthString.length() < 2) {
                            monthString = "0" + monthString;
                        }
                        String yearStr = String.valueOf(ABM.startTime.getYear());
                        safegraph.clearPatternsPlaces();
                        System.gc();
                        safegraph.requestDataset(allGISData, ABM.studyScope, yearStr, monthString, true, numCPUs);
                        currentMonth = month;
                    }
                    ABM.evaluateAllAgents(isParallel, numCPUs, isHardCoded);
                    ABM.currentTime = ABM.currentTime.plusMinutes(1);
//                    System.out.println(isPause);
                }
                isPause = false;
                isRunning = false;
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                thread.start();
            }
        });

    }

    public void resume(boolean isParallel, int numCPUs, boolean isHardCoded) {
        if (isHardCoded == true) {
            if (simulationDelayTime > -1) {
                simulationTimer = new Timer();
                resetTimerTask(isParallel, numCPUs, isHardCoded);
                simulationTimer.schedule(runTask, 0, simulationDelayTime);
            } else {
                fastForward(isParallel, numCPUs, isHardCoded);
            }
            isRunning = true;
        } else {
            if (simulationDelayTime > -1) {
                simulationTimer = new Timer();
                resetTimerTask(isParallel, numCPUs, isHardCoded);
                simulationTimer.schedule(runTask, 0, simulationDelayTime);
            } else {
                fastForward(isParallel, numCPUs, isHardCoded);
            }
            isRunning = true;
        }

    }

    public void pause() {
        if (simulationDelayTime > -1) {
            if (simulationTimer != null) {
                simulationTimer.cancel();
                isRunning = false;
            }
        } else {
            isPause = true;
            while (isRunning == true) {
                try {
                    System.out.println("Waiting to pause fastforward run!");
                    Thread.sleep(1 * 100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (newSimulationDelayTime > -2) {
            simulationDelayTime = newSimulationDelayTime;
        }
    }

    public void generatePeopleFromPatterns() {
//        people = new ArrayList();
//        int counter = 0;
//        for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.size(); i++) {
//            for (int j = 0; j < safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
//                if (safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs_place != null) {
//                    for (int k = 0; k < safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs_place.size(); k++) {
//                        Person person = new Person();
//                        person.index = counter;
//                        person.home = safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs_place.get(k).key;
//                        people.add(person);
//                        counter = counter + 1;
//                    }
//                }
//            }
//        }
    }

    public void reset() {
//        generatePeopleFromPatterns();

    }

    public static AllGISData loadAllGISDataKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.AllGISData.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusTract.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.City.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.Country.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.County.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.State.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.ZipCode.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.register(java.lang.Long.class);
        kryo.register(java.lang.Float.class);
        kryo.register(java.time.ZonedDateTime.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Input input;
        try {
            input = new Input(new FileInputStream(passed_file_path));
            AllGISData allGISData = kryo.readObject(input, AllGISData.class);
            input.close();

            return allGISData;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static CovidCsseJhu loadCasesDataKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(int.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.setReferences(true);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.DailyConfirmedCases.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.Country.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.State.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.County.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusTract.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.City.class);
        kryo.register(java.time.ZonedDateTime.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Input input;
        try {
            input = new Input(new FileInputStream(passed_file_path));
            CovidCsseJhu covidCsseJhu = kryo.readObject(input, CovidCsseJhu.class);
            input.close();

            return covidCsseJhu;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void initSafegraph() {
        safegraph = new Safegraph();
        safegraph.initAllPatternsAllPlaces();
        safegraph.setDatasetTemplate();
    }

}
