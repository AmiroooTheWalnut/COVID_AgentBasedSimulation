package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Engines.PythonEvaluationEngine;
import COVID_AgentBasedSimulation.Model.Engines.JavaEvaluationEngine;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentTemplate;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.BehaviorScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.JavaScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.PythonScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.Scenario;
import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.AllSafegraphPlaces;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.DwellTime;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.LongIntTuple;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Safegraph;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces;
import COVID_AgentBasedSimulation.Model.FACS_CHARM_compatible.RootFACS;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.RootArtificial;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import COVID_AgentBasedSimulation.Model.Structure.SupplementaryCaseStudyData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
//import esmaieeli.gisFastLocationOptimization.Simulation.VectorToPolygon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
//import lombok.Getter;
//import lombok.Setter;
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
public class MainModel extends Dataset {

    public static final long softwareVersion = 1L;
    static final long serialVersionUID = softwareVersion;

    public boolean isArtificialExact = false;

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
    public int simulationDelayTime = -1;
    public boolean isFastForward = false;

    public int currentMonth = -1;

    public boolean isResultSavedAtTheEnd = false;

    public boolean isStartBySafegraph = false;

    public transient int numCPUs = 1;

    public boolean isPause = false;
    public boolean isRunning = false;
    public boolean isReadyForBatchRun = true;

    public int newSimulationDelayTime = -2;

    public Scenario scenario = new Scenario();

    public transient long startTimeNanoSecond;

    public double sparsifyFraction = 1;
    public int lastMonthLoaded;//NOT USED
    public int lastYearLoaded;//NOT USED

    public String testPathB;//For BATCH RUNNING
    public String testPathName;//For BATCH RUNNING
    public int batchCounter = 0;//For BATCH RUNNING
    public ArrayList<String> runs = new ArrayList();//For BATCH RUNNING

//    private Thread fastForwardthread;
    public ExecutorService fastForwardPool = Executors.newSingleThreadExecutor();

    public ExecutorService agentEvalPool;

    public ExecutorService preprocessEvalPool;

    public ExecutorService groupInteractionEvalPool;

    public String datasetDirectory = "." + File.separator + "datasets";

    public boolean isDebugging = true;

    public transient double elapsed;

    public boolean isBatchRun = false;

    public void startScriptEngines() {
        javaEvaluationEngine = new JavaEvaluationEngine(this);
        pythonEvaluationEngine = new PythonEvaluationEngine(this);//MAY NEED TO BE STOPPED BECAUSE OF LOCAL SERVER STRUGGLE WITH PROFILER
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
                if (scsd.vDCells != null) {
                    for (int i = 0; i < scsd.vDCells.size(); i++) {
                        scsd.vDCells.get(i).cBGsInvolved = new ArrayList();
                        float avgLat = 0;
                        float avgLon = 0;
                        int counter = 0;
                        for (int j = 0; j < scsd.vDCells.get(i).cBGsIDsInvolved.size(); j++) {
                            CensusBlockGroup cbg = allGISData.findCensusBlockGroup(scsd.vDCells.get(i).cBGsIDsInvolved.get(j));
                            if (cbg == null) {
                                cbg = city.findCBG(scsd.vDCells.get(i).cBGsIDsInvolved.get(j));
                                if (cbg == null) {
                                    System.out.println("SVERE ERROR WHILE CONNECTING SUPPLEMENTARY DATA: CBG IS NULL!");
                                } else {
                                    avgLat = avgLat + cbg.lat;
                                    avgLon = avgLon + cbg.lon;
                                    counter += 1;
                                    scsd.vDCells.get(i).cBGsInvolved.add(cbg);
                                    scsd.vDCells.get(i).population += cbg.population * scsd.vDCells.get(i).cBGsPercentageInvolved.get(j);
                                }
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
                }
                if (scsd.cBGVDCells != null) {
                    for (int i = 0; i < scsd.cBGVDCells.size(); i++) {
                        scsd.cBGVDCells.get(i).cBGsInvolved = new ArrayList();
                        float avgLat = 0;
                        float avgLon = 0;
                        int counter = 0;
                        for (int j = 0; j < scsd.cBGVDCells.get(i).cBGsIDsInvolved.size(); j++) {
                            CensusBlockGroup cbg = allGISData.findCensusBlockGroup(scsd.cBGVDCells.get(i).cBGsIDsInvolved.get(j));
                            if (cbg == null) {
                                cbg = city.findCBG(scsd.cBGVDCells.get(i).cBGsIDsInvolved.get(j));
                                if (cbg == null) {
                                    cbg = city.findCBG(scsd.cBGVDCells.get(i).cBGsIDsInvolved.get(j));
                                    if (cbg == null) {
                                        System.out.println("SEVERE ERROR WHILE CONNECTING SUPPLEMENTARY DATA: CBG IS NULL!");
                                    } else {
                                        avgLat = avgLat + cbg.lat;
                                        avgLon = avgLon + cbg.lon;
                                        counter += 1;
                                        scsd.cBGVDCells.get(i).cBGsInvolved.add(cbg);
                                        scsd.cBGVDCells.get(i).population += cbg.population * scsd.cBGVDCells.get(i).cBGsPercentageInvolved.get(j);
                                    }
                                } else {
                                    avgLat = avgLat + cbg.lat;
                                    avgLon = avgLon + cbg.lon;
                                    counter += 1;
                                    scsd.cBGVDCells.get(i).cBGsInvolved.add(cbg);
                                    scsd.cBGVDCells.get(i).population += cbg.population * scsd.cBGVDCells.get(i).cBGsPercentageInvolved.get(j);
                                }
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
                }
                if (scsd.tessellations != null) {
                    for (int i = 0; i < scsd.tessellations.size(); i++) {
                        for (int j = 0; j < scsd.tessellations.get(i).cells.size(); j++) {
                            scsd.tessellations.get(i).cells.get(j).cBGsInvolved = new ArrayList();
                            float avgLat = 0;
                            float avgLon = 0;
                            int counter = 0;
                            for (int k = 0; k < scsd.tessellations.get(i).cells.get(j).cBGsIDsInvolved.size(); k++) {
                                CensusBlockGroup cbg = allGISData.findCensusBlockGroup(scsd.tessellations.get(i).cells.get(j).cBGsIDsInvolved.get(k));
                                if (cbg == null) {
                                    cbg = city.findCBG(scsd.tessellations.get(i).cells.get(j).cBGsIDsInvolved.get(k));
                                    if (cbg == null) {
                                        System.out.println("SEVERE ERROR WHILE CONNECTING SUPPLEMENTARY DATA: CBG IS NULL!");
                                    } else {
                                        avgLat = avgLat + cbg.lat;
                                        avgLon = avgLon + cbg.lon;
                                        counter += 1;
                                        scsd.tessellations.get(i).cells.get(j).cBGsInvolved.add(cbg);
                                        scsd.tessellations.get(i).cells.get(j).population += cbg.population * scsd.tessellations.get(i).cells.get(j).cBGsPercentageInvolved.get(k);
                                    }
                                } else {
                                    avgLat = avgLat + cbg.lat;
                                    avgLon = avgLon + cbg.lon;
                                    counter += 1;
                                    scsd.tessellations.get(i).cells.get(j).cBGsInvolved.add(cbg);
                                    scsd.tessellations.get(i).cells.get(j).population += cbg.population * scsd.tessellations.get(i).cells.get(j).cBGsPercentageInvolved.get(k);
                                }
                            }
                            scsd.tessellations.get(i).cells.get(j).lat = avgLat / ((float) counter);
                            scsd.tessellations.get(i).cells.get(j).lon = avgLon / ((float) counter);
                        }
                    }
                }
                city.vDCells = scsd.vDCells;
                city.cBGVDCells = scsd.cBGVDCells;
                city.cBGPolygons = scsd.cBGPolygons;
                city.vDPolygons = scsd.vDPolygons;
                city.cBGVDPolygons = scsd.cBGVDPolygons;
                city.cBGRegionLayer = scsd.cBGRegionImageLayer;
                city.vDRegionLayer = scsd.vDRegionImageLayer;
                city.cBGVDRegionLayer = scsd.cBGVDRegionImageLayer;
                city.tessellations = scsd.tessellations;
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
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon.class);
        kryo.register(de.fhpotsdam.unfolding.geo.Location.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.Tessellation.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.TessellationCell.class);
        kryo.register(double[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(int[][].class);
        kryo.register(boolean[][].class);
        kryo.register(boolean[].class);
        kryo.register(java.util.HashMap.class);
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
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon.class);
        kryo.register(de.fhpotsdam.unfolding.geo.Location.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.Tessellation.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.TessellationCell.class);
        kryo.register(double[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(int[][].class);
        kryo.register(boolean[][].class);
        kryo.register(boolean[].class);
        kryo.register(java.util.HashMap.class);
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

    public void initAgentBasedModel(boolean isStartScriptingEngine) {
        ABM = new AgentBasedModel(this);
        if (isStartScriptingEngine == true) {
            startScriptEngines();
        }
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

    public void initModelHardCoded(boolean isRunFromGUI, boolean isParallelLoadingData, boolean isParallelBehaviorEvaluation, int numResidents, int numRegions, int numCPUs, boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex, int fixedNumInfected, int numMinutesPerIterate) {
        isResultSavedAtTheEnd = false;
        int month = ABM.startTime.getMonthValue();
        currentMonth = month;
        String monthString = String.valueOf(month);
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        String dateName = ABM.startTime.getYear() + "_" + monthString;
        safegraph.clearPatternsPlaces();
        System.gc();
        safegraph.loadPatternsPlacesSet(datasetDirectory, dateName, allGISData, ABM.studyScope, isParallelLoadingData, numCPUs);

//        ABM.agents = new CopyOnWriteArrayList();
        ABM.agentsRaw = new ArrayList(numResidents);

        ABM.currentTime = ABM.startTime;

//        ABM.rootAgent = ABM.makeRootAgentHardCoded();
        ABM.root = new Root(this);

        //\/\/\/ get the total travels for the month from SafeGraph
        for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.size(); i++) {
            ABM.root.calcCumulativeMonthWeekHour(safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED
            ABM.root.numRealTravels = ABM.root.numRealTravels + safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).sumVisitsByDayOfMonth;
        }
        //^^^

        ABM.root.numAgents = numResidents;

        if (scenario.scenarioName.equals("CBG")) {
            ABM.root.constructor(this, numResidents, "CBG", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("VDFMTH")) {
            ABM.root.constructor(this, numResidents, "VDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("CBGVDFMTH")) {
            ABM.root.constructor(this, numResidents, "CBGVDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("AVDFMTH")) {
            ABM.root.constructor(this, numResidents, "AVDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("RMCBG")) {
            ABM.root.constructor(this, numResidents, "RMCBG", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("VDFNC")) {
            String[] temp = scenario.scenarioName.split("_");
            int numCells = Integer.valueOf(temp[1]);
            ABM.root.constructor(this, numResidents, "VDFNC", numCells, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("OVD")) {
            ABM.root.constructor(this, numResidents, "OVD", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        }
        ABM.measureHolder.initializeMeasures((Scope) (ABM.studyScopeGeography), ABM.root.pOIs);

//        if (scenario.equals("CBG")) {
//            ((Root) (ABM.rootAgent)).constructorCBG(this, sparsifyFraction);
//        } else if (scenario.equals("VD")) {
//            ((Root) (ABM.rootAgent)).constructorVD(this);
//        } else if (scenario.equals("CBGVD")) {
//            ((Root) (ABM.rootAgent)).constructorCBGVD(this);
//        } else if (scenario.equals("ABSVD")) {
//            ((Root) (ABM.rootAgent)).constructorABSVD(this);
//        }
        int passingNumCPU;
        if (isParallelBehaviorEvaluation == true) {
            passingNumCPU = numCPUs;
        } else {
            passingNumCPU = 1;
        }
//        pollBarrier = new CyclicBarrier(passingNumCPU);
        resetTimerTask(isRunFromGUI, passingNumCPU, true, isInfectCBGOnly, numMinutesPerIterate);
    }

    public void initModelArtificial(boolean isRunFromGUI, boolean isParallelLoadingData, boolean isParallelBehaviorEvaluation, int numResidents, int numRegions, int numCPUs, boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex, int numNoTessellation, int fixedNumInfected, int numMinutesPerIterate) {
        isResultSavedAtTheEnd = false;
        int month = ABM.startTime.getMonthValue();
        currentMonth = month;
        String monthString = String.valueOf(month);
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        String dateName = ABM.startTime.getYear() + "_" + monthString;
        safegraph.clearPatternsPlaces();
        System.gc();
        safegraph.loadPatternsPlacesSet(datasetDirectory, dateName, allGISData, ABM.studyScope, isParallelLoadingData, numCPUs);

        //ABM.agents = new CopyOnWriteArrayList();
        ABM.agentsRaw = new ArrayList(numResidents);
        ABM.currentTime = ABM.startTime;

//        ABM.rootAgent = ABM.makeRootAgentHardCoded();
        ABM.root = new RootArtificial(this);
//        System.out.println(ABM.agentsRaw.get(2314235));//TEST AN ERROR!
        if (ABM.exactSimGeoData.length() == 0) {
            System.out.println("EXACT GEOGRAPHY IS MISSING!");
            System.out.println("SIMULATION STOPPED!");
            return;
        }
        ABM.loadExactGeoData((RootArtificial) (ABM.root));

        //\/\/\/ get the total travels for the month from SafeGraph
        for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.size(); i++) {
            ABM.root.calcCumulativeMonthWeekHour(safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED
            ABM.root.numRealTravels = ABM.root.numRealTravels + safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).sumVisitsByDayOfMonth;
        }
        //^^^

        ABM.root.numAgents = numResidents;
        ((RootArtificial) (ABM.root)).numNoTessellation = numNoTessellation;

        if (scenario.scenarioName.equals("CBG")) {
            ABM.root.constructor(this, numResidents, "CBG", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("VDFMTH")) {
            ABM.root.constructor(this, numResidents, "VDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("CBGVDFMTH")) {
            ABM.root.constructor(this, numResidents, "CBGVDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("VD_CBG")) {
            ABM.root.constructor(this, numResidents, "VD_CBG", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("VD_CBGVD")) {
            ABM.root.constructor(this, numResidents, "VD_CBGVD", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("AVDFMTH")) {
            ABM.root.constructor(this, numResidents, "AVDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("RMCBG")) {
            ABM.root.constructor(this, numResidents, "RMCBG", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("Xmeans")) {
            ABM.root.constructor(this, numResidents, "Xmeans", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("VDFNC")) {
            String[] temp = scenario.scenarioName.split("_");
            int numCells = Integer.valueOf(temp[1]);
            ABM.root.constructor(this, numResidents, "VDFNC", numCells, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("OVD")) {
            ABM.root.constructor(this, numResidents, "OVD", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("noTessellation")) {
            ABM.root.constructor(this, numResidents, "noTessellation", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        }
        ABM.measureHolder.initializeMeasures((Scope) (ABM.studyScopeGeography), ABM.root.pOIs);

//        if (scenario.equals("CBG")) {
//            ((Root) (ABM.rootAgent)).constructorCBG(this, sparsifyFraction);
//        } else if (scenario.equals("VD")) {
//            ((Root) (ABM.rootAgent)).constructorVD(this);
//        } else if (scenario.equals("CBGVD")) {
//            ((Root) (ABM.rootAgent)).constructorCBGVD(this);
//        } else if (scenario.equals("ABSVD")) {
//            ((Root) (ABM.rootAgent)).constructorABSVD(this);
//        }
        int passingNumCPU;
        if (isParallelBehaviorEvaluation == true) {
            passingNumCPU = numCPUs;
        } else {
            passingNumCPU = 1;
        }
//        pollBarrier = new CyclicBarrier(passingNumCPU);
        resetTimerTask(isRunFromGUI, passingNumCPU, true, isInfectCBGOnly, numMinutesPerIterate);
    }

    public void initModelHardCodedFACS(boolean isRunFromGUI, boolean isParallelLoadingData, boolean isParallelBehaviorEvaluation, int numResidents, int numRegions, int numCPUs, boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex, int fixedNumInfected, int numMinutesPerIterate) {
        isResultSavedAtTheEnd = false;
        int month = ABM.startTime.getMonthValue();
        currentMonth = month;
        String monthString = String.valueOf(month);
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        String dateName = ABM.startTime.getYear() + "_" + monthString;
        safegraph.clearPatternsPlaces();
        System.gc();
//        safegraph.loadPatternsPlacesSet(datasetDirectory, dateName, allGISData, ABM.studyScope, isParallelLoadingData, numCPUs);
        safegraph.allPatterns.monthlyPatternsList = new ArrayList();
        safegraph.allSafegraphPlaces = new AllSafegraphPlaces();
        safegraph.allSafegraphPlaces.monthlySafegraphPlacesList = new ArrayList();
        safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.add(new SafegraphPlaces());
        safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(0).placesRecords=new ArrayList();
        City city = (City) (ABM.studyScopeGeography);
        Patterns patterns = new Patterns();
        patterns.patternRecords = new ArrayList();
        allGISData.readBrentPolygons(city);

        try {
            FileReader filereader;
            filereader = new FileReader("./datasets/brent_buildings.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> allData = csvReader.readAll();
            for (int i = 1; i < allData.size(); i++) {
//                System.out.println("I: " + i);
                if (allData.get(i)[0].equals("house")) {
                    continue;
                }

                PatternsRecordProcessed r = new PatternsRecordProcessed();
                r.raw_visit_counts = Integer.parseInt(allData.get(i)[3]);
                r.raw_visitor_counts= (int)(r.raw_visit_counts*(Math.random()*0.6+0.2));
                r.placeKey = String.valueOf(i);
                SafegraphPlace p = new SafegraphPlace();
                p.lat = Float.parseFloat(allData.get(i)[1]);
                p.lon = Float.parseFloat(allData.get(i)[2]);
                p.buldingLevels = 1;
                p.landArea = (int) (Math.random() * 400 + 50);
                if (allData.get(i)[0].equals("hospital")) {
                    p.naics_code = 6221;
                } else if (allData.get(i)[0].equals("school")) {
                    p.naics_code = 61;
                } else if (allData.get(i)[0].equals("office")) {
                    p.naics_code = 561;
                } else if (allData.get(i)[0].equals("leisure")) {
                    p.naics_code = 71;
                } else if (allData.get(i)[0].equals("park")) {
                    p.naics_code = 712190;
                } else if (allData.get(i)[0].equals("supermarket")) {
                    p.naics_code = 4451;
                } else if (allData.get(i)[0].equals("shopping")) {
                    p.naics_code = 45;
                } else if (allData.get(i)[0].equals("house")) {
                    continue;
                }
//                boolean isCBGFound = false;
//                for (int m = 0; m < city.censusTracts.size(); m++) {
                for (int n = 0; n < city.censusTracts.get(0).censusBlocks.size(); n++) {
                    GeometryFactory geomFactory = new GeometryFactory();
                    Point point = geomFactory.createPoint(new Coordinate(p.lat, p.lon));
                    if (city.censusTracts.get(0).censusBlocks.get(n).shape.get(0).covers(point)) {
                        p.censusBlock = city.censusTracts.get(0).censusBlocks.get(n);
                        r.poi_cbg=p.censusBlock.id;
//                            isCBGFound = true;
                        break;
                    }
                }
//                    if (isCBGFound == true) {
//                        break;
//                    }
//                }
                p.placeKey=String.valueOf(i);
                r.place = p;
                safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(0).placesRecords.add(p);
                r.bucketed_dwell_times = new ArrayList();
                DwellTime dt1 = new DwellTime();
                dt1.dwellDuration = new short[2];
                dt1.dwellDuration[0] = 1;
                dt1.dwellDuration[1] = 5;
                dt1.number = (int) (Math.random() * 10);
                DwellTime dt2 = new DwellTime();
                dt2.dwellDuration = new short[2];
                dt2.dwellDuration[0] = 5;
                dt2.dwellDuration[1] = 10;
                dt2.number = (int) (Math.random() * 20);
                DwellTime dt3 = new DwellTime();
                dt3.dwellDuration = new short[2];
                dt3.dwellDuration[0] = 10;
                dt3.dwellDuration[1] = 20;
                dt3.number = (int) (Math.random() * 30);
                DwellTime dt4 = new DwellTime();
                dt4.dwellDuration = new short[2];
                dt4.dwellDuration[0] = 20;
                dt4.dwellDuration[1] = 60;
                dt4.number = (int) (Math.random() * 30);
                DwellTime dt5 = new DwellTime();
                dt5.dwellDuration = new short[2];
                dt5.dwellDuration[0] = 60;
                dt5.dwellDuration[1] = 240;
                dt5.number = (int) (Math.random() * 10);

                r.bucketed_dwell_times.add(dt1);
                r.bucketed_dwell_times.add(dt2);
                r.bucketed_dwell_times.add(dt3);
                r.bucketed_dwell_times.add(dt4);
                r.bucketed_dwell_times.add(dt5);
                r.visitor_home_cbgs = new ArrayList();
                r.visitor_daytime_cbgs = new ArrayList();
//                for (int m = 0; m < city.censusTracts.size(); m++) {
                for (int n = 0; n < city.censusTracts.get(0).censusBlocks.size(); n++) {
                    float cBGLat = city.censusTracts.get(0).censusBlocks.get(n).lat;
                    float cBGLon = city.censusTracts.get(0).censusBlocks.get(n).lon;
                    float pLat = p.lat;
                    float pLon = p.lon;
                    float dist = (float) (Math.sqrt(Math.pow(cBGLat - pLat, 2) + Math.pow(cBGLon - pLon, 2)));
//                    System.out.println("DISTS: "+(int)(dist*100));
                    LongIntTuple pairHome = new LongIntTuple(city.censusTracts.get(0).censusBlocks.get(n).id, (int) (dist * 100));
                    r.visitor_home_cbgs.add(pairHome);
                    LongIntTuple pairWork = new LongIntTuple(city.censusTracts.get(0).censusBlocks.get(n).id, (int)(Math.max(0, Math.random()-0.5*5+(int) (dist * 100))));
                    r.visitor_daytime_cbgs.add(pairWork);
                }
//                }
                patterns.patternRecords.add(r);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        safegraph.allPatterns.monthlyPatternsList.add(patterns);

        safegraph.connectPatternsAndPlaces(safegraph.allPatterns.monthlyPatternsList.get(0), safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(0), allGISData, false, numCPUs);

//        ABM.agents = new CopyOnWriteArrayList();
        ABM.agentsRaw = new ArrayList(numResidents);

        ABM.currentTime = ABM.startTime;

//        ABM.rootAgent = ABM.makeRootAgentHardCoded();
        ABM.root = new RootFACS(this);

        //\/\/\/ get the total travels for the month from SafeGraph
        for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.size(); i++) {
            ABM.root.calcCumulativeMonthWeekHour(safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED
            ABM.root.numRealTravels = ABM.root.numRealTravels + safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).sumVisitsByDayOfMonth;
        }
        //^^^

        ABM.root.numAgents = numResidents;

        if (scenario.scenarioName.equals("CBG")) {
            ABM.root.constructor(this, numResidents, "CBG", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("VDFMTH")) {
            ABM.root.constructor(this, numResidents, "VDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("CBGVDFMTH")) {
            ABM.root.constructor(this, numResidents, "CBGVDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("AVDFMTH")) {
            ABM.root.constructor(this, numResidents, "AVDFMTH", -1, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("RMCBG")) {
            ABM.root.constructor(this, numResidents, "RMCBG", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.startsWith("VDFNC")) {
            String[] temp = scenario.scenarioName.split("_");
            int numCells = Integer.valueOf(temp[1]);
            ABM.root.constructor(this, numResidents, "VDFNC", numCells, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        } else if (scenario.scenarioName.equals("OVD")) {
            ABM.root.constructor(this, numResidents, "OVD", numRegions, isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
        }
        ABM.measureHolder.initializeMeasures((Scope) (ABM.studyScopeGeography), ABM.root.pOIs);

//        if (scenario.equals("CBG")) {
//            ((Root) (ABM.rootAgent)).constructorCBG(this, sparsifyFraction);
//        } else if (scenario.equals("VD")) {
//            ((Root) (ABM.rootAgent)).constructorVD(this);
//        } else if (scenario.equals("CBGVD")) {
//            ((Root) (ABM.rootAgent)).constructorCBGVD(this);
//        } else if (scenario.equals("ABSVD")) {
//            ((Root) (ABM.rootAgent)).constructorABSVD(this);
//        }
        int passingNumCPU;
        if (isParallelBehaviorEvaluation == true) {
            passingNumCPU = numCPUs;
        } else {
            passingNumCPU = 1;
        }
//        pollBarrier = new CyclicBarrier(passingNumCPU);
        resetTimerTask(isRunFromGUI, passingNumCPU, true, isInfectCBGOnly, numMinutesPerIterate);
    }

    public void initModel(boolean isRunFromGUI, boolean isParallelLoadingData, boolean isParallelBehaviorEvaluation, int numCPUs, int numMinutesPerIterate) {
        isResultSavedAtTheEnd = false;
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
        safegraph.loadPatternsPlacesSet(datasetDirectory, dateName, allGISData, ABM.studyScope, isParallelLoadingData, numCPUs);

        //ABM.agents = new CopyOnWriteArrayList();
        ABM.agentsRaw = new ArrayList();

        ABM.currentTime = ABM.startTime;

        ABM.rootAgent = ABM.makeRootAgent();

        //\/\/\/ get the total travels for the month from SafeGraph
//        for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.size(); i++) {
//            ABM.rootAgent.calcCumulativeMonthWeekHour(safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED
//            ABM.rootAgent.numRealTravels = ABM.root.numRealTravels + safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).sumVisitsByDayOfMonth;
//        }
        //^^^
        pythonEvaluationEngine.saveAllPythonScripts(ABM.agentTemplates);
        if (ABM.rootAgent.myTemplate.constructor.isJavaScriptActive == true) {
            //myMainModel.javaEvaluationEngine.runScript(output.myTemplate.constructor.javaScript.script);

            javaEvaluationEngine.runParsedScript(ABM.rootAgent, ABM.rootAgent.myTemplate.constructor.javaScript.parsedScript);
        } else {
            pythonEvaluationEngine.runScript(ABM.rootAgent.myTemplate.constructor.pythonScript);
        }

        int passingNumCPU;
        if (isParallelBehaviorEvaluation == true) {
            passingNumCPU = numCPUs;
        } else {
            passingNumCPU = 1;
        }
//        pollBarrier = new CyclicBarrier(passingNumCPU);

//                currentEvaluatingAgent[0] = oldCurrentEvaluatingAgent[0];
//        if (ABM.rootAgent.myTemplate.agentTypeName.equals("Person")) {
//            System.out.println(ABM.rootAgent.myIndex);
//            System.out.println("!!!!");
//        }
//        ABM.rootAgent = new Agent(ABM.agentTemplates.get(0));
        resetTimerTask(isRunFromGUI, passingNumCPU, false, false, numMinutesPerIterate);

//        if (ABM.rootAgent.myTemplate.constructor.isJavaScriptActive == true) {
//
//            //javaEvaluationEngine.runScript(ABM.rootAgent.myTemplate.constructor.javaScript.script);
//            javaEvaluationEngine.runParsedScript(ABM.rootAgent, ABM.rootAgent.myTemplate.constructor.javaScript.parsedScript);
//        } else {
//            pythonEvaluationEngine.runScript(ABM.rootAgent.myTemplate.constructor.pythonScript);
//        }
    }

    public void resetTimerTask(boolean isRunFromGUI, int numCPUs, boolean isHardCoded, boolean isInfectCBGOnly, int numMinutesPerIterate) {
        runTask = new TimerTask() {
            @Override
            public void run() {
                iterate(isRunFromGUI, numCPUs, isHardCoded, isInfectCBGOnly, numMinutesPerIterate);
            }
        };
    }

    public void fastForward(boolean isRunFromGUI, boolean isParallel, int numCPUs, boolean isHardCoded, boolean isInfectCBGOnly, int numMinutesPerIterate) {

//        fastForwardthread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isPause == false) {
//                    iterate(numCPUs, isHardCoded);
////                    System.out.println(isPause);
//                }
//                isPause = false;
//                isRunning = false;
//            }
//        });
        if (isRunFromGUI == true) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    fastForwardPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            while (isPause == false) {
                                iterate(isRunFromGUI, numCPUs, isHardCoded, isInfectCBGOnly, numMinutesPerIterate);
//                    System.out.println(isPause);
                            }
                            isPause = false;
                            isRunning = false;
                        }
                    });
                    //\/\/\/ OLD DESIGN WITH THREADS
//                fastForwardthread.start();
                    //^^^ OLD DESIGN WITH THREADS
                }
            });
        } else {
            fastForwardPool.execute(new Runnable() {
                @Override
                public void run() {
                    while (isPause == false) {
                        iterate(isRunFromGUI, numCPUs, isHardCoded, isInfectCBGOnly, numMinutesPerIterate);
//                    System.out.println(isPause);
                    }
                    isPause = false;
                    isRunning = false;
                }
            });
        }

    }

    public void iterate(boolean isRunFromGUI, int numCPUs, boolean isHardCoded, boolean isInfectCBGOnly, int numMinutesPerIterate) {
        if (isResultSavedAtTheEnd == false) {
            if (ABM.currentTime.isEqual(ABM.endTime) || ABM.currentTime.isAfter(ABM.endTime)) {
                isRunning = false;
                long endTimeNanoSecond = System.nanoTime();
                elapsed = ((endTimeNanoSecond - startTimeNanoSecond) / 1000000000);
                System.out.println("ABM runtime (seconds): " + elapsed);
                System.out.println("Num real travels: " + ABM.root.numRealTravels);
                for (int i = 0; i < ABM.root.people.size(); i++) {
                    ABM.root.numTravels = ABM.root.numTravels + ABM.root.people.get(i).numTravels;
                }
                for (int i = 0; i < ABM.root.people.size(); i++) {
                    ABM.root.numContacts = ABM.root.numContacts + ABM.root.people.get(i).numContacts;
                }
                System.out.println("Num ABM travels: " + ABM.root.numTravels);
                pause();
                saveResult(ABM.root.regions, isInfectCBGOnly);
                if (isRunFromGUI == false) {
                    fastForwardPool.shutdown();
                    agentEvalPool.shutdown();
                    groupInteractionEvalPool.shutdown();
                    isReadyForBatchRun = true;
                    return;
                } else {
                    isReadyForBatchRun = true;
                    return;
                }
            }
        }
        //\/\/\/ DYNAMICALLY ADD NEW PATTERNS OF THE NEW MONTH AND GENERATE SCHEDULES
        int month = ABM.currentTime.getMonthValue();
        if (currentMonth != month) {
            String monthString = String.valueOf(month);
            if (monthString.length() < 2) {
                monthString = "0" + monthString;
            }
            String yearStr = String.valueOf(ABM.currentTime.getYear());
            safegraph.clearPatternsPlaces();
            System.gc();
            safegraph.requestDataset(datasetDirectory, allGISData, ABM.studyScope, yearStr, monthString, true, numCPUs);
            //\/\/\/ get the total travels for the month from SafeGraph
            for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.size(); i++) {
                ABM.root.calcCumulativeMonthWeekHour(safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED
                ABM.root.numRealTravels = ABM.root.numRealTravels + safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).sumVisitsByDayOfMonth;
            }
            //^^^
            currentMonth = month;

            ABM.root.generateSchedules(this, ABM.root.regionType, ABM.root.regions);
        }
        //^^^ DYNAMICALLY ADD NEW PATTERNS OF THE NEW MONTH AND GENERATE SCHEDULES
        ABM.evaluateAllAgents(numCPUs, isHardCoded);
        ABM.currentTime = ABM.currentTime.plusMinutes(numMinutesPerIterate);
    }

    public void resume(boolean isRunFromGUI, boolean isParallel, int numCPUs, boolean isHardCoded, boolean isInfectCBGOnly, int numMinutesPerIterate) {
        if (agentEvalPool == null) {
            agentEvalPool = Executors.newFixedThreadPool(numCPUs);
        }
        if (groupInteractionEvalPool == null) {
            groupInteractionEvalPool = Executors.newFixedThreadPool(numCPUs);
        }
        if (isHardCoded == true) {
            if (simulationDelayTime > -1) {
                simulationTimer = new Timer();
                resetTimerTask(isRunFromGUI, numCPUs, isHardCoded, isHardCoded, numMinutesPerIterate);
                simulationTimer.schedule(runTask, 0, simulationDelayTime);
            } else {
                fastForward(isRunFromGUI, isParallel, numCPUs, isHardCoded, isInfectCBGOnly, numMinutesPerIterate);
            }
            isRunning = true;
        } else {
            if (simulationDelayTime > -1) {
                simulationTimer = new Timer();
                resetTimerTask(isRunFromGUI, numCPUs, isHardCoded, isHardCoded, numMinutesPerIterate);
                simulationTimer.schedule(runTask, 0, simulationDelayTime);
            } else {
                fastForward(isRunFromGUI, isParallel, numCPUs, isHardCoded, isInfectCBGOnly, numMinutesPerIterate);
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
            while (isRunning == true) {
                try {
                    System.out.println("Waiting to pause fastforward run!");
                    Thread.sleep(1 * 100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            isPause = true;
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

//    public void debugSaveBoundaries(boolean[][] input) {
//        int width = input.length;
//        int height = input[0].length;
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if(input[x][y]==true){
//                    image.setRGB(x, y, Color.black.getRGB());
//                }else{
//                    image.setRGB(x, y, Color.white.getRGB());
//                }
//            }
//        }
//        File outputFile = new File("DEBUGING" + ".png");
//        try {
//            ImageIO.write(image, "png", outputFile);
//        } catch (IOException ex) {
//            Logger.getLogger(VectorToPolygon.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void saveResult(ArrayList<Region> regions, boolean isInfectCBGOnly) {
//        debugSaveBoundaries(ABM.root.regionsLayer.imageBoundaries);
        String directoryPath = "projects" + File.separator + ABM.filePath.substring(ABM.filePath.lastIndexOf(File.separator) + 1, ABM.filePath.length());
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS");
        Date date = new Date();
        if (scenario.scenarioName.contains("Xmeans")) {
            String[] strs = scenario.scenarioName.split("_");
            String scName = "";
            try {
                int v = Integer.parseInt(strs[strs.length - 1]);
                for (int i = 0; i < strs.length - 1; i++) {
                    scName = scName + strs[i];
                }
                scenario.scenarioName = scName;
            } catch (NumberFormatException nfe) {
            }
            scenario.scenarioName = scenario.scenarioName + "_" + regions.size();
        }
        String testPath;
        if (ABM.isMatching == true) {
            testPath = "projects" + File.separator + ABM.filePath.substring(ABM.filePath.lastIndexOf(File.separator) + 1, ABM.filePath.length()) + File.separator + formatter.format(date) + "_NumPeople_" + ABM.root.people.size() + "_" + scenario.scenarioName + "_MATCHING";
        } else {
            testPath = "projects" + File.separator + ABM.filePath.substring(ABM.filePath.lastIndexOf(File.separator) + 1, ABM.filePath.length()) + File.separator + formatter.format(date) + "_NumPeople_" + ABM.root.people.size() + "_" + scenario.scenarioName;
        }

        if (isBatchRun == true) {
            testPathB = testPath;
            testPathName = formatter.format(date) + "_NumPeople_" + ABM.root.people.size() + "_" + scenario.scenarioName;
            testPath = testPath + "_B" + batchCounter;
            batchCounter = batchCounter + 1;
            runs.add(testPath);
        }
        File testDirectory = new File(testPath);
        if (!testDirectory.exists()) {
            testDirectory.mkdirs();
        }

        if (ABM.isSaveHistoricalRun == true) {
            HistoricalRun historicalRun = new HistoricalRun();
            historicalRun.regions = regions;
            historicalRun.startTime = ABM.startTime;
            historicalRun.endTime = ABM.endTime;
            historicalRun.regionsLayer = ABM.root.regionsLayer;
//        historicalRun.saveHistoricalRunJson("./projects/" + ABM.filePath + "/" + formatter.format(date)+"/data.json");
            HistoricalRun.saveHistoricalRunKryo(testPath + File.separator + "data", historicalRun);
        }

//        for (int i = 0; i < historicalRun.regions.size(); i++) {
//            for (int j = 0; j < historicalRun.regions.get(i).hourlyRegionSnapshot.size(); j++) {
//                int val = regions.get(i).hourlyRegionSnapshot.get(j).IS;
//                if(val>0){
//                    System.out.println("INFECTED_SYM");
//                    System.out.println("Region: "+i);
//                    System.out.println("time: "+j);
//                }
//                val = regions.get(i).hourlyRegionSnapshot.get(j).IAS;
//                if(val>0){
//                    System.out.println("INFECTED_ASYM");
//                    System.out.println("Region: "+i);
//                    System.out.println("time: "+j);
//                }
//            }
//        }
        ABM.root.writeDailyInfection(testPath + File.separator + "infectionReport");
        ABM.root.writeDailyMobility(testPath + File.separator + "mobilityReport");
        ABM.root.writeSimulationSummary(testPath + File.separator + "simulationSummary");
//        if (isInfectCBGOnly == true) {
        ABM.root.writeConvertedToCBGInfection(testPath + File.separator + "CBGInf");
        ABM.root.writeAllMobilityCounts(testPath + File.separator + "travelToAllPOIs");
//        }
        ABM.root.writeTotalContacts(testPath + File.separator + "rawContactData");
        ABM.measureHolder.writeReports(testPath + File.separator);
        if (ABM.root instanceof RootArtificial) {
            RootArtificial root = (RootArtificial) (ABM.root);
            if (root.isTessellationBuilt == true) {
                root.writeScheduleSimilarityArtifitial(testPath + File.separator + "regionMobilitySimilarity");
//                root.writeScheduleSimilarityArtifitialDebug(root.scheduleListExactArray,root.peopleNoTessellation);
            }
            //BROKEN FOR NOW, THIS FEATURE WILL BE REMOVED
//            root.runClusterers(testPath);

        }
        isResultSavedAtTheEnd = true;
    }

    public void initSafegraph() {
        safegraph = new Safegraph();
        safegraph.initAllPatternsAllPlaces();
        safegraph.setDatasetTemplate();
    }

    public static int binarySearchCumulative(double value, ArrayList<Double> input) {
        int stepSize = input.size() / 2;
        int index = stepSize;
        if (stepSize < 8) {
            for (int i = 0; i < input.size(); i++) {
                if (input.get(i) > value) {
                    return i;
                }
            }
        } else {
            while (stepSize > 1) {
                stepSize = stepSize / 2;
                if (value < input.get(index)) {
                    index = index - stepSize;
                } else {
                    index = index + stepSize;
                }
            }
            if (index < 6) {
                for (int i = 0; i < 7; i++) {
                    if (value < input.get(i)) {
                        index = i;
                        break;
                    }
                }
            } else if (index > input.size() - 6) {
                for (int i = input.size() - 7; i < input.size(); i++) {
                    if (value < input.get(i)) {
                        index = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < 6; i++) {
                    if (value < input.get(index - 3 + i)) {
                        index = index - 3 + i;
                        break;
                    }
                }
            }

        }

        return index;
    }

    public static int binarySearchCumulative(float value, ArrayList<Float> input) {
        int stepSize = input.size() / 2;
        int index = stepSize;
        if (stepSize < 8) {
            for (int i = 0; i < input.size(); i++) {
                if (input.get(i) > value) {
                    return i;
                }
            }
        } else {
            while (stepSize > 1) {
                stepSize = stepSize / 2;
                if (value < input.get(index)) {
                    index = index - stepSize;
                } else {
                    index = index + stepSize;
                }
            }
            if (index < 6) {
                for (int i = 0; i < 7; i++) {
                    if (value < input.get(i)) {
                        index = i;
                        break;
                    }
                }
            } else if (index > input.size() - 6) {
                for (int i = input.size() - 7; i < input.size(); i++) {
                    if (value < input.get(i)) {
                        index = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < 6; i++) {
                    if (value < input.get(index - 3 + i)) {
                        index = index - 3 + i;
                        break;
                    }
                }
            }

        }

        return index;
    }

    public static int binarySearchCumulative(float value, float[] input) {
        int stepSize = input.length / 2;
        int index = stepSize;
        if (stepSize < 8) {
            for (int i = 0; i < input.length; i++) {
                if (input[i] > value) {
                    return i;
                }
            }
        } else {
            while (stepSize > 1) {
                stepSize = stepSize / 2;
                if (value < input[index]) {
                    index = index - stepSize;
                } else {
                    index = index + stepSize;
                }
            }
            if (index < 6) {
                for (int i = 0; i < 7; i++) {
                    if (value < input[i]) {
                        index = i;
                        break;
                    }
                }
            } else if (index > input.length - 6) {
                for (int i = input.length - 7; i < input.length; i++) {
                    if (value < input[i]) {
                        index = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < 6; i++) {
                    if (value < input[index - 3 + i]) {
                        index = index - 3 + i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    public Safegraph getSafegraph() {
        return safegraph;
    }

    public AgentBasedModel getABM() {
        return ABM;
    }
}
